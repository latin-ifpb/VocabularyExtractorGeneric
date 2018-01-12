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
        System.out.print("\t" + this.tokens.getText(ctx.all_member_modifiers().getSourceInterval()) + " ");

    }

    @Override
    public void enterClass_definition(CSharpParser.Class_definitionContext ctx) {
        System.out.println(ctx.CLASS()+ " " + this.tokens.getText(ctx.identifier()));
    }

    @Override
    public void enterMethod_declaration(CSharpParser.Method_declarationContext ctx) {
        String name = this.tokens.getText(ctx.method_member_name().identifier(0));
        System.out.print("\t\t" + name);
        //codigo nao extrai se o metodo nao tiver parametros
    }

    @Override
    public void enterFormal_parameter_list(CSharpParser.Formal_parameter_listContext ctx) {
        System.out.println("(" + this.tokens.getText(ctx) + ")");
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
        System.out.print("\t\t"+ name);
    }

}
