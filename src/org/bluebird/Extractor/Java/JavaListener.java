package org.bluebird.Extractor.Java;

import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bluebird.LanguagesUtil.Java.JavaParser;
import org.bluebird.LanguagesUtil.Java.JavaParserBaseListener;

import java.util.Stack;

public class JavaListener extends JavaParserBaseListener {

    private TokenStream tokens;
    private String modifiersClassInterface = "";
    private String modifiersVariable;
    private Stack<Integer> ruleIndex;

    JavaListener(JavaParser parser) {
        this.tokens = parser.getTokenStream();
        this.ruleIndex = new Stack<>();
    }
    /**
      * Extrai o acesso das classes e interfarces
     * @param ctx Entidade da Parser Tree
     */
    public void enterModifier(JavaParser.ModifierContext ctx) {
        String temp = this.tokens.getText(ctx.classOrInterfaceModifier());
        if (temp.equals("public") || temp.equals("private") || temp.equals("protected")){
            this.modifiersClassInterface = temp;
        }
    }

    /**
     * Extrai as classes
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        TerminalNode classIdentifier = ctx.IDENTIFIER();
        //ruleIndex.push(ctx.getStart().getLine());
        //FileCreator.appendToVxlFile("\t\t<class name=\"" + classIdentifier + "\" acess=\"" + this.modifiersClassInterface + "\">\n");
        System.out.println(this.modifiersClassInterface + " " + ctx.CLASS() + " " + classIdentifier);
        this.modifiersClassInterface = "default";
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
        System.out.println("\t" + typeMethod + " " + methodIdentifier + " " + methodParamenters);
    }
    /**
     * Extrai os atributos e seus respectivos tipos
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        String fieldIdentifier =  this.tokens.getText(ctx.variableDeclarators().variableDeclarator(0).variableDeclaratorId().getSourceInterval());
        String typeField = this.tokens.getText(ctx.typeType());
        System.out.println("\t" + this.modifiersClassInterface + " "+ typeField + " " + fieldIdentifier);
        this.modifiersClassInterface = "default";
    }
    /**
     * Extrai os construtores e seus respectivos parametros
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
        String constructorParameters = this.tokens.getText(ctx.formalParameters());
        TerminalNode constructorIdentifier = ctx.IDENTIFIER();
        System.out.println("\t"+constructorIdentifier + " " +
                constructorParameters);
    }
    /**
     * Extrai o acesso das variaveis
     * @param ctx Entidade da Parser Tree
     */
    public void enterVariableModifier(JavaParser.VariableModifierContext ctx) {
        String temp = this.tokens.getText(ctx.annotation());
        System.out.println(temp);
    }



}
