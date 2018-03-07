package org.bluebird.Extractor.CSharp;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.bluebird.Extractor.CallGraph;
import org.bluebird.Extractor.CommentsExtractor;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtil.CSharp.CSharpParser;
import org.bluebird.LanguagesUtil.CSharp.CSharpParserBaseListener;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

public class CSharpListener extends CSharpParserBaseListener {

    private TokenStream tokens;
    private String modifiers = "default";
    private StringBuilder args = new StringBuilder();
    private String funcaoAtual = null;
    private CallGraph callGraph;
    private CommentsExtractor commentsExtractor;

    /*Construtor que passa o parser*/
    CSharpListener(CSharpParser parser, BufferedTokenStream tokenStream) {
        this.tokens = parser.getTokenStream();
        this.callGraph = new CallGraph();
        this.commentsExtractor = new CommentsExtractor(tokenStream);
    }

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

    @Override
    public void enterCompilation_unit(CSharpParser.Compilation_unitContext ctx) {
        commentsExtractor.getAllComments(ctx);
    }

    @Override
    public void exitCompilation_unit(CSharpParser.Compilation_unitContext ctx) {
        commentsExtractor.associateComments(ctx);
    }

    @Override
    /* Extrai o namespace*/
    public void enterNamespace_declaration(CSharpParser.Namespace_declarationContext ctx) {
        String namespaceIdentifier = this.tokens.getText(ctx.qualified_identifier().getSourceInterval());

        FileCreator.appendToXmlFile("\t<nmspc name=\"" + namespaceIdentifier + "\">\n");
    }

    @Override
    public void exitNamespace_declaration(CSharpParser.Namespace_declarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToXmlFile("\t</nmspc>\n");
    }

    @Override
    /* Extrai a classe */
    public void enterClass_definition(CSharpParser.Class_definitionContext ctx) {
        String classIdentifier = this.tokens.getText(ctx.identifier());

        try {
            FileCreator.appendToXmlFile("\t\t<class name=\"" + classIdentifier + "\">\n");
        } catch (NullPointerException e) {  //Se der erro a classe nao tem parametro
            FileCreator.appendToXmlFile("\t\t<class name=\"" + classIdentifier + "\">\n");
        }
    }

    @Override
    public void exitClass_definition(CSharpParser.Class_definitionContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToXmlFile("\t\t</class>\n");
    }

    @Override
    /* Extrai o metodo e os parametros*/
    public void enterMethod_declaration(CSharpParser.Method_declarationContext ctx) {
        String methodIdentifier = this.tokens.getText(ctx.method_member_name().identifier(0));
        this.funcaoAtual = methodIdentifier;
        callGraph.setNodes(methodIdentifier);

        try {
            returnArgsType(ctx.formal_parameter_list().fixed_parameters());
        } catch (NullPointerException e) {
            args.append("");
        }

        FileCreator.appendToXmlFile("\t\t\t<mth name=\"" + methodIdentifier + "(" + this.args + ")" + "\" acess=\"" +
                this.modifiers + "\">\n");
    }

    @Override
    public void exitMethod_declaration(CSharpParser.Method_declarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        this.funcaoAtual = null;
        FileCreator.appendToXmlFile("\t\t\t</mth>\n");
    }

    @Override
    /* Extrai os atributos */
    public void enterField_declaration(CSharpParser.Field_declarationContext ctx) {
        String fieldIdentifier = "";

        for (CSharpParser.Variable_declaratorContext atributo : ctx.variable_declarators().variable_declarator()) {
            fieldIdentifier = this.tokens.getText(atributo.identifier().getSourceInterval());
        }
        FileCreator.appendToXmlFile("\t\t\t<field name=\"" + fieldIdentifier + "\" acess=\"" + this.modifiers + "\" ></field>\n");
    }

    @Override
    /* Extrai o construtor*/
    public void enterConstructor_declaration(CSharpParser.Constructor_declarationContext ctx) {
        String constructorIdentifier = this.tokens.getText(ctx.identifier());

        try {
            returnArgsType(ctx.formal_parameter_list().fixed_parameters());
        } catch (NullPointerException e) {
            args.setLength(0);
        }

        FileCreator.appendToXmlFile("\t\t\t<constr name=\"" + constructorIdentifier + "(" + this.args + ")" +
                "\" acess=\"" + this.modifiers + "\">\n");
    }

    @Override
    public void exitConstructor_declaration(CSharpParser.Constructor_declarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToXmlFile("\t\t\t</constr>\n");
    }

    @Override
    /* Extrai os modificadores de atributos e metodos da classe */
    public void enterClass_member_declaration(CSharpParser.Class_member_declarationContext ctx) {
        String temp;

        try {
            for (int i = 0; i < ctx.all_member_modifiers().all_member_modifier().size(); i++) {
                temp = this.tokens.getText(ctx.all_member_modifiers().all_member_modifier(i));

                if (temp.equals("private") || temp.equals("public") || (temp.equals("protected"))) {
                    this.modifiers = temp;
                }
            }
        } catch (NullPointerException e) {
            this.modifiers = "default";
        }
    }

    @Override
    /* Extrai os getters e setters*/
    public void enterProperty_declaration(CSharpParser.Property_declarationContext ctx) {
        String propertyIdentifier = this.tokens.getText(ctx.member_name().namespace_or_type_name().identifier(0));
        this.funcaoAtual = propertyIdentifier;
        callGraph.setNodes(propertyIdentifier);

        FileCreator.appendToXmlFile("\t\t\t<property name=\"" + propertyIdentifier + "\" acess=\"" + this.modifiers + "\">\n");
    }

    @Override
    public void exitProperty_declaration(CSharpParser.Property_declarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        this.funcaoAtual = null;
        FileCreator.appendToXmlFile("\t\t\t</property>\n");
    }

    @Override
    public void enterArg_declaration(CSharpParser.Arg_declarationContext ctx) {
        String argIdentifier = this.tokens.getText(ctx.identifier());

        argIdentifier = argIdentifier.replace("<", "&lt;");
        argIdentifier = argIdentifier.replace(">", "&gt;");

        FileCreator.appendToXmlFile("\t\t\t\t<arg name=\"" + argIdentifier + "\"></arg>\n");
    }

    @Override
    /* Extrai a variavel local da classe*/
    public void enterLocal_variable_declaration(CSharpParser.Local_variable_declarationContext ctx) {
        String variableType = this.tokens.getText(ctx.local_variable_type());
        String variableIdentifier = this.tokens.getText(ctx.local_variable_declarator(0).identifier());

        variableType = variableType.replace("<", "&lt;");
        variableType = variableType.replace(">", "&gt;");

        FileCreator.appendToXmlFile("\t\t\t<lvar name=\"" + variableIdentifier + "\" type=\"" + variableType + "\"></lvar>\n");
    }

    @Override
    public void enterMember_access(CSharpParser.Member_accessContext ctx) {
        String mth = this.tokens.getText(ctx.identifier().getSourceInterval());

        mth = mth.replace(".", "");
        mth = mth.replace("?", "");
        if (this.funcaoAtual != null && !mth.equals("WriteLine") && !mth.equals("ReadLine") && !mth.equals("Write")) {
            callGraph.setEdge(this.funcaoAtual, mth);
        }
    }
    @Override
    public void enterStruct_definition(CSharpParser.Struct_definitionContext ctx) {
        String nameStruct = this.tokens.getText(ctx.identifier());
    }
    @Override

    public void enterEnum_definition(CSharpParser.Enum_definitionContext ctx) {
        String nameEnum = this.tokens.getText(ctx.identifier());
    }

    public void enterInterface_definition(CSharpParser.Interface_definitionContext ctx) {
        String nameInterface = this.tokens.getText(ctx.identifier());
    }
    @Override
    public void enterPrimary_expression(CSharpParser.Primary_expressionContext ctx) {
        String mth;

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
