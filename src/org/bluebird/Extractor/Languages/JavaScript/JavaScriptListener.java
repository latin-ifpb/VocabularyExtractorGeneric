package org.bluebird.Extractor.Languages.JavaScript;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bluebird.Extractor.CallGraph;
import org.bluebird.Extractor.CommentsExtractor;
import org.bluebird.Extractor.Setup.ExtractorOptions;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtils.JavaScript.JavaScriptLexer;
import org.bluebird.LanguagesUtils.JavaScript.JavaScriptParser;
import org.bluebird.LanguagesUtils.JavaScript.JavaScriptParserBaseListener;

import java.util.Stack;

public class JavaScriptListener extends JavaScriptParserBaseListener {

    private TokenStream tokens;
    private CallGraph callGraph;
    private String funcaoAtual = null;
    private Stack<Integer> ruleIndex;
    private CommentsExtractor commentsExtractor;
    private boolean methodExists = false;

    /**
     * Construtor do Listener da Linguagem JS
     *
     * @param parser      Parser do JS
     * @param tokenStream Token Stream do Arquivo JS
     */
    JavaScriptListener(JavaScriptParser parser, BufferedTokenStream tokenStream) {
        this.tokens = parser.getTokenStream();
        this.callGraph = new CallGraph();
        this.commentsExtractor = new CommentsExtractor(tokenStream);
        this.ruleIndex = new Stack<>();
    }

    /**
     * Inicia a extração do arquivo JS e pega todos comentarios do arquivo
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterProgram(JavaScriptParser.ProgramContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.getAllComments(ctx, JavaScriptLexer.COMMENTS_CHANNEL);
            this.ruleIndex.push(1);
        }
    }

    /**
     * Termina a extração do arquivo JS
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitProgram(JavaScriptParser.ProgramContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.associateComments(ctx);
        }
    }

    /**
     * Extrai as informações das funções no arquivo JS
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterFunctionDeclaration(JavaScriptParser.FunctionDeclarationContext ctx) {
        TerminalNode functionArg, functionIdentifier;

        functionIdentifier = ctx.Identifier();

        if (ExtractorOptions.isVocabularyTxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("function " + functionIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            ruleIndex.push(ctx.getStart().getLine());

            FileCreator.appendToVxlFile("\t<fnc name=\"" + functionIdentifier + "\" >\n");

            if (ctx.formalParameterList() != null) {
                for (JavaScriptParser.FormalParameterArgContext arg : ctx.formalParameterList().formalParameterArg()) {
                    functionArg = arg.Identifier();
                    FileCreator.appendToVxlFile("\t\t<arg name=\"" + functionArg + "\" ></arg>\n");
                }

                if (ctx.formalParameterList().lastFormalParameterArg() != null) {
                    functionArg = ctx.formalParameterList().lastFormalParameterArg().Identifier();
                    FileCreator.appendToVxlFile("\t\t<arg name=\"" + functionArg + "\" ></arg>\n");
                }
            }
        }
    }

    /**
     * Associa os comentarios a funcao
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitFunctionDeclaration(JavaScriptParser.FunctionDeclarationContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.associateComments(ctx);
            ruleIndex.pop();
            FileCreator.appendToVxlFile("\t</fnc>\n");
        }
    }

    /**
     * Extrai a classe
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterClassExpression(JavaScriptParser.ClassExpressionContext ctx) {
        TerminalNode classIdentifier = ctx.Identifier();

        if (ExtractorOptions.isVocabularyTxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("class " + classIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            ruleIndex.push(ctx.getStart().getLine());
            FileCreator.appendToVxlFile("\t<class name=\"" + classIdentifier + "\" >\n");
        }
    }

    /**
     * Associa comentarios a classe
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitClassExpression(JavaScriptParser.ClassExpressionContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.associateComments(ctx);
            ruleIndex.pop();
            FileCreator.appendToVxlFile("\t</class>\n");
        }
    }

    /**
     * Extrai o metodo
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterMethodDefinition(JavaScriptParser.MethodDefinitionContext ctx) {
        String methodIdentifier;

        try {
            methodIdentifier = this.tokens.getText(ctx.propertyName());

            if (ExtractorOptions.isVocabularyTxtEnabled()) {
                FileCreator.appendToVocabularyTxtFile("method " + methodIdentifier + "\n");
            }

            if (ExtractorOptions.isVxlEnabled()) {
                ruleIndex.push(ctx.getStart().getLine());

                FileCreator.appendToVxlFile("\t<mth name=\"" + methodIdentifier + "\" >\n");
            }

            this.methodExists = true;
        } catch (NullPointerException e) {

        }
    }

    /**
     * Associa os comentarios ao metodo
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitMethodDefinition(JavaScriptParser.MethodDefinitionContext ctx) {
        if(this.methodExists) {
            if (ExtractorOptions.isVxlEnabled()) {
                commentsExtractor.associateComments(ctx);
                ruleIndex.pop();
                FileCreator.appendToVxlFile("\t</mth>\n");
            }

            this.methodExists = false;
        }
    }
}
