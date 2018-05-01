package org.bluebird.Extractor.Java;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bluebird.Extractor.CommentsExtractor;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtil.Java.JavaLexer;
import org.bluebird.LanguagesUtil.Java.JavaParser;
import org.bluebird.LanguagesUtil.Java.JavaParserBaseListener;

import java.util.Stack;

public class JavaListener extends JavaParserBaseListener {

    private TokenStream tokens;
    private String modifiers = "default";
    private String modifiersClassOrInterface = "default";
    private Stack<Integer> ruleIndex;
    private CommentsExtractor commentsExtractor;

    JavaListener(JavaParser parser, BufferedTokenStream tokenStream) {
        this.tokens = parser.getTokenStream();
        this.ruleIndex = new Stack<>();
        this.commentsExtractor = new CommentsExtractor(tokenStream);
    }
    /**
     * Inicia a extração do arquivo Java e pega todos comentarios do arquivo
     * @param ctx Entidade da Parser Tree
     */
    public void enterCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        commentsExtractor.getAllComments(ctx, JavaLexer.COMMENTS_CHANNEL);
        this.ruleIndex.push(1);
    }
    /**
     * Termina a extração do arquivo Java
     * @param ctx Entidade da Parser Tree
     */
    public void exitCompilationUnit(JavaParser.CompilationUnitContext ctx) {
        commentsExtractor.associateComments(ctx);
    }


    /**
      * Extrai o acesso dos metodos, construtores e atributos
     * @param ctx Entidade da Parser Tree
     */

    public void enterModifier(JavaParser.ModifierContext ctx) {
        String temp = this.tokens.getText(ctx.classOrInterfaceModifier());
        if (temp.equals("public") || temp.equals("private") || temp.equals("protected")){
            this.modifiers = temp;
        }
    }

    /**
     * Extrai as classes
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        TerminalNode classIdentifier = ctx.IDENTIFIER();
        ruleIndex.push(ctx.getStart().getLine());
        FileCreator.appendToVxlFile("\t\t<class name=\"" + classIdentifier + "\" acess=\"" + this.modifiersClassOrInterface + "\">\n");
        System.out.println(this.modifiersClassOrInterface + " " + ctx.CLASS() + " " + classIdentifier);
        this.modifiersClassOrInterface = "default";
    }
    /**
     * Associa comentarios a classe
     * @param ctx Entidade da Parser Tree
     */

    public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        ruleIndex.pop();
        FileCreator.appendToVxlFile("\t\t</class>\n");
    }
    /**
     * Extrai os metodos
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        TerminalNode methodIdentifier = ctx.IDENTIFIER();
        String methodParamenters = this.tokens.getText(ctx.formalParameters());
        String typeMethod = this.tokens.getText(ctx.typeTypeOrVoid()); //caso precise de adicionar o tipo do metodo no vxl
        FileCreator.appendToVxlFile("\t\t\t<mth name=\"" + methodIdentifier + methodParamenters + "\" acess=\"" +
                this.modifiers + "\">\n");
        //System.out.println("\t" + this.modifiers + " "+ typeMethod + " " + methodIdentifier + " " + methodParamenters);
        this.modifiers = "default";
    }
    /**
     * Associa comentarios aos metodos
     * @param ctx Entidade da Parser Tree
     */
    public void exitMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        commentsExtractor.associateComments(ruleIndex.peek(), ctx);
        FileCreator.appendToVxlFile("\t\t\t</mth>\n");
    }
    /**
     * Extrai os atributos e seus respectivos tipos
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        String fieldIdentifier =  this.tokens.getText(ctx.variableDeclarators().variableDeclarator(0).variableDeclaratorId().getSourceInterval());
        String typeField = this.tokens.getText(ctx.typeType()); //caso precise adicionar o tipo do atributo ao vxl
        //System.out.println("\t" + this.modifiers + " "+ typeField + " " + fieldIdentifier);
        FileCreator.appendToVxlFile("\t\t\t<field name=\"" + fieldIdentifier + "\" acess=\"" + this.modifiers + "\" >\n");
        this.modifiers = "default";
    }
    /**
     * Associa comentarios aos atributos
     * @param ctx Entidade da Parser Tree
     */
    public void exitFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToVxlFile("\t\t\t</field>\n");
    }
    /**
     * Extrai os construtores e seus respectivos parametros
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        String constructorParameters = this.tokens.getText(ctx.formalParameters());
        TerminalNode constructorIdentifier = ctx.IDENTIFIER();
        //System.out.println("\t" + this.modifiers + " " +constructorIdentifier + " " +constructorParameters);
        FileCreator.appendToVxlFile("\t\t\t<constr name=\"" + constructorIdentifier + constructorParameters +
                "\" acess=\"" + this.modifiers + "\">\n");
        this.modifiers = "default";
    }
    /**
     * Associa comentarios ao constructor
     * @param ctx Entidade da Parser Tree
     */
    public void exitConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToVxlFile("\t\t\t</constr>\n");
    }
    /**
     * Extrai o enum
     * @param ctx Entidade da Parser Tree
     */
    public void enterEnumDeclaration(JavaParser.EnumDeclarationContext ctx) {
        TerminalNode enumIdentifier = ctx.IDENTIFIER();
        //falta extrair o modificador do enum
        //System.out.println("\t" + this.modifiers + ctx.ENUM() + " " + enumIdentifier);
        FileCreator.appendToVxlFile("\t\t\t<enum name=\"" + enumIdentifier +
                "\" acess=\"\">\n");

    }
    /**
     * Extrai a interface
     * @param ctx Entidade da Parser Tree
     */
    public void enterInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        TerminalNode interfaceIdentifier = ctx.IDENTIFIER();
        //System.out.println(this.modifiersClassOrInterface + " " + ctx.INTERFACE() + " " + interfaceIdentifier);
        FileCreator.appendToVxlFile("\t\t\t<intfc name=\"" + interfaceIdentifier +
                "\" acess=\"" + this.modifiersClassOrInterface + "\">\n");
        this.modifiersClassOrInterface = "default";
    }
    /**
     * Fecha tag da interface
     * @param ctx Entidade da Parser Tree
     */
    public void exitInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        FileCreator.appendToVxlFile("\t\t\t</intfc>\n");

    }
    /**
     * Extrai os modificadores das classes e interfaces
     * @param ctx Entidade da Parser Tree
     */
    public void enterTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
        String temp = " ";
        for (int i = 0; i < ctx.classOrInterfaceModifier().size(); i ++ ){
            temp = this.tokens.getText(ctx.classOrInterfaceModifier(i));
        }
        if (temp.equals("public") || temp.equals("private") || temp.equals("protected")){
            this.modifiersClassOrInterface = temp;
        }
    }
    /**
     * Extrai a variavel local do metodo
     * @param ctx Entidade da Parser Tree
     */
    public void enterLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
        String variableIdentifier = this.tokens.getText(ctx.variableDeclarators().variableDeclarator(0));
        variableIdentifier = breakString(variableIdentifier);
        String variableType = this.tokens.getText(ctx.typeType());
        //System.out.println("\t\t" + variableType + " " + variableIdentifier);
        FileCreator.appendToVxlFile("\t\t\t<lvar name=\"" + variableIdentifier + "\" type=\"" + variableType);

    }
    /**
    Metodo que tira a declaração das variaveis locais e deixa apenas o seu tipo e nome
     @param identifier entidade do Metodo enterLocalVariableDeclaration
     */
    public String breakString(String identifier){
        String[] temp;
        for (int i = 0; i < identifier.length(); i++){
            temp = identifier.split("=");
            identifier = temp[0];
        }
        return identifier;
    }



}
