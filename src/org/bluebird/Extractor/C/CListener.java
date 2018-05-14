package org.bluebird.Extractor.C;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.bluebird.Extractor.CallGraph;
import org.bluebird.Extractor.CommentsExtractor;
import org.bluebird.Extractor.Setup.ExtractorOptions;
import org.bluebird.FileUtils.FileBrowser;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtil.C.CBaseListener;
import org.bluebird.LanguagesUtil.C.CParser;

import java.util.Stack;

public class CListener extends CBaseListener {

    private TokenStream tokens;
    private CallGraph callGraph;
    private Stack<Integer> ruleIndex;
    private CommentsExtractor commentsExtractor;
    private final int COMMENTS_CHANNEL = 4;

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
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterCompilationUnit(CParser.CompilationUnitContext ctx) {
        if (ExtractorOptions.isVocabularytxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("file " + FileBrowser.getActualFile() + "\n");
        }

        if(ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("<file name=\"" + FileBrowser.getActualFile() + "\" >\n");
            commentsExtractor.getAllComments(ctx, COMMENTS_CHANNEL);
            this.ruleIndex.push(1);
        }
    }

    /**
     * Termina a extração do arquivo C
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitCompilationUnit(CParser.CompilationUnitContext ctx) {
        if(ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.associateComments(ctx);
            FileCreator.appendToVxlFile("</file>\n");
        }
    }

    /**
     * Extrai as informações das funções no arquivo C
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        String functionIdentifier = this.tokens.getText(ctx.functionIdentifier());
        StringBuilder functionType = new StringBuilder();

        if (ExtractorOptions.isVocabularytxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("function " + functionIdentifier + "\n");
        }

        if(ExtractorOptions.isVxlEnabled()) {
            for (CParser.DeclarationSpecifierContext type : ctx.declarationSpecifiers().declarationSpecifier()) {
                if (type.typeSpecifier() != null) {
                    functionType.append(this.tokens.getText(type.typeSpecifier()));
                    functionType.append(" ");
                }
            }

            FileCreator.appendToVxlFile("\t<fnc name=\"" + functionIdentifier + "\" type=\"" + functionType + "\" >\n");
        }
    }

    /**
     * Associa os comentarios a funcao
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitFunctionDefinition(CParser.FunctionDefinitionContext ctx) {
        if(ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.associateComments(ctx);
            FileCreator.appendToVxlFile("\t</fnc>\n");
        }
    }

    /**
     * Extrai as variaveis locais
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterBlockItem(CParser.BlockItemContext ctx) {
        StringBuilder varType = new StringBuilder();
        String varIdentifier;

        if(ctx.declaration() != null) {
            varIdentifier = this.tokens.getText(ctx.declaration().initDeclaratorList().initDeclarator().declarator());

            if (ExtractorOptions.isVocabularytxtEnabled()) {
                FileCreator.appendToVocabularyTxtFile("lvar " + varIdentifier + "\n");
            }

            if(ExtractorOptions.isVxlEnabled()) {
                for(CParser.DeclarationSpecifierContext type : ctx.declaration().declarationSpecifiers().declarationSpecifier()) {
                    varType.append(this.tokens.getText(type));
                }

                FileCreator.appendToVxlFile("\t\t<lvar name=\"" + varIdentifier + "\" type=\"" + varType + "\" ></lvar>\n");
            }
        }
    }


    /**
     * Extrai os argumentos das funções no arquivo C
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterParameterDeclaration(CParser.ParameterDeclarationContext ctx) {
        if (ctx.declarator() != null) {
            String argIdentifier = this.tokens.getText(ctx.declarator());
            StringBuilder functionType = new StringBuilder();

            if (ExtractorOptions.isVocabularytxtEnabled()) {
                FileCreator.appendToVocabularyTxtFile("arg " + argIdentifier + "\n");
            }

            if(ExtractorOptions.isVxlEnabled()) {
                try {
                    for (CParser.DeclarationSpecifierContext type : ctx.declarationSpecifiers().declarationSpecifier()) {
                        functionType.append(this.tokens.getText(type.typeSpecifier()));
                    }
                } catch (NullPointerException e) {
                    functionType.append("");
                }

                FileCreator.appendToVxlFile("\t\t<arg name=\"" + argIdentifier + "\" type=\"" + functionType + "\" ></arg>\n");
            }
        }
    }

    /**
     * Extrai struct ou enum
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterStructOrUnionSpecifier(CParser.StructOrUnionSpecifierContext ctx) {
        String structOrUnion;
        String structOrUnionIdentifier;

        if (ctx.structDeclarationList() != null) {
            structOrUnion = this.tokens.getText(ctx.structOrUnion());
            structOrUnionIdentifier = ctx.Identifier().toString();

            if (ExtractorOptions.isVocabularytxtEnabled()) {
                FileCreator.appendToVocabularyTxtFile(structOrUnion+ " " + structOrUnionIdentifier + "\n");
            }

            if(ExtractorOptions.isVxlEnabled()) {
                FileCreator.appendToVxlFile("<" + structOrUnion + " name=\"" + structOrUnionIdentifier + "\" >\n");
            }
        }
    }

    /**
     * Associa os comentario ao struct ou union
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitStructOrUnionSpecifier(CParser.StructOrUnionSpecifierContext ctx) {
        if(ExtractorOptions.isVxlEnabled()) {
            String structOrUnion;
            commentsExtractor.associateComments(ctx);

            if (ctx.structDeclarationList() != null) {
                structOrUnion = this.tokens.getText(ctx.structOrUnion());
                FileCreator.appendToVxlFile("</" + structOrUnion + ">\n");
            }
        }
    }

}
