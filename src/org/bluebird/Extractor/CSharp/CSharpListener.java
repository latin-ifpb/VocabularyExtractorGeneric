package org.bluebird.Extractor.CSharp;

import org.antlr.v4.runtime.TokenStream;
import org.bluebird.LanguagesUtil.CSharp.CSharpParser;
import org.bluebird.LanguagesUtil.CSharp.CSharpParserBaseListener;

public class CSharpListener extends CSharpParserBaseListener {

    private CSharpParser cSharpParser;
    private TokenStream tokens;

    CSharpListener(CSharpParser parser) {
        this.cSharpParser = parser;
        this.tokens = parser.getTokenStream();
    }/*Construtor que passa o parser*/

    @Override
    public void enterNamespace_declaration(CSharpParser.Namespace_declarationContext ctx) {
        System.out.println(ctx.NAMESPACE() + " " + this.tokens.getText(ctx.qualified_identifier().getSourceInterval()) +" ");
    }/* Extrai o namespace*/

    @Override
    public void enterType_declaration(CSharpParser.Type_declarationContext ctx) {
        String modificadores = "";
        try {
            for(int i = 0; i < ctx.all_member_modifiers().all_member_modifier().size(); i++){
                modificadores += this.tokens.getText(ctx.all_member_modifiers().all_member_modifier(i)) + " ";
            }
        } catch (NullPointerException e) {
            modificadores = "";
        }

        System.out.print("\t" + modificadores);
    }/* Extrai o modificador da classe*/

    @Override
    public void enterClass_definition(CSharpParser.Class_definitionContext ctx) {
        try {
            String args = this.tokens.getText(ctx.type_parameter_list().type_parameter(0));
            System.out.println(ctx.CLASS() + " " + this.tokens.getText(ctx.identifier()) + " <" + args + ">");
        } catch (NullPointerException e) {  //Se der erro a classe nao tem parametro
            System.out.println(ctx.CLASS() + " " + this.tokens.getText(ctx.identifier()));
        }
    }/* Extrai a classe */

    @Override
    public void enterMethod_declaration(CSharpParser.Method_declarationContext ctx) {
        String name = this.tokens.getText(ctx.method_member_name().identifier(0));
        String args;

        try {
            args = this.tokens.getText(ctx.formal_parameter_list());
        } catch (NullPointerException e) {  //Se der erro nao tem parametro
            args = "";
        }
        System.out.println(name + "(" + args + ")");
    } /* Extrai o metodo e os parametros*/

    @Override
    public void enterField_declaration(CSharpParser.Field_declarationContext ctx) {
        for ( CSharpParser.Variable_declaratorContext atributo : ctx.variable_declarators().variable_declarator()) {
            System.out.println(this.tokens.getText(atributo.identifier().getSourceInterval()));
        }
    }/* Extrai os atributos */

    @Override
    public void enterConstructor_declaration(CSharpParser.Constructor_declarationContext ctx) {
        String name = this.tokens.getText(ctx.identifier());
        String args;
        try {
            args = this.tokens.getText(ctx.formal_parameter_list());
        } catch (NullPointerException e) {  //Se der erro nao tem parametro
            args = "";
        }
        System.out.println(name + "(" + args + ")");
    } /* Extrai o construtor*/

    @Override
    public void enterClass_member_declaration(CSharpParser.Class_member_declarationContext ctx) {
        String modificadores = "";
        try {
            for(int i = 0; i < ctx.all_member_modifiers().all_member_modifier().size(); i++){
                modificadores += this.tokens.getText(ctx.all_member_modifiers().all_member_modifier(i)) + " ";
            }
        } catch (NullPointerException e) {
            modificadores = " ";
        }

        System.out.print("\t\t" + modificadores);
    }/* Extrai os modificadores de atributos e metodos da classe */

    @Override
    public void enterProperty_declaration(CSharpParser.Property_declarationContext ctx) {
        System.out.println(this.tokens.getText(ctx.member_name().namespace_or_type_name().identifier(0)));
    }/* Extrai os getters e setters*/

    @Override
    public void enterTyped_member_declaration(CSharpParser.Typed_member_declarationContext ctx) {
        System.out.print(this.tokens.getText(ctx.type()) + " ");

    }/* Extrai os tipos das classes, dos metodos, e dos atributos*/

    @Override
    public void enterLocal_variable_declaration(CSharpParser.Local_variable_declarationContext ctx) {
        String tipo = this.tokens.getText(ctx.local_variable_type());
        String nome = this.tokens.getText(ctx.local_variable_declarator(0).identifier());
        System.out.println("\t\t\t" + tipo + " " + nome);
    } /* Extrai a variavel local da classe*/

}
