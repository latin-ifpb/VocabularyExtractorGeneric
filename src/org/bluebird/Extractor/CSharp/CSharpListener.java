package org.bluebird.Extractor.CSharp;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.bluebird.Extractor.CallGraph;
import org.bluebird.Extractor.CommentsExtractor;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtil.CSharp.CSharpParser;
import org.bluebird.LanguagesUtil.CSharp.CSharpParserBaseListener;
import org.bluebird.UserInterface.App.AppController;

import java.util.Stack;

public class CSharpListener extends CSharpParserBaseListener {

    private TokenStream tokens;
    private String modifiers = "default";
    private StringBuilder args = new StringBuilder();
    private String funcaoAtual = null;
    private CallGraph callGraph;
    private Stack<Integer> ruleIndex;
    private CommentsExtractor commentsExtractor;

    //Construtor que passa o parser
    CSharpListener(CSharpParser parser, BufferedTokenStream tokenStream) {
        this.tokens = parser.getTokenStream();
        this.callGraph = new CallGraph();
        this.commentsExtractor = new CommentsExtractor(tokenStream);
        this.ruleIndex = new Stack<>();
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

    //Extrai o namespace
    @Override
    public void enterNamespace_declaration(CSharpParser.Namespace_declarationContext ctx) {
        String namespaceIdentifier = this.tokens.getText(ctx.qualified_identifier().getSourceInterval());

        ruleIndex.push(ctx.getStart().getLine());
        FileCreator.appendToXmlFile("\t<nmspc name=\"" + namespaceIdentifier + "\">\n");
    }

    @Override
    public void exitNamespace_declaration(CSharpParser.Namespace_declarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        ruleIndex.pop();
        FileCreator.appendToXmlFile("\t</nmspc>\n");
    }

    //Extrai a classe
    @Override
    public void enterClass_definition(CSharpParser.Class_definitionContext ctx) {
        String classIdentifier = this.tokens.getText(ctx.identifier());

        ruleIndex.push(ctx.getStart().getLine());
        FileCreator.appendToXmlFile("\t\t<class name=\"" + classIdentifier + "\" acess=\"" + this.modifiers + "\">\n");
        this.modifiers = "default";
    }

    @Override
    public void exitClass_definition(CSharpParser.Class_definitionContext ctx) {
        commentsExtractor.associateComments(ctx);
        ruleIndex.pop();
        FileCreator.appendToXmlFile("\t\t</class>\n");
    }

    //Extrai o metodo e os parametros
    @Override
    public void enterMethod_declaration(CSharpParser.Method_declarationContext ctx) {
        String methodIdentifier = this.tokens.getText(ctx.method_member_name().identifier(0));

        if(AppController.getCallGraphCheck()) {
            this.funcaoAtual = methodIdentifier;
            callGraph.setNodes(methodIdentifier);
        }

        try {
            returnArgsType(ctx.formal_parameter_list().fixed_parameters());
        } catch (NullPointerException e) {
            args.append("");
        }

        FileCreator.appendToXmlFile("\t\t\t<mth name=\"" + methodIdentifier + "(" + this.args + ")" + "\" acess=\"" +
                this.modifiers + "\">\n");
        this.modifiers = "default";
    }

    @Override
    public void exitMethod_declaration(CSharpParser.Method_declarationContext ctx) {
        commentsExtractor.associateComments(ruleIndex.peek(), ctx);
        this.funcaoAtual = null;
        FileCreator.appendToXmlFile("\t\t\t</mth>\n");
    }

    //Extrai os atributos
    @Override
    public void enterField_declaration(CSharpParser.Field_declarationContext ctx) {
        String fieldIdentifier = "";

        for (CSharpParser.Variable_declaratorContext atributo : ctx.variable_declarators().variable_declarator()) {
            fieldIdentifier = this.tokens.getText(atributo.identifier().getSourceInterval());
        }
        FileCreator.appendToXmlFile("\t\t\t<field name=\"" + fieldIdentifier + "\" acess=\"" + this.modifiers + "\" >\n");
        this.modifiers = "default";
    }

    @Override
    public void exitField_declaration(CSharpParser.Field_declarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToXmlFile("\t\t\t</field>\n");
    }

    //Extrai o construtor
    @Override
    public void enterConstructor_declaration(CSharpParser.Constructor_declarationContext ctx) {
        String constructorIdentifier = this.tokens.getText(ctx.identifier());

        try {
            returnArgsType(ctx.formal_parameter_list().fixed_parameters());
        } catch (NullPointerException e) {
            args.setLength(0);
        }

        FileCreator.appendToXmlFile("\t\t\t<constr name=\"" + constructorIdentifier + "(" + this.args + ")" +
                "\" acess=\"" + this.modifiers + "\">\n");
        this.modifiers = "default";
    }

    @Override
    public void exitConstructor_declaration(CSharpParser.Constructor_declarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToXmlFile("\t\t\t</constr>\n");
    }

    //Extrai os modificadores de atributos e metodos da classe
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

    //Extrai os getters e setters
    @Override
    public void enterProperty_declaration(CSharpParser.Property_declarationContext ctx) {
        String propertyIdentifier = this.tokens.getText(ctx.member_name().namespace_or_type_name().identifier(0));
        if(AppController.getCallGraphCheck()) {
            this.funcaoAtual = propertyIdentifier;
            callGraph.setNodes(propertyIdentifier);
        }

        FileCreator.appendToXmlFile("\t\t\t<prpty name=\"" + propertyIdentifier + "\" acess=\"" + this.modifiers + "\">\n");
        this.modifiers = "default";
    }

    @Override
    public void exitProperty_declaration(CSharpParser.Property_declarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        this.funcaoAtual = null;
        FileCreator.appendToXmlFile("\t\t\t</prpty>\n");
    }

    @Override
    public void enterArg_declaration(CSharpParser.Arg_declarationContext ctx) {
        String argIdentifier = this.tokens.getText(ctx.identifier());

        argIdentifier = argIdentifier.replace("<", "&lt;");
        argIdentifier = argIdentifier.replace(">", "&gt;");

        FileCreator.appendToXmlFile("\t\t\t\t<arg name=\"" + argIdentifier + "\"></arg>\n");
    }

    // Extrai a variavel local da classe
    @Override
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

    // Extrai Struct
    @Override
    public void enterStruct_definition(CSharpParser.Struct_definitionContext ctx) {
        String structIdentifier = this.tokens.getText(ctx.identifier());
        FileCreator.appendToXmlFile("\t\t\t<struct name=\"" + structIdentifier +
                "\" acess=\"" + this.modifiers + "\">\n");
        this.modifiers = "default";
    }

    @Override
    public void exitStruct_definition(CSharpParser.Struct_definitionContext ctx) {
        FileCreator.appendToXmlFile("\t\t\t</struct>\n");
    }

    //Extrai Enum
    @Override
    public void enterEnum_definition(CSharpParser.Enum_definitionContext ctx) {
        String enumIdentifier = this.tokens.getText(ctx.identifier());
        FileCreator.appendToXmlFile("\t\t\t<enum name=\"" + enumIdentifier +
                "\" acess=\"" + this.modifiers + "\">\n");
        this.modifiers = "default";
    }

    @Override
    public void exitEnum_definition(CSharpParser.Enum_definitionContext ctx) {
        FileCreator.appendToXmlFile("\t\t\t</enum>\n");
    }

    //Extrai Interface
    @Override
    public void enterInterface_definition(CSharpParser.Interface_definitionContext ctx) {
        String interfaceIdentifier = this.tokens.getText(ctx.identifier());
        FileCreator.appendToXmlFile("\t\t\t<intfc name=\"" + interfaceIdentifier +
                "\" acess=\"" + this.modifiers + "\">\n");
        this.modifiers = "default";
    }

    @Override
    public void exitInterface_definition(CSharpParser.Interface_definitionContext ctx) {
        FileCreator.appendToXmlFile("\t\t\t</intfc>\n");
    }

    @Override
    public void enterPrimary_expression(CSharpParser.Primary_expressionContext ctx) {
        String mth;

        if(AppController.getCallGraphCheck()) {
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
