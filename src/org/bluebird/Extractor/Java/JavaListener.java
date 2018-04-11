package org.bluebird.Extractor.Java;

import org.antlr.v4.runtime.TokenStream;
import org.bluebird.LanguagesUtil.Java.JavaParser;
import org.bluebird.LanguagesUtil.Java.JavaParserBaseListener;

public class JavaListener extends JavaParserBaseListener {

    private TokenStream tokens;

    JavaListener(JavaParser parser) {
        this.tokens = parser.getTokenStream();
    }

    @Override
    public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
        System.out.println(ctx.CLASS() + " " + ctx.IDENTIFIER());
    }
    @Override
    public void enterMethodDeclaration(JavaParser.MethodDeclarationContext ctx) {
        System.out.println("\t" + ctx.IDENTIFIER());
    }

    @Override
    public void enterFieldDeclaration(JavaParser.FieldDeclarationContext ctx) {
        String atributo = tokens.getText(ctx.variableDeclarators().variableDeclarator(0).variableDeclaratorId().getSourceInterval());
        System.out.println("\t" + atributo);
    }
}
