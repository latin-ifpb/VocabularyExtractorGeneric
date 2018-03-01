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

import static org.bluebird.FileUtils.FileBrowser.readFile;

public class CSharpWalker implements LanguageWalker {

    public void walkFileTree(File file) throws IOException {
        String code = readFile(file, Charset.forName("UTF-8"));
        CSharpLexer lexer = new CSharpLexer(new ANTLRInputStream(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CSharpParser parser = new CSharpParser(tokens);
        ParserRuleContext tree = parser.compilation_unit();
        ParseTreeWalker walker = new ParseTreeWalker();
        CSharpListener extractor = new CSharpListener(parser, tokens);
        walker.walk(extractor, tree);
    }

    @Override
    public String languageFormat() {
        return "cs";
    }
}
