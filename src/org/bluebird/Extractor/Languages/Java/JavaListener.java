package org.bluebird.Extractor.Languages.Java;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bluebird.Extractor.CommentsExtractor;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtils.Java.JavaLexer;
import org.bluebird.LanguagesUtils.Java.JavaParser;
import org.bluebird.LanguagesUtils.Java.JavaParserBaseListener;

import java.util.Stack;

public class JavaListener extends JavaParserBaseListener {

    private TokenStream tokens;
    private String modifiers = "default";
    private String modifiersClassOrInterface = "default";
    private Stack<Integer> ruleIndex;
    private CommentsExtractor commentsExtractor;
    private String pack;

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

    public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        String packageIdentifier = this.tokens.getText(ctx.qualifiedName());
        ruleIndex.push(ctx.getStart().getLine());
        packageIdentifier = packageIdentifier.replace(".","/");
        this.pack = packageIdentifier;
        FileCreator.appendToVxlFile("\t<pack name=\""+packageIdentifier+"\">\n");
    }
    public void exitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        ruleIndex.pop();
        FileCreator.appendToVxlFile("\t<pack>\n");
    }

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
        FileCreator.appendToVxlFile("\t\t<class name=\"" +pack+"\""+ classIdentifier +".java\""+ " intfc=\"n\"" +"\" acess=\"" + this.modifiersClassOrInterface + "\"> sloc=\n");
        //System.out.println(this.modifiersClassOrInterface + " " + ctx.CLASS() + " " + classIdentifier);
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
        String typeMethod = this.tokens.getText(ctx.typeTypeOrVoid());
        FileCreator.appendToVxlFile("\t\t\t<mth name=\"" +pack+"\""+ methodIdentifier + methodParamenters + "\" type=\"" + typeMethod + "\"acess=\"" +
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
        String typeField = this.tokens.getText(ctx.typeType());
        //System.out.println("\t" + this.modifiers + " "+ typeField + " " + fieldIdentifier);
        if (verificaConstante(fieldIdentifier) == false){
            FileCreator.appendToVxlFile("\t\t\t<field name=\"" + fieldIdentifier + "\"type=\""+ typeField + "\" acess=\"" + this.modifiers + "\" >\n");
        }else{
            FileCreator.appendToVxlFile("\t\t\t<const name=\""+fieldIdentifier+"\"type=\""+ typeField + "\" acess=\"" + this.modifiers + "\" >\n");
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
     * Associa comentarios aos atributos
     * @param ctx Entidade da Parser Tree
     */
    public void exitFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        String fieldIdentifier =  this.tokens.getText(ctx.variableDeclarators().variableDeclarator(0).variableDeclaratorId().getSourceInterval());

        commentsExtractor.associateComments(ctx);
        if (verificaConstante(fieldIdentifier) == false){
            FileCreator.appendToVxlFile("\t\t\t</field>\n");
        }else{
            FileCreator.appendToVxlFile("\t\t\t</const>\n");
        }
    }
    /**
     * Extrai os construtores e seus respectivos parametros
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        String constructorParameters = this.tokens.getText(ctx.formalParameters());
        TerminalNode constructorIdentifier = ctx.IDENTIFIER();
        //System.out.println("\t" + this.modifiers + " " +constructorIdentifier + " " +typeParameters);
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
        //System.out.println("\t" + this.modifiers + ctx.ENUM() + " " + enumIdentifier);
        FileCreator.appendToVxlFile("\t\t\t<enum name=\"" + enumIdentifier +
                "\" acess=\""+ this.modifiersClassOrInterface+"\">\n");
        this.modifiersClassOrInterface = "default";

    }
    /**
     * Extrai a interface
     * @param ctx Entidade da Parser Tree
     */
    public void enterInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
        TerminalNode interfaceIdentifier = ctx.IDENTIFIER();
        //System.out.println(this.modifiersClassOrInterface + " " + ctx.INTERFACE() + " " + interfaceIdentifier);
        FileCreator.appendToVxlFile("\t\t\t<intfc name=\""+pack+"\"" + interfaceIdentifier +
                ".java"+"\" acess=\"" + this.modifiersClassOrInterface + "\">\n");
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
        if (temp.equals("public") || temp.equals("private") || temp.equals("protected") ){
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
        FileCreator.appendToVxlFile("\t\t\t<lvar name=\"" + variableIdentifier + "\" type=\"" + variableType +"\"\n");

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
