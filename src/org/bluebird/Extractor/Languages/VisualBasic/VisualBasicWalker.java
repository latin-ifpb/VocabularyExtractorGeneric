package org.bluebird.Extractor.Languages.VisualBasic;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.bluebird.Extractor.LanguageWalker;
import org.bluebird.LanguagesUtils.VisualBasic.VisualBasicLexer;
import org.bluebird.LanguagesUtils.VisualBasic.VisualBasicParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static org.bluebird.FileUtils.FileBrowser.readFile;

public class VisualBasicWalker implements LanguageWalker {
    /**
     * Pecorre a arvore gerada do codigo fonte
     *
     * @param file Arquivo a ser pecorrido
     * @throws IOException Erro de leitura do arquivo
     */
    @Override
    public void walkFileTree(File file) throws IOException {
        String code = readFile(file, Charset.forName("UTF-8"));
        VisualBasicLexer lexer = new VisualBasicLexer(new ANTLRInputStream(code));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        VisualBasicParser parser = new VisualBasicParser(tokens);
        ParserRuleContext tree = parser.startRule();
        ParseTreeWalker walker = new ParseTreeWalker();
        VisualBasicListener extractor = new VisualBasicListener(parser, tokens);
        walker.walk(extractor, tree);
    }

    /**
     * Diz qual formato de arquivo da linguagem
     *
     * @return Formato do arquivo
     */
    @Override
    public String languageFormat() {
        return "vb";
    }
}
