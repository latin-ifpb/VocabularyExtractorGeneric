package org.bluebird.Extractor.CSharp;

import org.antlr.v4.runtime.BufferedTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.bluebird.Extractor.CallGraph;
import org.bluebird.Extractor.CommentsExtractor;
import org.bluebird.FileUtils.FileCreator;
import org.bluebird.LanguagesUtil.CSharp.CSharpParser;
import org.bluebird.LanguagesUtil.CSharp.CSharpParserBaseListener;

public class CSharpListener extends CSharpParserBaseListener {

    private TokenStream tokens;
    private String modifiers = "default";
    private StringBuilder args = new StringBuilder();
    private String funcaoAtual = null;
    private CallGraph callGraph;
    private CommentsExtractor commentsExtractor;

    CSharpListener(CSharpParser parser, BufferedTokenStream tokenStream) {
        this.tokens = parser.getTokenStream();
        this.callGraph = new CallGraph();
        this.commentsExtractor = new CommentsExtractor(tokenStream);
    }/*Construtor que passa o parser*/

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
    public void enterNamespace_declaration(CSharpParser.Namespace_declarationContext ctx) {
        String namespaceIdentifier = this.tokens.getText(ctx.qualified_identifier().getSourceInterval());

        FileCreator.appendToXmlFile("\t<nmspc name=\"" + namespaceIdentifier + "\">\n");
    }/* Extrai o namespace*/

    @Override
    public void exitNamespace_declaration(CSharpParser.Namespace_declarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToXmlFile("\t</nmspc>\n");
    }

    @Override
    public void enterClass_definition(CSharpParser.Class_definitionContext ctx) {
        String classIdentifier = this.tokens.getText(ctx.identifier());
        try {
            FileCreator.appendToXmlFile("\t\t<class name=\"" + classIdentifier + "\">\n");
        } catch (NullPointerException e) {  //Se der erro a classe nao tem parametro
            FileCreator.appendToXmlFile("\t\t<class name=\"" + classIdentifier + "\">\n");
        }
    }/* Extrai a classe */

    @Override
    public void exitClass_definition(CSharpParser.Class_definitionContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToXmlFile("\t\t</class>\n");
    }

    @Override
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
    } /* Extrai o metodo e os parametros*/

    @Override
    public void exitMethod_declaration(CSharpParser.Method_declarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToXmlFile("\t\t\t</mth>\n");
    }

    @Override
    public void enterField_declaration(CSharpParser.Field_declarationContext ctx) {
        String fieldIdentifier = "";

        for (CSharpParser.Variable_declaratorContext atributo : ctx.variable_declarators().variable_declarator()) {
            fieldIdentifier = this.tokens.getText(atributo.identifier().getSourceInterval());
        }
        FileCreator.appendToXmlFile("\t\t\t<field name=\"" + fieldIdentifier + "\" acess=\"" + this.modifiers + "\" ></field>\n");
    }/* Extrai os atributos */

    @Override
    public void enterConstructor_declaration(CSharpParser.Constructor_declarationContext ctx) {
        String constructorIdentifier = this.tokens.getText(ctx.identifier());
        this.funcaoAtual = constructorIdentifier;
        callGraph.setNodes(constructorIdentifier);

        try {
            returnArgsType(ctx.formal_parameter_list().fixed_parameters());
        } catch (NullPointerException e) {
            args.setLength(0);
        }

        FileCreator.appendToXmlFile("\t\t\t<constr name=\"" + constructorIdentifier + "(" + this.args + ")" +
                "\" acess=\"" + this.modifiers + "\">\n");
    } /* Extrai o construtor*/

    @Override
    public void exitConstructor_declaration(CSharpParser.Constructor_declarationContext ctx) {
        commentsExtractor.associateComments(ctx);
        FileCreator.appendToXmlFile("\t\t\t</constr>\n");
    }

    @Override
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
    }/* Extrai os modificadores de atributos e metodos da classe */

    @Override
    public void enterProperty_declaration(CSharpParser.Property_declarationContext ctx) {
        String propertyIdentifier = this.tokens.getText(ctx.member_name().namespace_or_type_name().identifier(0));
        this.funcaoAtual = propertyIdentifier;
        callGraph.setNodes(propertyIdentifier);

        FileCreator.appendToXmlFile("\t\t\t<property name=\"" + propertyIdentifier + "\" acess=\"" + this.modifiers + "\">\n");
    }/* Extrai os getters e setters*/

    @Override
    public void enterGet_accessor_declaration(CSharpParser.Get_accessor_declarationContext ctx) {
        callGraph.setEdge(this.funcaoAtual, "get");
    }

    @Override
    public void enterSet_accessor_declaration(CSharpParser.Set_accessor_declarationContext ctx) {
        callGraph.setEdge(this.funcaoAtual, "set");
    }

    @Override
    public void enterAccessor_declarations(CSharpParser.Accessor_declarationsContext ctx) {
        try {
            callGraph.setEdge(this.funcaoAtual, this.tokens.getText(ctx.GET().getSourceInterval()));
        } catch (NullPointerException e) {
            System.out.print("");
        }

        try {
            callGraph.setEdge(this.funcaoAtual, this.tokens.getText(ctx.SET().getSourceInterval()));
        } catch (NullPointerException e) {
            System.out.print("");
        }
    }

    @Override
    public void exitProperty_declaration(CSharpParser.Property_declarationContext ctx) {
        commentsExtractor.associateComments(ctx);
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
    public void enterLocal_variable_declaration(CSharpParser.Local_variable_declarationContext ctx) {
        String variableType = this.tokens.getText(ctx.local_variable_type());
        String variableIdentifier = this.tokens.getText(ctx.local_variable_declarator(0).identifier());

        variableType = variableType.replace("<", "&lt;");
        variableType = variableType.replace(">", "&gt;");

        FileCreator.appendToXmlFile("\t\t\t<lvar name=\"" + variableIdentifier + "\" type=\"" + variableType + "\"></lvar>\n");
    } /* Extrai a variavel local da classe*/

    @Override
    public void enterPrimary_expression(CSharpParser.Primary_expressionContext ctx) {
        String metodo;

        for (int i = 0; i < ctx.method_invocation().size(); i++) {

            try {
                metodo = this.tokens.getText(ctx.member_access(i));
            } catch (NullPointerException k) {
                metodo = this.tokens.getText(ctx.primary_expression_start().getSourceInterval());
            }

            metodo = metodo.replace(".", "");
            metodo = metodo.replace("?", "");
            callGraph.setEdge(this.funcaoAtual, metodo);
        }
    }

    @Override
    public void enterObjectCreationExpression(CSharpParser.ObjectCreationExpressionContext ctx) {
        try {
            callGraph.setEdge(this.funcaoAtual, this.tokens.getText(ctx.type()));
        } catch (NullPointerException e) {
            System.out.println("");
        }
    }
}
