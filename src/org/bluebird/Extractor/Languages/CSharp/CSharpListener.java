package org.bluebird.Extractor.Languages.CSharp;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.bluebird.Extractor.CallGraph;
import org.bluebird.Extractor.CommentsExtractor;
import org.bluebird.Extractor.ReadFile;
import org.bluebird.Extractor.Setup.ExtractorInit;
import org.bluebird.Extractor.Setup.ExtractorOptions;
import org.bluebird.FileUtils.FileBrowser;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtils.CSharp.CSharpLexer;
import org.bluebird.LanguagesUtils.CSharp.CSharpParser;
import org.bluebird.LanguagesUtils.CSharp.CSharpParserBaseListener;
import org.bluebird.Utils.ClocCounter;

import javax.print.attribute.Attribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CSharpListener extends CSharpParserBaseListener {

    private TokenStream tokens;
    private String modifiers = "default";
    private StringBuilder args = new StringBuilder();
    private String funcaoAtual = null;
    private CallGraph callGraph;
    private Stack<Integer> ruleIndex;
    private CommentsExtractor commentsExtractor;
    private String type = "default";
    private ArrayList<String> comentario;
    private ExtractorInit extractorInit;
    private int lineCompile;
    private StringBuilder pathFile = new StringBuilder();
    private ArrayList<String> listCabecalho = new ArrayList<>();
    private String typeOfEntities = "default";
    private String accessEntity = "default"; // acesso das entidades
    private String modifiersEntity = "default"; // modificadores das entidades

    /**
     * Construtor do Listener da Linguagem C#
     *
     * @param parser      Parser do C#
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
     *
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
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterCompilation_unit(CSharpParser.Compilation_unitContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            commentsExtractor.getAllComments(ctx, CSharpLexer.COMMENTS_CHANNEL);
            this.ruleIndex.push(1);
            String dirPath = ExtractorInit.getNamePath();
            String nameFile = dirPath + "/" + FileBrowser.getActualFile();
            lineCompile = ctx.getStart().getLine();
            ReadFile.lerArquivo(lineCompile, nameFile); //ATENÇÃO
            listCabecalho = ReadFile.getListCabecalho();
            addComentario(listCabecalho);
        }
    }

    /**
     * Termina a extração do arquivo C#
     *
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
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterNamespace_declaration(CSharpParser.Namespace_declarationContext ctx) {
        String namespaceIdentifier = this.tokens.getText(ctx.qualified_identifier().getSourceInterval());
        commentsExtractor.getCommentsEntidadeCSharp(ctx);
        int slocNamespace = ClocCounter.lineCount(ctx);
        if (ExtractorOptions.isVocabularyTxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("namespace " + namespaceIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            ruleIndex.push(ctx.getStart().getLine());
            FileCreator.appendToVxlFile("\t<nmspc name=\"" + namespaceIdentifier + "\" cloc=\"" + slocNamespace + "\">\n");
        }
    }

    /**
     * Associa comentarios ao namespace
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitNamespace_declaration(CSharpParser.Namespace_declarationContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            //commentsExtractor.associateComments(ctx);
            comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
            addComentario(comentario);
            ruleIndex.pop();
            FileCreator.appendToVxlFile("\t</nmspc>\n");
        }
    }

    /**
     * Extrai a classe
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterClass_definition(CSharpParser.Class_definitionContext ctx) {
        String classIdentifier = this.tokens.getText(ctx.identifier());
        commentsExtractor.getCommentsEntidadeCSharp(ctx);
        int slocClass = ClocCounter.lineCount(ctx);

        if (ExtractorOptions.isVocabularyTxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("class " + classIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            ruleIndex.push(ctx.getStart().getLine());
            FileCreator.appendToVxlFile("\t\t<class name=\"" + classIdentifier + "\" acess=\"" + this.modifiers + "\" cloc=\""+ slocClass + "\">\n");
            this.modifiers = "default";
        }
    }

    /**
     * Associa comentarios a classe
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitClass_definition(CSharpParser.Class_definitionContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            //commentsExtractor.associateComments(ctx);
            comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
            addComentario(comentario);
            ruleIndex.pop();
            FileCreator.appendToVxlFile("\t\t</class>\n");
        }
    }

    /**
     * Extrai o metodo
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterMethod_declaration(CSharpParser.Method_declarationContext ctx) {
        String methodIdentifier = this.tokens.getText(ctx.method_member_name().identifier(0));
        commentsExtractor.getCommentsEntidadeCSharp(ctx);
        int slocMethod = ClocCounter.lineCount(ctx);

        if (ExtractorOptions.isVocabularyTxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("method " + methodIdentifier + "\n");
        }

        if (ExtractorOptions.isCallGraphEnabled()) {
            this.funcaoAtual = methodIdentifier;
            callGraph.setNodes(methodIdentifier);
        }

        if (ExtractorOptions.isVxlEnabled()) {
            try {
                returnArgsType(ctx.formal_parameter_list().fixed_parameters());
            } catch (NullPointerException e) {
                args.append("");
            }

            FileCreator.appendToVxlFile("\t\t\t<mth name=\"" + methodIdentifier + "(" + this.args + ")" + "\" acess=\"" +
                    this.modifiers + "\" cloc=\"" + slocMethod +  "\" type=\"" + this.typeOfEntities + "\">\n");
            this.modifiers = "default";
            this.typeOfEntities = "default";
            this.args.setLength(0);

        }
    }

    /**
     * Associa comentarios aos metodos
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitMethod_declaration(CSharpParser.Method_declarationContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            //commentsExtractor.associateComments(ruleIndex.peek(), ctx);
            comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
            addComentario(comentario);
            this.funcaoAtual = null;
            FileCreator.appendToVxlFile("\t\t\t</mth>\n");
        }
    }

    /**
     * Extrai o atributo
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterField_declaration(CSharpParser.Field_declarationContext ctx) {
        String fieldIdentifier = "";
        commentsExtractor.getCommentsEntidadeCSharp(ctx);
        for (CSharpParser.Variable_declaratorContext atributo : ctx.variable_declarators().variable_declarator()) {
            fieldIdentifier = this.tokens.getText(atributo.identifier().getSourceInterval());
        }

        if (ExtractorOptions.isVocabularyTxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("field " + fieldIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t<field name=\"" + fieldIdentifier + "\" acess=\"" + this.modifiers + "\"  type=\"" + this.typeOfEntities + "\">\n");
            this.modifiers = "default";
            this.typeOfEntities = "default";
        }
    }

    /**
     * Associa comentarios aos atributos
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitField_declaration(CSharpParser.Field_declarationContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            //commentsExtractor.associateComments(ctx);
            comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
            addComentario(comentario);
            FileCreator.appendToVxlFile("\t\t\t</field>\n");
        }
    }

    /**
     * Extrai o constructor
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterConstructor_declaration(CSharpParser.Constructor_declarationContext ctx) {
        String constructorIdentifier = this.tokens.getText(ctx.identifier());
        commentsExtractor.getCommentsEntidadeCSharp(ctx);
        int slocConstructor = ClocCounter.lineCount(ctx);

        if (ExtractorOptions.isVocabularyTxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("constructor " + constructorIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            try {
                returnArgsType(ctx.formal_parameter_list().fixed_parameters());
            } catch (NullPointerException e) {
                args.setLength(0);
            }

            FileCreator.appendToVxlFile("\t\t\t<constr name=\"" + constructorIdentifier + "(" + this.args + ")" +
                    "\" acess=\"" + this.modifiers + "\" cloc=\"" + slocConstructor + "\">\n");
            this.modifiers = "default";
            this.args.setLength(0);
        }
    }

    /**
     * Associa comentarios ao constructor
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitConstructor_declaration(CSharpParser.Constructor_declarationContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
            addComentario(comentario);
            //commentsExtractor.associateComments(ctx);
            FileCreator.appendToVxlFile("\t\t\t</constr>\n");
        }
    }

    @Override
    public void enterAll_member_modifiers(CSharpParser.All_member_modifiersContext ctx) {
        String temp;

        for (int i = 0; i < ctx.all_member_modifier().size(); i++) {
            temp = this.tokens.getText(ctx.all_member_modifier(i));

            if (temp.equals("private") || temp.equals("public") || (temp.equals("protected"))) {
                this.modifiers = temp;
            }
        }
        this.typeOfEntities = "default";
    }

    /**
     * Extrai os getters e setters
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterProperty_declaration(CSharpParser.Property_declarationContext ctx) {
        String propertyIdentifier = this.tokens.getText(ctx.member_name().namespace_or_type_name().identifier(0));
        commentsExtractor.getCommentsEntidadeCSharp(ctx);

        if (ExtractorOptions.isVocabularyTxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("property " + propertyIdentifier + "\n");
        }

        if (ExtractorOptions.isCallGraphEnabled()) {
            this.funcaoAtual = propertyIdentifier;
            callGraph.setNodes(propertyIdentifier);
        }

        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t<prpty name=\"" + propertyIdentifier + "\" acess=\"" + this.modifiers + "\" type=\"" + this.typeOfEntities + "\">\n");
            this.modifiers = "default";
            this.typeOfEntities = "default";
        }
    }


    /**
     * Associa comentarios aos propertys
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitProperty_declaration(CSharpParser.Property_declarationContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            //commentsExtractor.associateComments(ctx);
            comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
            addComentario(comentario);
            this.funcaoAtual = null;
            FileCreator.appendToVxlFile("\t\t\t</prpty>\n");
        }
    }

    /**
     * Extrai os identifiers dos argumentos dos metodos
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterArg_declaration(CSharpParser.Arg_declarationContext ctx) {
        String argIdentifier = this.tokens.getText(ctx.identifier());

        if (ExtractorOptions.isVocabularyTxtEnabled()) {
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
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterLocal_variable_declaration(CSharpParser.Local_variable_declarationContext ctx) {
        String variableType = this.tokens.getText(ctx.local_variable_type());
        String type = this.tokens.getText(ctx.local_variable_type());
        String variableIdentifier = this.tokens.getText(ctx.local_variable_declarator(0).identifier());

        if (ExtractorOptions.isVocabularyTxtEnabled()) {
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
     *
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
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterStruct_definition(CSharpParser.Struct_definitionContext ctx) {
        String structIdentifier = this.tokens.getText(ctx.identifier());
        commentsExtractor.getCommentsEntidadeCSharp(ctx);
        int slocStruct = ClocCounter.lineCount(ctx);
        if (ExtractorOptions.isVocabularyTxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("struct " + structIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t<struct name=\"" + structIdentifier + "\" acess=\"" + this.modifiers +
                    "\" cloc=\"" + slocStruct + "\">\n");
            this.modifiers = "default";
        }
    }

    /**
     * Associa comentarios ao struct
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitStruct_definition(CSharpParser.Struct_definitionContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            //commentsExtractor.associateComments(ctx);
            comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
            addComentario(comentario);
            FileCreator.appendToVxlFile("\t\t\t</struct>\n");
        }
    }

    /**
     * Extrai o enum
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterEnum_definition(CSharpParser.Enum_definitionContext ctx) {
        String enumIdentifier = this.tokens.getText(ctx.identifier()), enumVariable;
        commentsExtractor.getCommentsEntidadeCSharp(ctx);
        int slocEnum = ClocCounter.lineCount(ctx);

        if (ExtractorOptions.isVocabularyTxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("enum " + enumIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t<enum name=\"" + enumIdentifier + "\" acess=\"" + this.modifiers +
                    "\" cloc=\"" + slocEnum + "\">\n");
            for (CSharpParser.Enum_member_declarationContext variable : ctx.enum_body().enum_member_declaration()) {
                enumVariable = this.tokens.getText(variable.identifier());
                FileCreator.appendToVxlFile("\t\t\t\t<lvar name=\"" + enumVariable + "\"></lvar>\n");
            }
            this.modifiers = "default";
        }
    }

    /**
     * Fecha tag do enum
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitEnum_definition(CSharpParser.Enum_definitionContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
            addComentario(comentario);
            FileCreator.appendToVxlFile("\t\t\t</enum>\n");
        }
    }

    /**
     * Extrai a interface
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterInterface_definition(CSharpParser.Interface_definitionContext ctx) {
        String interfaceIdentifier = this.tokens.getText(ctx.identifier());
        commentsExtractor.getCommentsEntidadeCSharp(ctx);
        int slocInterface = ClocCounter.lineCount(ctx);

        if (ExtractorOptions.isVocabularyTxtEnabled()) {
            FileCreator.appendToVocabularyTxtFile("interface " + interfaceIdentifier + "\n");
        }

        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t<intfc name=\"" + interfaceIdentifier + "\" acess=\"" +
                    this.modifiers + "\" cloc=\"" + slocInterface + "\">\n");
            this.modifiers = "default";
        }
    }

    /**
     * Fecha tag da interface
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void exitInterface_definition(CSharpParser.Interface_definitionContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            comentario = commentsExtractor.associarComentario(ctx.getStart().getLine(), ctx.getStop().getLine());
            addComentario(comentario);
            FileCreator.appendToVxlFile("\t\t\t</intfc>\n");
        }
    }

    /**
     * Extrai chamadas de metodos
     *
     * @param ctx Entidade da Parser Tree
     */
    @Override
    public void enterPrimary_expression(CSharpParser.Primary_expressionContext ctx) {
        String mth;

        if (ExtractorOptions.isCallGraphEnabled()) {
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
    @Override
    public void enterConstant_declarator(CSharpParser.Constant_declaratorContext ctx) {
        String constantes = this.tokens.getText(ctx.identifier());
        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t<const name=\"" + constantes + "\" acess=\"" +
                    this.modifiers + "\" type=\"" + this.typeOfEntities + "\">\n");
            this.modifiers = "default";
            this.typeOfEntities = "default";
        }
    }

    @Override
    public void exitConstant_declarator(CSharpParser.Constant_declaratorContext ctx) {
        if (ExtractorOptions.isVxlEnabled()) {
            FileCreator.appendToVxlFile("\t\t\t</const>\n");
        }
    }
    @Override
    public void enterAttribute(CSharpParser.AttributeContext ctx) {
       String identifierAtribute = ctx.getStart().getText();
        FileCreator.appendToVxlFile("\t\t\t<atrbt name=\"" + identifierAtribute + "\">\n");
    }

    @Override public void exitAttribute(CSharpParser.AttributeContext ctx) {
        FileCreator.appendToVxlFile("\t\t\t</atrbt>\n");
    }

    @Override
    public void enterAttribute_argument(CSharpParser.Attribute_argumentContext ctx) {
        String attributeArgs = this.tokens.getText(ctx.expression());
        attributeArgs = attributeArgs.replace("\"", "");
        FileCreator.appendToVxlFile("\t\t\t\t<args descr=\"" + attributeArgs + "\"></args>\n");
    }
    @Override
    public void enterIsType(CSharpParser.IsTypeContext ctx) {
        CSharpParser.Base_typeContext  base_typeContext = ctx.base_type();
    }

    @Override
    public void enterType(CSharpParser.TypeContext ctx) {
        typeOfEntities = this.tokens.getText(ctx.base_type());
    }

    public void addComentario (ArrayList<String> listComentario) {
        if (listComentario.size() !=0) {
            for (String cmt: listComentario) {
                FileCreator.appendToVxlFile("\t\t\t<cmmt descr=\"" + cmt + "\"></cmmt>\n");
            }
        }
    }

}
