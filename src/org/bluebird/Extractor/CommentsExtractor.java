package org.bluebird.Extractor;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.bluebird.FileUtils.FileCreator;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CommentsExtractor {

    private Map<Integer, String> comments;
    private BufferedTokenStream commentsStream;
    private Map<Integer, String> javaDoc;

    /**
     * Inicializa o hashmap que relaciona comentarios com linha do codigo
     *
     * @param commentsStream Token Stream do codigo fonte
     */
    public CommentsExtractor(BufferedTokenStream commentsStream) {
        this.comments = new HashMap<>();
        this.commentsStream = commentsStream;
        this.javaDoc = new HashMap<>();
    }

    /**
     * Extrai todos comentarios do arquivo
     *
     * @param ctx     Entidade
     * @param channel Canal dos comentarios
     */
    public void getAllComments(ParserRuleContext ctx, int channel) {
        String comment;

        for (Token cmt : commentsStream.getTokens(ctx.getStart().getTokenIndex(), ctx.getStop().getTokenIndex())) {
            if (cmt.getChannel() == channel) {
                comment = cmt.getText();
                comment = comment.replace("<", "&lt;");
                comment = comment.replace(">", "&gt;");
                comment = comment.replace("\"", "&quot;");
                comment = comment.replace("&", "&amp;");
                this.comments.put(cmt.getLine(), comment);
            }
        }
    }

    /**
     * Associa o comentario a entidade
     *
     * @param ctx Entidade
     */
    public void associateComments(ParserRuleContext ctx) {
        for (int i = ctx.getStart().getLine(); i <= ctx.getStop().getLine(); i++) {
            String cmt = this.comments.get(i);
            if (cmt != null) {
                if (!cmt.contains("/**")) {
                    FileCreator.appendToVxlFile("\t\t\t<cmmt descr=\"" + cmt + "\"></cmmt>\n");
                    this.comments.remove(i);
                }
                else {
                    this.comments.remove(i);
                }
            }
        }
    }

    /**
     * Associa o comentario a entidade
     *
     * @param index Linha inicial
     * @param ctx   Entidade
     */
    public void associateComments(int index, ParserRuleContext ctx) {
        for (int i = index; i < ctx.getStop().getLine(); i++) {
            String cmt = this.comments.get(i);
            if (cmt != null) {
                if (!cmt.contains("/**")) {
                    FileCreator.appendToVxlFile("\t\t\t<cmmt descr=\"" + cmt + "\"></cmmt>\n");
                    this.comments.remove(i);
                }
                else {
                    this.comments.remove(i);
                }
            }

        }
    }

    public void getJavaDoc(ParserRuleContext ctx) {
        for (int i = ctx.getStart().getLine(); i < ctx.getStop().getLine(); i++) {
            String javaDoc = this.comments.get(i);
            if (javaDoc != null) {
                if (javaDoc.contains("/**")) {
                    this.javaDoc.put(i, javaDoc);
                    this.comments.remove(i);
                }

            }

        }
    }

       public String associateJavaDoc(int getLineStart) {
            Set<Integer> key = this.javaDoc.keySet();
            String javaDoc = "";
            for(Integer i : key) {
                if(i < getLineStart) {
                    javaDoc = this.javaDoc.get(i);
                    this.javaDoc.remove(i);
                    break;

                }
          }
           return javaDoc;
        }
}



