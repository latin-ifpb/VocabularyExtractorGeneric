package org.bluebird.Extractor.CSharp;

import org.antlr.v4.runtime.TokenStream;
import org.bluebird.LanguagesUtil.CSharp.CSharpParser;
import org.bluebird.LanguagesUtil.CSharp.CSharpParserBaseListener;

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
    public void enterType_declaration(CSharpParser.Type_declarationContext ctx) {
        System.out.print("\t" + this.tokens.getText(ctx.all_member_modifiers().getSourceInterval()) + " ");
    }
    public void enterClass_base(CSharpParser.Class_baseContext ctx) {
            System.out.print("\t" + ctx.class_type().STRING() + " ");
    }

}
