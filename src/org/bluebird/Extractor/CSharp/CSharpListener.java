package org.bluebird.Extractor.CSharp;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.bluebird.Extractor.CallGraph;
import org.bluebird.Extractor.CommentsExtractor;
import org.bluebird.Extractor.Setup.ExtractorOptions;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtil.CSharp.CSharpLexer;
import org.bluebird.LanguagesUtil.CSharp.CSharpParser;
import org.bluebird.LanguagesUtil.CSharp.CSharpParserBaseListener;

import java.util.Stack;

public class CSharpListener extends CSharpParserBaseListener {

    private TokenStream tokens;
    private String modifiers = "default";
    private StringBuilder args = new StringBuilder();
    private String funcaoAtual = null;
    private CallGraph callGraph;
    private Stack<Integer> ruleIndex;
    private CommentsExtractor commentsExtractor;

    /**
     * Construtor do Listener da Linguagem C#
     * @param parser Parser do C#
     * @param tokenStream Token Stream do Arquivo C#
     */
    CSharpListener(CSharpParser parser, BufferedTokenStream tokenStream) {
        this.tokens = parser.getTokenStream();
        this.callGraph = new CallGraph();
        this.commentsExtractor = new CommentsExtractor(tokenStream);
        this.ruleIndex = new Stack<>();
    }

    /**
     * Extrai os tipos dos argumentos dos metodos
     * @param ctx Entidade da Parser Tree
     */
    private void returnArgsType(CSharpParser.Fixed_parametersContext ctx) {
        String temp;

        args.setLength(0);
        for (int i = 0; i < ctx.fixed_parameter().size(); i++) {
            if (i < ctx.fixed_parameter().size() - 1) {
                temp = this.tokens.getText(ctx.fixed_parameter(i).arg_declaration().type()) + ", ";
            } else {
                temp = this.tokens.getText(ctx.fixed_parameter(i).arg_declaration().type());
            }
            temp = temp.replace("<", "&lt;");
            temp = temp.replace(">", "&gt;");
            args.append(temp);
        }
    }

    /**
     * Inicia a extração do arquivo C# e pega todos comentarios do arquivo
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterCompilation_unit(CSharpParser.Compilation_unitContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.getAllComments(ctx, CSharpLexer.COMMENTS_CHANNEL);
            this.ruleIndex.push(1);
        }
    }

    /**
     * Termina a extração do arquivo C#
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitCompilation_unit(CSharpParser.Compilation_unitContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.associateComments(ctx);
        }
    }

    /**
     * Extrai o namespace
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterNamespace_declaration(CSharpParser.Namespace_declarationContext ctx) {
        String namespaceIdentifier = this.tokens.getText(ctx.qualified_identifier().getSourceInterval());

        if (ExtractorOptions.isVocabularytxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("namespace " + namespaceIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            ruleIndex.push(ctx.getStart().getLine());
            FileCreator.appendToVxlFile("\t<nmspc name=\"" + namespaceIdentifier + "\">\n");
        }
    }

    /**
     * Associa comentarios ao namespace
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitNamespace_declaration(CSharpParser.Namespace_declarationContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.associateComments(ctx);
            ruleIndex.pop();
            FileCreator.appendToVxlFile("\t</nmspc>\n");
        }
    }

    /**
     * Extrai a classe
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterClass_definition(CSharpParser.Class_definitionContext ctx) {
        String classIdentifier = this.tokens.getText(ctx.identifier());

        if (ExtractorOptions.isVocabularytxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("class " + classIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            ruleIndex.push(ctx.getStart().getLine());
            FileCreator.appendToVxlFile("\t\t<class name=\"" + classIdentifier + "\" acess=\"" + this.modifiers + "\">\n");
            this.modifiers = "default";
        }
    }

    /**
     * Associa comentarios a classe
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitClass_definition(CSharpParser.Class_definitionContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.associateComments(ctx);
            ruleIndex.pop();
            FileCreator.appendToVxlFile("\t\t</class>\n");
        }
    }

    /**
     * Extrai o metodo
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterMethod_declaration(CSharpParser.Method_declarationContext ctx) {
        String methodIdentifier = this.tokens.getText(ctx.method_member_name().identifier(0));

        if (ExtractorOptions.isVocabularytxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("method " + methodIdentifier + "\n");
        }

        if(ExtractorOptions.isCallGraphEnabled()) {
            this.funcaoAtual = methodIdentifier;
            callGraph.setNodes(methodIdentifier);
        }

        if(ExtractorOptions.isVxlEnabled()) {
            try {
                returnArgsType(ctx.formal_parameter_list().fixed_parameters());
            } catch (NullPointerException e) {
                args.append("");
            }

            FileCreator.appendToVxlFile("\t\t\t<mth name=\"" + methodIdentifier + "(" + this.args + ")" + "\" acess=\"" +
                    this.modifiers + "\">\n");
            this.modifiers = "default";
        }
    }

    /**
     * Associa comentarios aos metodos
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitMethod_declaration(CSharpParser.Method_declarationContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.associateComments(ruleIndex.peek(), ctx);
            this.funcaoAtual = null;
            FileCreator.appendToVxlFile("\t\t\t</mth>\n");
        }
    }

    /**
     * Extrai o atributo
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterField_declaration(CSharpParser.Field_declarationContext ctx) {
        String fieldIdentifier = "";

        for (CSharpParser.Variable_declaratorContext atributo : ctx.variable_declarators().variable_declarator()) {
            fieldIdentifier = this.tokens.getText(atributo.identifier().getSourceInterval());
        }

        if (ExtractorOptions.isVocabularytxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("field " + fieldIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t<field name=\"" + fieldIdentifier + "\" acess=\"" + this.modifiers + "\" >\n");
            this.modifiers = "default";
        }
    }

    /**
     * Associa comentarios aos atributos
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitField_declaration(CSharpParser.Field_declarationContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.associateComments(ctx);
            FileCreator.appendToVxlFile("\t\t\t</field>\n");
        }
    }

    /**
     * Extrai o constructor
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterConstructor_declaration(CSharpParser.Constructor_declarationContext ctx) {
        String constructorIdentifier = this.tokens.getText(ctx.identifier());

        if (ExtractorOptions.isVocabularytxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("constructor " + constructorIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            try {
                returnArgsType(ctx.formal_parameter_list().fixed_parameters());
            } catch (NullPointerException e) {
                args.setLength(0);
            }

            FileCreator.appendToVxlFile("\t\t\t<constr name=\"" + constructorIdentifier + "(" + this.args + ")" +
                    "\" acess=\"" + this.modifiers + "\">\n");
            this.modifiers = "default";
        }
    }

    /**
     * Associa comentarios ao constructor
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitConstructor_declaration(CSharpParser.Constructor_declarationContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.associateComments(ctx);
            FileCreator.appendToVxlFile("\t\t\t</constr>\n");
        }
    }

    /**
     * Extrai os modificadores de atributos e metodos da classe
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterAll_member_modifiers(CSharpParser.All_member_modifiersContext ctx) {
        String temp;

        for( int i = 0; i < ctx.all_member_modifier().size(); i++) {
            temp = this.tokens.getText(ctx.all_member_modifier(i));

            if (temp.equals("private") || temp.equals("public") || (temp.equals("protected"))) {
                this.modifiers = temp;
            }
        }
    }

    /**
     * Extrai os getters e setters
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterProperty_declaration(CSharpParser.Property_declarationContext ctx) {
        String propertyIdentifier = this.tokens.getText(ctx.member_name().namespace_or_type_name().identifier(0));

        if (ExtractorOptions.isVocabularytxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("property " + propertyIdentifier + "\n");
        }

        if(ExtractorOptions.isCallGraphEnabled()) {
            this.funcaoAtual = propertyIdentifier;
            callGraph.setNodes(propertyIdentifier);
        }

        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t<prpty name=\"" + propertyIdentifier + "\" acess=\"" + this.modifiers + "\">\n");
            this.modifiers = "default";
        }
    }

    /**
     * Associa comentarios aos propertys
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitProperty_declaration(CSharpParser.Property_declarationContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.associateComments(ctx);
            this.funcaoAtual = null;
            FileCreator.appendToVxlFile("\t\t\t</prpty>\n");
        }
    }

    /**
     * Extrai os identifiers dos argumentos dos metodos
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterArg_declaration(CSharpParser.Arg_declarationContext ctx) {
        String argIdentifier = this.tokens.getText(ctx.identifier());

        if (ExtractorOptions.isVocabularytxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("arg " + argIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            argIdentifier = argIdentifier.replace("<", "&lt;");
            argIdentifier = argIdentifier.replace(">", "&gt;");

            FileCreator.appendToVxlFile("\t\t\t\t<arg name=\"" + argIdentifier + "\"></arg>\n");
        }
    }

    /**
     * Extrai a variavel local do metodo
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterLocal_variable_declaration(CSharpParser.Local_variable_declarationContext ctx) {
        String variableType = this.tokens.getText(ctx.local_variable_type());
        String variableIdentifier = this.tokens.getText(ctx.local_variable_declarator(0).identifier());

        if (ExtractorOptions.isVocabularytxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("lvar " + variableIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            variableType = variableType.replace("<", "&lt;");
            variableType = variableType.replace(">", "&gt;");

            FileCreator.appendToVxlFile("\t\t\t<lvar name=\"" + variableIdentifier + "\" type=\"" + variableType + "\"></lvar>\n");
        }
    }

    /**
     * Extrai acesso a membros
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterMember_access(CSharpParser.Member_accessContext ctx) {
        String mth = this.tokens.getText(ctx.identifier().getSourceInterval());

        mth = mth.replace(".", "");
        mth = mth.replace("?", "");
        if (this.funcaoAtual != null && !mth.equals("WriteLine") && !mth.equals("ReadLine") && !mth.equals("Write")) {
            callGraph.setEdge(this.funcaoAtual, mth);
        }
    }

    /**
     * Extrai o struct
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterStruct_definition(CSharpParser.Struct_definitionContext ctx) {
        String structIdentifier = this.tokens.getText(ctx.identifier());

        if (ExtractorOptions.isVocabularytxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("struct " + structIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t<struct name=\"" + structIdentifier + "\" acess=\"" + this.modifiers + "\">\n");
            this.modifiers = "default";
        }
    }

    /**
     * Associa comentarios ao struct
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitStruct_definition(CSharpParser.Struct_definitionContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.associateComments(ctx);
            FileCreator.appendToVxlFile("\t\t\t</struct>\n");
        }
    }

    /**
     * Extrai o enum
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterEnum_definition(CSharpParser.Enum_definitionContext ctx) {
        String enumIdentifier = this.tokens.getText(ctx.identifier());

        if (ExtractorOptions.isVocabularytxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("enum " + enumIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t<enum name=\"" + enumIdentifier + "\" acess=\"" + this.modifiers + "\">\n");
            this.modifiers = "default";
        }
    }

    /**
     * Fecha tag do enum
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitEnum_definition(CSharpParser.Enum_definitionContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t</enum>\n");
        }
    }

    /**
     * Extrai a interface
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterInterface_definition(CSharpParser.Interface_definitionContext ctx) {
        String interfaceIdentifier = this.tokens.getText(ctx.identifier());

        if (ExtractorOptions.isVocabularytxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("interface " + interfaceIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t<intfc name=\"" + interfaceIdentifier + "\" acess=\"" + this.modifiers + "\">\n");
            this.modifiers = "default";
        }
    }

    /**
     * Fecha tag da interface
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitInterface_definition(CSharpParser.Interface_definitionContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t</intfc>\n");
        }
    }

    /**
     * Extrai chamadas de metodos
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterPrimary_expression(CSharpParser.Primary_expressionContext ctx) {
        String mth;

        if(ExtractorOptions.isCallGraphEnabled()) {
            for (int i = 0; i < ctx.method_invocation().size(); i++) {

                try {
                    this.tokens.getText(ctx.member_access(0));
                } catch (NullPointerException k) {
                    mth = this.tokens.getText(ctx.primary_expression_start().getSourceInterval());
                    mth = mth.replace(".", "");
                    mth = mth.replace("?", "");
                    if (this.funcaoAtual != null) {
                        callGraph.setEdge(this.funcaoAtual, mth);
                    }
                }
            }
        }
    }
}
