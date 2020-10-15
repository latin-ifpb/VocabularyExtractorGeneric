package org.bluebird.Extractor.Languages.VisualBasic;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.bluebird.Extractor.CommentsExtractor;
import org.bluebird.Extractor.Setup.ExtractorOptions;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtils.VisualBasic.VisualBasicBaseListener;
import org.bluebird.LanguagesUtils.VisualBasic.VisualBasicParser;

import java.util.Stack;

public class VisualBasicListener extends VisualBasicBaseListener {

    private TokenStream tokens;
    private Stack<Integer> ruleIndex;
    private CommentsExtractor commentsExtractor;
    private BufferedTokenStream tokenStream;
    private final int COMMENTS_CHANNEL = 4;

    /**
     * Construtor do Listener da Linguagem VisualBasic
     *
     * @param parser      Parser do VisualBasic
     * @param tokenStream Token Stream do Arquivo VisualBasic
     */
    VisualBasicListener(VisualBasicParser parser, BufferedTokenStream tokenStream) {
        this.tokens = parser.getTokenStream();
        this.commentsExtractor = new CommentsExtractor(tokenStream);
        this.ruleIndex = new Stack<>();
        this.tokenStream = tokenStream;
    }

    @Override
    public void enterStartRule(VisualBasicParser.StartRuleContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.getAllComments(ctx, COMMENTS_CHANNEL);
            this.ruleIndex.push(1);
        }
    }

    @Override
    public void enterSubStmt(VisualBasicParser.SubStmtContext ctx) {
        String subIdentifier = this.tokens.getText(ctx.ambiguousIdentifier());

        if (ExtractorOptions.isVocabularyTxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("subprocess " + subIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("<sub name=\"" + subIdentifier + "\">\n");
        }
    }
}
