package org.bluebird.Extractor.Languages.Java;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bluebird.Extractor.CommentsExtractor;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtils.Java.JavaLexer;
import org.bluebird.LanguagesUtils.Java.JavaParser;
import org.bluebird.LanguagesUtils.Java.JavaParserBaseListener;
import org.bluebird.Utils.ClocCounter;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class JavaListener extends JavaParserBaseListener {

    private TokenStream tokens;
    private String modifiers = "default";
    private String modifiersClassOrInterface = "default";
    private Stack<Integer> ruleIndex;
    private CommentsExtractor commentsExtractor;
    private String pack;
    private int lineCount;
    private ArrayList<String> javaDoc;
    private Stack<Integer> stackerClass;
    private String checkInnerClass = "";

    JavaListener(JavaParser parser, BufferedTokenStream tokenStream) {
        this.tokens = parser.getTokenStream();
        this.ruleIndex = new Stack<>();
        this.commentsExtractor = new CommentsExtractor(tokenStream);
        this.stackerClass = new Stack<>();
    }

    /**
     * Inicia a extração do arquivo Java e pega todos comentarios do arquivo
     *
     * @param ctx Entidade da Parser Tree
     */
    public void enterCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        commentsExtractor.getAllComments(ctx, JavaLexer.COMMENTS_CHANNEL);
        commentsExtractor.getJavaDoc(ctx);
        this.ruleIndex.push(1);
    }

    /**
     * Termina a extração do arquivo Java
     *
     * @param ctx Entidade da Parser Tree
     */
    public void exitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        commentsExtractor.associateComments(ctx);
    }


    /**
     * Extrai o acesso dos metodos, construtores e atributos
     *
     * @param ctx Entidade da Parser Tree
     */

    public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        String packageIdentifier = this.tokens.getText(ctx.qualifiedName());
        ruleIndex.push(ctx.getStart().getLine());
        packageIdentifier = packageIdentifier.replace(".", "/");
        this.pack = packageIdentifier;
        FileCreator.appendToVxlFile("\t<pack name=\"" + packageIdentifier + "\">\n");
    }

    public void exitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        ruleIndex.pop();
        FileCreator.appendToVxlFile("\t</pack>\n");
    }

    public void enterModifier(JavaParser.ModifierContext ctx) {
        try {
            String temp = this.tokens.getText(ctx.classOrInterfaceModifier());
            if (temp.equals("public") || temp.equals("private") || temp.equals("protected")) {
                this.modifiers = temp;
            }
        } catch (NullPointerException e) {

        }
    }

    /**
     * Extrai as classes
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        stackerClass.push(ctx.getStart().getLine());
        TerminalNode classIdentifier = ctx.IDENTIFIER();
        lineCount = ClocCounter.lineCount(ctx);
        javaDoc = commentsExtractor.associateJavaDoc(ctx.getStart().getLine());
        ruleIndex.push(ctx.getStart().getLine());
        checkInnerClass = innerClass();
        FileCreator.appendToVxlFile("\t\t<class name=\"" + pack + "/" + classIdentifier + ".java\"" + " intfc=\"n\" acess=\"" +
                this.modifiersClassOrInterface + "\" inn=\"" +  checkInnerClass + "\" loc=\"" + lineCount + "\">\n");
        addJavaDoc(javaDoc);
        //FileCreator.appendToVxlFile("\t\t\t <javaDoc cmmt=\"" + javaDoc + "\"></javaDoc>\n");
        this.modifiersClassOrInterface = "default";
        //javaDoc = "";
    }

    /**
     * Associa comentarios a classe
     *
     * @param ctx Entidade da Parser Tree
     */

    public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        stackerClass.pop();
        commentsExtractor.associateComments(ctx);
        ruleIndex.pop();
        FileCreator.appendToVxlFile("\t\t</class>\n");
    }

    public String innerClass () {
        if(stackerClass.size() > 1 ) {
            return "s";
        }
        return "n";
    }

    /**
     * Extrai os metodos
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        lineCount = ClocCounter.lineCount(ctx);
        javaDoc = commentsExtractor.associateJavaDoc(ctx.getStart().getLine());
        TerminalNode methodIdentifier = ctx.IDENTIFIER();
        String methodParamenters = this.tokens.getText(ctx.formalParameters());
        String typeMethod = this.tokens.getText(ctx.typeTypeOrVoid());

        methodParamenters = methodParamenters.replace("<", "&lt;");
        methodParamenters = methodParamenters.replace(">", "&gt;");
        typeMethod = typeMethod.replace("<", "&lt;");
        typeMethod = typeMethod.replace(">", "&gt;");

        FileCreator.appendToVxlFile("\t\t\t<mth name=\"" + methodIdentifier + methodParamenters + "\" type=\"" + typeMethod + "\" acess=\"" +
                this.modifiers + "\" loc=\"" + lineCount + "\">\n");
        addJavaDoc(javaDoc);
        //FileCreator.appendToVxlFile("\t\t\t <javaDoc cmmt=\"" + javaDoc + "\"></javaDoc>\n");

        this.modifiers = "default";
       // javaDoc = "";
    }

    /**
     * Associa comentarios aos metodos
     *
     * @param ctx Entidade da Parser Tree
     */
    public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        commentsExtractor.associateComments(ruleIndex.peek(), ctx);
        FileCreator.appendToVxlFile("\t\t\t</mth>\n");
    }

    /**
     * Extrai os atributos e seus respectivos tipos
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        lineCount = ClocCounter.lineCount(ctx);
        String fieldIdentifier = this.tokens.getText(ctx.variableDeclarators().variableDeclarator(0).variableDeclaratorId().getSourceInterval());
        String typeField = this.tokens.getText(ctx.typeType());

        typeField = typeField.replace("<", "&lt;");
        typeField = typeField.replace(">", "&gt;");

        if (verificaConstante(fieldIdentifier) == false) {
            FileCreator.appendToVxlFile("\t\t\t<field name=\"" + fieldIdentifier + "\" type=\"" + typeField + "\" acess=\"" + this.modifiers + "\" loc=\"" + lineCount + "\" ></field>\n");
        } else {
            FileCreator.appendToVxlFile("\t\t\t<const name=\"" + fieldIdentifier + "\" type=\"" + typeField + "\" acess=\"" + this.modifiers + "\" loc=\"" + lineCount +  "\" ></const>\n");
        }
        this.modifiers = "default";
    }

    public boolean verificaConstante(String constante) {
        if (constante.toUpperCase().equals(constante)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Extrai os construtores e seus respectivos parametros
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        String constructorParameters = this.tokens.getText(ctx.formalParameters());
        TerminalNode constructorIdentifier = ctx.IDENTIFIER();
        javaDoc = commentsExtractor.associateJavaDoc(ctx.getStart().getLine());
        constructorParameters = constructorParameters.replace("<", "&lt;");
        constructorParameters = constructorParameters.replace(">", "&gt;");

        FileCreator.appendToVxlFile("\t\t\t<constr name=\"" + constructorIdentifier + constructorParameters + "\" acess=\"" + this.modifiers + "\">\n");
        addJavaDoc(javaDoc);
        //FileCreator.appendToVxlFile("\t\t\t <javaDoc cmmt=\"" + javaDoc + "\"></javaDoc>\n");
        this.modifiers = "default";
        //javaDoc = "";
    }

    /**
     * Associa comentarios ao constructor
     *
     * @param ctx Entidade da Parser Tree
     */
    public void exitConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToVxlFile("\t\t\t</constr>\n");
    }

    /**
     * Extrai o enum
     *
     * @param ctx Entidade da Parser Tree
     */
    public void enterEnumDeclaration(JavaParser.EnumDeclarationContext ctx) {
        lineCount = ClocCounter.lineCount(ctx);
        javaDoc = commentsExtractor.associateJavaDoc(ctx.getStart().getLine());
        TerminalNode enumIdentifier = ctx.IDENTIFIER();

    FileCreator.appendToVxlFile("\t\t\t<enum name=\"" + enumIdentifier + "\" acess=\"" + this.modifiersClassOrInterface + "\" loc=\"" + lineCount + "\">\n");
        addJavaDoc(javaDoc);
   // FileCreator.appendToVxlFile("\t\t\t <javaDoc cmmt=\"" + javaDoc + "\"></javaDoc>\n");
        constEnum(ctx);
        //javaDoc = "";
        this.modifiersClassOrInterface = "default";
    }

    public void constEnum (JavaParser.EnumDeclarationContext ctx) {
        String constEnum = this.tokens.getText(ctx.enumConstants());
        String constantes[] = constEnum.split(",");


        for (int i = 0; i < constantes.length; i++) {
            String temp = constantes[i].strip();

            if (temp.contains("(")) {
               temp = temp.replaceAll("\\(.*\\)", "");
//               FileCreator.appendToVxlFile("\t\t\t<const name=\"" + temp + "\"></const>\n");
            }
            FileCreator.appendToVxlFile("\t\t\t<const name=\"" + temp + "\"></const>\n");
        }
    }


    public void exitEnumDeclaration(JavaParser.EnumDeclarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToVxlFile("\t\t\t</enum>\n");
    }

    /**
     * Extrai a interface
     *
     * @param ctx Entidade da Parser Tree
     */
    public void enterInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        TerminalNode interfaceIdentifier = ctx.IDENTIFIER();

        FileCreator.appendToVxlFile("\t\t\t<intfc name=\"" + interfaceIdentifier + ".java" + "\" acess=\"" + this.modifiersClassOrInterface + "\">\n");
        this.modifiersClassOrInterface = "default";
    }

    /**
     * Fecha tag da interface
     *
     * @param ctx Entidade da Parser Tree
     */
    public void exitInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        FileCreator.appendToVxlFile("\t\t\t</intfc>\n");

    }

    /**
     * Extrai os modificadores das classes e interfaces
     *
     * @param ctx Entidade da Parser Tree
     */
    public void enterTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        String temp = " ";
        for (int i = 0; i < ctx.classOrInterfaceModifier().size(); i++) {
            temp = this.tokens.getText(ctx.classOrInterfaceModifier(i));
        }
        if (temp.equals("public") || temp.equals("private") || temp.equals("protected")) {
            this.modifiersClassOrInterface = temp;
        }
    }


    /**
     * Extrai a variavel local do metodo
     *
     * @param ctx Entidade da Parser Tree
     */
    public void enterLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        String variableIdentifier = this.tokens.getText(ctx.variableDeclarators().variableDeclarator(0));
        variableIdentifier = breakString(variableIdentifier);
        String variableType = this.tokens.getText(ctx.typeType());

        variableType = variableType.replace("<", "&lt;");
        variableType = variableType.replace(">", "&gt;");

        FileCreator.appendToVxlFile("\t\t\t<lvar name=\"" + variableIdentifier + "\" type=\"" + variableType + "\"></lvar>\n");

    }

    /**
     * Metodo que tira a declaração das variaveis locais e deixa apenas o seu tipo e nome
     *
     * @param identifier entidade do Metodo enterLocalVariableDeclaration
     */
    public String breakString(String identifier) {
        String[] temp;
        for (int i = 0; i < identifier.length(); i++) {
            temp = identifier.split("=");
            identifier = temp[0];
        }
        return identifier;
    }

    public void addJavaDoc (List<String> listaJavaDoc) {
        String temp= "";
        if (listaJavaDoc.size() != 0) {
            for (String javaDoc : listaJavaDoc) {
                FileCreator.appendToVxlFile("\t\t\t <javaDoc cmmt=\"" + javaDoc + "\"></javaDoc>\n");
            }
        }
        else {
            FileCreator.appendToVxlFile("\t\t\t <javaDoc cmmt=\"" + temp + "\"></javaDoc>\n");
        }
    }
}
