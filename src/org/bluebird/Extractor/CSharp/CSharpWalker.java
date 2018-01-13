package org.bluebird.Extractor.CSharp;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.bluebird.Extractor.LanguageWalker;
import org.bluebird.LanguagesUtil.CSharp.CSharpLexer;
import org.bluebird.LanguagesUtil.CSharp.CSharpParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class CSharpWalker implements LanguageWalker {

    private static String readFile(File file, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }

    public void walkFileTree(File file) throws IOException {
        String code = readFile(file, Charset.forName("UTF-8"));
        CSharpLexer lexer = new CSharpLexer(new ANTLRInputStream(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CSharpParser parser = new CSharpParser(tokens);
        ParserRuleContext tree = parser.compilation_unit();
        ParseTreeWalker walker = new ParseTreeWalker();
        CSharpListener extractor = new CSharpListener(parser);
        walker.walk(extractor, tree);
    }
}
