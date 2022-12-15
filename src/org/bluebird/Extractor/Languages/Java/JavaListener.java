package org.bluebird.Extractor.Languages.Java;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bluebird.Extractor.CommentsExtractor;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtils.Java.JavaLexer;
import org.bluebird.LanguagesUtils.Java.JavaParser;
import org.bluebird.LanguagesUtils.Java.JavaParserBaseListener;
import org.bluebird.Utils.ClocCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class JavaListener extends JavaParserBaseListener {

    private TokenStream tokens;
    private String modifiers = "default";
    private String modifiersClassOrInterface = "default";
    private String modifiersAbstractFinal = "default"; // modificadores de classes e interfaces
    private String modifiersAF = "default"; // modificadores de métodos, fields e outras coisas
    private Stack<Integer> ruleIndex;
    private CommentsExtractor commentsExtractor;
    private String pack;
    private int lineCount;
    private ArrayList<String> javaDoc;
    private Stack<Integer> stackerClass;
    private String checkInnerClass = "";
    private ArrayList<String> comentario;
    private int lineCompile;


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
        lineCompile = ctx.start.getLine();
        this.ruleIndex.push(1);
    }

    /**
     * Termina a extração do arquivo Java
     *
     * @param ctx Entidade da Parser Tree
     */
    public void exitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToVxlFile("\t</pack>\n");
    }


    /**
     * Extrai o acesso dos metodos, construtores e atributos
     *
     * @param ctx Entidade da Parser Tree
     */

    public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        String packageIdentifier = this.tokens.getText(ctx.qualifiedName());
        ruleIndex.push(ctx.getStart().getLine());
        commentsExtractor.getCommentsEntidadeJava(ctx);
        packageIdentifier = packageIdentifier.replace(".", "/");
        this.pack = packageIdentifier;
        FileCreator.appendToVxlFile("\t<pack name=\"" + packageIdentifier + "\">\n");
    }

    public void exitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
        addComentario(comentario);
        //commentsExtractor.associateComments(ctx);
        ruleIndex.pop();
        //FileCreator.appendToVxlFile("\t</pack>\n");
    }

    public void enterModifier(JavaParser.ModifierContext ctx) {
        try {
            String temp = this.tokens.getText(ctx.classOrInterfaceModifier());
            if (temp.equals("public") || temp.equals("private") || temp.equals("protected")) {
                this.modifiers = temp;
            }
            if (temp.equals("abstract") || temp.equals("final") || temp.equals("static")) {
                this.modifiersAF = temp;
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
        commentsExtractor.getCommentsEntidadeJava(ctx);
        ruleIndex.push(ctx.getStart().getLine());
        //checkInnerClass = innerClass();
        String accessAndModifierClass = innerClass();
        if (accessAndModifierClass.equals("y")) {
            FileCreator.appendToVxlFile("\t\t<class name=\"" + pack + "/" + classIdentifier + ".java\"" + " intfc=\"n\" access=\"" + this.modifiers + "\" mod=\"" + this.modifiersAF + "\" inn=\"" + accessAndModifierClass + "\" loc=\"" + lineCount + "\">\n");
            addJavaDoc(javaDoc);
            this.modifiers = "default";
            this.modifiersAF = "default";
        }
        else {
            FileCreator.appendToVxlFile("\t\t<class name=\"" + pack + "/" + classIdentifier + ".java\"" + " intfc=\"n\" access=\"" + this.modifiersClassOrInterface + "\" mod=\"" + this.modifiersAbstractFinal + "\" inn=\"" + accessAndModifierClass + "\" loc=\"" + lineCount + "\">\n");
            addJavaDoc(javaDoc);
            this.modifiersClassOrInterface = "default";
            this.modifiersAbstractFinal = "default";
        }

    }

    /**
     * Associa comentarios a classe
     *
     * @param ctx Entidade da Parser Tree
     */

    public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
        addComentario(comentario);
        stackerClass.pop();
        ruleIndex.pop();
        FileCreator.appendToVxlFile("\t\t</class>\n");
    }

    public String innerClass () {
        if(stackerClass.size() > 1 ) {
            return "y";
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
        commentsExtractor.getCommentsEntidadeJava(ctx);
        TerminalNode methodIdentifier = ctx.IDENTIFIER();
        String methodParamenters = this.tokens.getText(ctx.formalParameters());
        String typeMethod = this.tokens.getText(ctx.typeTypeOrVoid());

        methodParamenters = methodParamenters.replace("<", "&lt;");
        methodParamenters = methodParamenters.replace(">", "&gt;");
        typeMethod = typeMethod.replace("<", "&lt;");
        typeMethod = typeMethod.replace(">", "&gt;");

        FileCreator.appendToVxlFile("\t\t\t<mth name=\"" + methodIdentifier + methodParamenters + "\" type=\"" + typeMethod + "\" access=\"" +
                this.modifiers + "\" mod=\"" + this.modifiersAF + "\" loc=\"" + lineCount + "\">\n");
        addJavaDoc(javaDoc);

        this.modifiers = "default";
        this.modifiersAF = "default";
    }

    /**
     * Associa comentarios aos metodos
     *
     * @param ctx Entidade da Parser Tree
     */
    public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
        addComentario(comentario);
        //commentsExtractor.associateComments(ruleIndex.peek(), ctx);
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

        if (!verificaConstante(fieldIdentifier)) {
            FileCreator.appendToVxlFile("\t\t\t<field name=\"" + fieldIdentifier + "\" type=\"" + typeField + "\" acess=\"" + this.modifiers + "\" mod=\"" + this.modifiersAF + "\" loc=\"" + lineCount + "\" ></field>\n");
        } else {
            FileCreator.appendToVxlFile("\t\t\t<const name=\"" + fieldIdentifier + "\" type=\"" + typeField + "\" acess=\"" + this.modifiers + "\" mod=\"" + this.modifiersAF + "\" loc=\"" + lineCount +  "\" ></const>\n");
        }
        this.modifiers = "default";
        this.modifiersAF = "default";
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
        commentsExtractor.getCommentsEntidadeJava(ctx);
        constructorParameters = constructorParameters.replace("<", "&lt;");
        constructorParameters = constructorParameters.replace(">", "&gt;");

        FileCreator.appendToVxlFile("\t\t\t<constr name=\"" + constructorIdentifier + constructorParameters + "\" acess=\"" + this.modifiers + "\">\n");
        addJavaDoc(javaDoc);
        this.modifiers = "default";
    }

    /**
     * Associa comentarios ao constructor
     *
     * @param ctx Entidade da Parser Tree
     */
    public void exitConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
       // commentsExtractor.associateComments(ctx);
        comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
        addComentario(comentario);
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
        commentsExtractor.getCommentsEntidadeJava(ctx);
        TerminalNode enumIdentifier = ctx.IDENTIFIER();

        FileCreator.appendToVxlFile("\t\t\t<enum name=\"" + enumIdentifier + "\" acess=\"" + this.modifiers + "\" mod=\"" + this.modifiersAF + "\" loc=\"" + lineCount + "\">\n");
        addJavaDoc(javaDoc);
        constEnum(ctx);
        this.modifiersClassOrInterface = "default";
        this.modifiersAF = "default";
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
        comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
        addComentario(comentario);
        //commentsExtractor.associateComments(ctx);
        FileCreator.appendToVxlFile("\t\t\t</enum>\n");
    }

    /**
     * Extrai a interface
     *
     * @param ctx Entidade da Parser Tree
     */
    public void enterInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        TerminalNode interfaceIdentifier = ctx.IDENTIFIER();

        FileCreator.appendToVxlFile("\t\t\t<intfc name=\"" + interfaceIdentifier + ".java" + "\" acess=\"" + this.modifiersClassOrInterface + "\" mod=\"" + this.modifiersAF + "\">\n");
        this.modifiersClassOrInterface = "default";
        this.modifiersAbstractFinal = "default";
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
            if (temp.equals("public") || temp.equals("private") || temp.equals("protected")) {
                this.modifiersClassOrInterface = temp;
            }

            if (temp.equals("abstract") || temp.equals("final") || temp.equals("static")) {
                this.modifiersAbstractFinal = temp;
            }
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
                FileCreator.appendToVxlFile("\t\t\t <javaDoc descr=\"" + javaDoc + "\"></javaDoc>\n");
            }
        }
        else {
            FileCreator.appendToVxlFile("\t\t\t <javaDoc descr=\"" + temp + "\"></javaDoc>\n");
        }
    }

    public void addComentario (ArrayList<String> listComentario) {
        if (listComentario.size() !=0) {
            for (String cmt: listComentario) {
                FileCreator.appendToVxlFile("\t\t\t<cmmt descr=\"" + cmt + "\"></cmmt>\n");
            }
        }
    }
}
