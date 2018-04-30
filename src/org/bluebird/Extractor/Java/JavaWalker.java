package org.bluebird.Extractor.Java;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.bluebird.Extractor.LanguageWalker;
import org.bluebird.LanguagesUtil.Java.JavaLexer;
import org.bluebird.LanguagesUtil.Java.JavaParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.bluebird.FileUtils.FileBrowser.readFile;

public class JavaWalker implements LanguageWalker {

    @Override
    public void walkFileTree(File file) throws IOException {
        String code = readFile(file, Charset.forName("UTF-8"));
        JavaLexer lexer = new JavaLexer(new ANTLRInputStream(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        ParserRuleContext tree = parser.compilationUnit();
        ParseTreeWalker walker = new ParseTreeWalker();
        JavaListener extractor = new JavaListener(parser, tokens);
        walker.walk(extractor, tree);
    }

    @Override
    public String languageFormat() {
        return "java";
    }
}
