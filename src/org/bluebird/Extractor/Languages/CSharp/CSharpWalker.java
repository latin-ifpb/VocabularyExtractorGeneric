package org.bluebird.Extractor.Languages.CSharp;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.bluebird.Extractor.LanguageWalker;
import org.bluebird.LanguagesUtils.CSharp.CSharpLexer;
import org.bluebird.LanguagesUtils.CSharp.CSharpParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.bluebird.FileUtils.FileBrowser.readFile;

public class CSharpWalker implements LanguageWalker {

    /**
     * Pecorre a arvore gerada do codigo fonte
     *
     * @param file Arquivo a ser pecorrido
     * @throws IOException Erro de leitura do arquivo
     */
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

    /**
     * Diz qual formato de arquivo da linguagem
     *
     * @return Formato do arquivo
     */
    @Override
    public String languageFormat() {
        return "cs";
    }
}
