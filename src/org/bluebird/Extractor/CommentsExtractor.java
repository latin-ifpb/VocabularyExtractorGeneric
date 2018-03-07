package org.bluebird.Extractor;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtil.CSharp.CSharpLexer;

import java.util.HashMap;
import java.util.Map;

public class CommentsExtractor {

    private Map<Integer, String> comments;
    private BufferedTokenStream commentsStream;

    public CommentsExtractor(BufferedTokenStream commentsStream) {
        this.comments = new HashMap<>();
        this.commentsStream = commentsStream;
    }

    public void getAllComments(ParserRuleContext ctx) {
        String comment;

        for (Token cmt : commentsStream.getTokens(ctx.getStart().getTokenIndex(), ctx.getStop().getTokenIndex())) {
            if (cmt.getChannel() == CSharpLexer.COMMENTS_CHANNEL) {
                comment = cmt.getText();
                comment = comment.replace("<", "&lt;");
                comment = comment.replace(">", "&gt;");
                comment = comment.replace("\"", "&quot;");
                comment = comment.replace("&", "&amp;");
                this.comments.put(cmt.getLine(), comment);
            }
        }
    }

    public void associateComments(ParserRuleContext ctx) {
        for (int i = ctx.getStart().getLine(); i < ctx.getStop().getLine(); i++) {
            String cmt = this.comments.get(i);
            if (cmt != null) {
                FileCreator.appendToXmlFile("\t\t\t<cmmt descr=\"" + cmt + "\"></cmmt>\n");
                this.comments.remove(i);
            }
        }
    }
}
