package org.bluebird.Extractor.CSharp;

import org.antlr.v4.runtime.TokenStream;
import org.bluebird.LanguagesUtil.CSharp.CSharpParser;
import org.bluebird.LanguagesUtil.CSharp.CSharpParserBaseListener;

import java.util.List;

public class CSharpListener extends CSharpParserBaseListener {

    private CSharpParser cSharpParser;
    private TokenStream tokens;

    public CSharpListener(CSharpParser parser) {
        this.cSharpParser = parser;
        this.tokens = parser.getTokenStream();
    }

    @Override
    public void enterNamespace_declaration(CSharpParser.Namespace_declarationContext ctx) {
        System.out.println(ctx.NAMESPACE() + " " + this.tokens.getText(ctx.qualified_identifier().getSourceInterval()) +" ");

    }

    @Override
    public void enterType_declaration(CSharpParser.Type_declarationContext ctx) {
        try {
            System.out.print("\t" + this.tokens.getText(ctx.all_member_modifiers().getSourceInterval()) + " ");
        } catch (NullPointerException e) { //Se der erro nao tem modificador na classe
            System.out.print("\t");
        }

    }

    @Override
    public void enterClass_definition(CSharpParser.Class_definitionContext ctx) {
        try {
            String args = this.tokens.getText(ctx.type_parameter_list().type_parameter(0));
            System.out.println(ctx.CLASS()+ " " + this.tokens.getText(ctx.identifier()) + " <" + args + ">");
        } catch (NullPointerException e) {  //Se der erro a classe nao tem parametro
            System.out.println(ctx.CLASS()+ " " + this.tokens.getText(ctx.identifier()));
        }
    }

    @Override
    public void enterMethod_declaration(CSharpParser.Method_declarationContext ctx) {
        String name = this.tokens.getText(ctx.method_member_name().identifier(0));
        String args;
        try {
            args = this.tokens.getText(ctx.formal_parameter_list());
        } catch (NullPointerException e) {  //Se der erro nao tem parametro
            args = "";
        }
        System.out.println("\t\t" + name + "(" + args + ")");
    }

    @Override
    public void enterField_declaration(CSharpParser.Field_declarationContext ctx) {
        for ( CSharpParser.Variable_declaratorContext atributo : ctx.variable_declarators().variable_declarator()) {
            System.out.println("\t\t" + this.tokens.getText(atributo.identifier().getSourceInterval()));
        }
    }

    @Override
    public void enterConstructor_declaration(CSharpParser.Constructor_declarationContext ctx) {
        String name =  this.tokens.getText(ctx.identifier());
        String args;
        try {
            args = this.tokens.getText(ctx.formal_parameter_list());
        } catch (NullPointerException e) {  //Se der erro nao tem parametro
            args = "";
        }
        System.out.println("\t\t"+ name + "(" + args + ")");
    }

}
