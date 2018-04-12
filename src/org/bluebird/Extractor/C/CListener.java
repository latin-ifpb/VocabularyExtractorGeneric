package org.bluebird.Extractor.C;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.bluebird.Extractor.CallGraph;
import org.bluebird.Extractor.CommentsExtractor;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtil.C.CBaseListener;
import org.bluebird.LanguagesUtil.C.CParser;

import java.util.Stack;

public class CListener extends CBaseListener {

    private TokenStream tokens;
    private String funcaoAtual = null;
    private CallGraph callGraph;
    private Stack<Integer> ruleIndex;
    private CommentsExtractor commentsExtractor;

    /**
     * Construtor do Listener da Linguagem C
     * @param parser Parser do C
     * @param tokenStream Token Stream do Arquivo C
     */
    CListener(CParser parser, BufferedTokenStream tokenStream) {
        this.tokens = parser.getTokenStream();
        this.callGraph = new CallGraph();
        this.commentsExtractor = new CommentsExtractor(tokenStream);
        this.ruleIndex = new Stack<>();
    }

    /**
     * Inicia a extração do arquivo C e pega todos comentarios do arquivo
     * @param ctx A Parser Tree
     */
    @Override
    public void enterCompilationUnit(CParser.CompilationUnitContext ctx) {
        commentsExtractor.getAllComments(ctx, 4);
        this.ruleIndex.push(1);
    }

    /**
     * Termina a extração do arquivo C
     * @param ctx A Parser Tree
     */
    @Override
    public void exitCompilationUnit(CParser.CompilationUnitContext ctx) {
        commentsExtractor.associateComments(ctx);
    }

    /**
     * Extrai as informações das funções no arquivo C
     * @param ctx A Parser Tree
     */
    @Override
    public void enterFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        String functionName = this.tokens.getText(ctx.declarator().directDeclarator().directDeclarator());
        String functionType = "";

        for (CParser.DeclarationSpecifierContext type : ctx.declarationSpecifiers().declarationSpecifier()) {
            functionType = this.tokens.getText(type.typeSpecifier());
        }

        FileCreator.appendToXmlFile("<fnc name=\"" + functionName + "\" type=\"" + functionType + "\" >\n");
    }

    /**
     * Extrai as informações das funções no arquivo C
     * @param ctx A Parser Tree
     */
    @Override
    public void exitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToXmlFile("</fnc>\n");
    }

    /**
     * Extrai os argumentos das funções no arquivo C
     * @param ctx A Parser Tree
     */
    @Override
    public void enterParameterDeclaration(CParser.ParameterDeclarationContext ctx) {
        if (ctx.declarator() != null) {
            String argName = this.tokens.getText(ctx.declarator());
            String functionType = "";

            for (CParser.DeclarationSpecifierContext type : ctx.declarationSpecifiers().declarationSpecifier()) {
                functionType = this.tokens.getText(type.typeSpecifier());
            }

            FileCreator.appendToXmlFile("\t<arg name=\"" + argName + "\" type=\"" + functionType + "\" ></arg>\n");
        }
    }
}
