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
    private Map<Integer, String> commentEntidade;
    private List<String> tagsJavaDoc = Arrays.asList("@author", "{@code}", "{@docRoot}", "@deprecated", "@exception",
            "{@inheritDoc}", "{@link}", "{@linkplain}", "@param", "@return", "@see", "@serial", "@serialData",
            "@serialField", "@since", "@throws", "{@value}", "@version");


    /**
     * Inicializa o hashmap que relaciona comentarios com linha do codigo
     *
     * @param commentsStream Token Stream do codigo fonte
     */
    public CommentsExtractor(BufferedTokenStream commentsStream) {
        this.comments = new HashMap<>();
        this.commentsStream = commentsStream;
        this.listJavaDoc = new HashMap<>();
        this.commentEntidade = new HashMap<>();
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
                    if(!cmt.contains("@")) {
                        FileCreator.appendToVxlFile("\t\t\t<cmmt descr=\"" + cmt + "\"></cmmt>\n");
                        this.comments.remove(i);
                    }
                    else {
                        this.comments.remove(i);
                    }
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
                    if(!cmt.contains("@")) {
                        FileCreator.appendToVxlFile("\t\t\t<cmmt descr=\"" + cmt + "\"></cmmt>\n");
                        this.comments.remove(i);
                    }
                    else {
                        //é javadoc
                        this.comments.remove(i);
                    }
                }
            }

        }
    }

    public void getCommentsEntidadeJava(ParserRuleContext ctx) {
        for (int i = ctx.getStart().getLine(); i<ctx.getStop().getLine(); i++) {
            String comment = this.comments.get(i);
            if(comment != null) {
                if ((comment.contains("/**") && !comment.contains("@")) || comment.contains("//") || comment.contains("/*"))
                {
                    this.commentEntidade.put(i, comment);
                    this.comments.remove(i);
                }
            }
        }
    }

    public void getCommentsEntidadeCSharp(ParserRuleContext ctx) {
        for (int i = ctx.getStart().getLine(); i<ctx.getStop().getLine(); i++) {
            String comment = this.comments.get(i);
            if (comment != null) {
                if (comment.contains("//") || comment.contains("/*")) {
                    this.commentEntidade.put(i, comment);
                    this.comments.remove(i);
                }


            }
        }
    }

    public ArrayList<String> associarComentario(int getLineStart, int getLineStop) {
        ArrayList<String> listTemp = new ArrayList<>();
        String comentario = "";
        for (Integer i : new HashSet<>(commentEntidade.keySet())) {
            if (i >= getLineStart && i<= getLineStop) {
                comentario = this.commentEntidade.get(i);
                listTemp.add(comentario);
                commentEntidade.remove(i);
            }
        }
        return listTemp;

    }



    /**
     * Pega o javaDoc e adiciona lista de JavaDocs
     * @param ctx Entidade
     */

    public void getJavaDoc(ParserRuleContext ctx) {
        for (int i = ctx.getStart().getLine(); i < ctx.getStop().getLine(); i++) {
            String javaDoc = this.comments.get(i);
            if (javaDoc != null) {
                if ((javaDoc.contains("/**")) && (javaDoc.contains("@"))) {
                    this.listJavaDoc.put(i, javaDoc);
                    this.comments.remove(i);
                }
            }
        }
    }

    /**
     * Associa a o javaDoc a entidade correta
     * @param getLineStart linha que começa a entidade
     * @return lista com os javaDocs que pertence à entidade
     */

    public ArrayList<String> associateJavaDoc(int getLineStart) {
        ArrayList<String> listTemp = new ArrayList<>();
        String javaDoc = "";
        for (Integer i : new HashSet<>(listJavaDoc.keySet())) {
            if (i < getLineStart) {
                javaDoc = this.listJavaDoc.get(i);
                listTemp.add(javaDoc);
                listJavaDoc.remove(i);
                }
            }
            return listTemp;
        }
    }




