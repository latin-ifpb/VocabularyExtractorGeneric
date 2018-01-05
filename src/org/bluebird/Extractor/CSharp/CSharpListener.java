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
}
