package org.bluebird.Extractor;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.bluebird.FileUtils.FileCreator;

import java.io.File;
import java.util.*;

public class CommentsExtractor {

    private Map<Integer, String> comments;
    private BufferedTokenStream commentsStream;
    private Map<Integer, String> listJavaDoc;


    /**
         * Inicializa o hashmap que relaciona comentarios com linha do codigo
     *
     * @param commentsStream Token Stream do codigo fonte
     */
    public CommentsExtractor(BufferedTokenStream commentsStream) {
        this.comments = new HashMap<>();
        this.commentsStream = commentsStream;
        this.listJavaDoc = new HashMap<>();
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
                } else {
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
                if (!cmt.contains("/**")) { // melhorar essa condição
                    FileCreator.appendToVxlFile("\t\t\t<cmmt descr=\"" + cmt + "\"></cmmt>\n");
                    this.comments.remove(i);
                } else {
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
                    this.listJavaDoc.put(i, javaDoc);
                    this.comments.remove(i);
                }

            }

        }
    }

    public ArrayList<String> associateJavaDoc(int getLineStart) {
        ArrayList<String> listTemp = new ArrayList<>();
        String javaDoc = "";
        for (Integer i : new HashSet<>(listJavaDoc.keySet())) {
            if (i < getLineStart) {
                javaDoc = this.listJavaDoc.get(i);
                listTemp.add(javaDoc);
                //FileCreator.appendToVxlFile("\t\t\t <javaDoc cmmt=\"" + javaDoc + "\"></javaDoc>\n");
                listJavaDoc.remove(i);
                }
            }
            return listTemp;
        }
    }




