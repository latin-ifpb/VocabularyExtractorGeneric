package org.bluebird.test;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CSharpListenerTest {
    private Map<String, String> mapVxl;

    public void lerArquivo(String entity, String path) {
        mapVxl  = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            while (br.ready()) {
                String linha = br.readLine();
                if(linha != null){
                    mapVxl.put(linha, linha);
                }
            }
            br.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Test
    public void test_constantes() {
        lerArquivo("comment","/home/pamela/Documentos/Projeto/test/csharp/const.txt");
        assertTrue(this.mapVxl.containsValue("<const name=\"constFloat\" acess=\"public\" type=\"float\"> </const>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"constInt\" acess=\"public\" type=\"int\"> </const>"));
    }

    @Test
    public void test_constructor() {
        lerArquivo("comment","/home/pamela/Documentos/Projeto/test/csharp/constr.txt");
        assertTrue(this.mapVxl.containsValue("constr name=\"Cliente(string)\" acess=\"public\" cloc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<arg name=\"nome\"/>"));
        assertTrue(this.mapVxl.containsValue("</constr>"));
    }

    @Test
    public void test_attribute() {
        lerArquivo("attribute","/home/pamela/Documentos/Projeto/test/csharp/atrbt.txt");
        assertTrue(this.mapVxl.containsValue("<atrbt name=\"Obsolete\"> </atrbt>"));
        assertTrue(this.mapVxl.containsValue("<atrbt name=\"MySpecial\"> </atrbt>"));
    }
    @Test
    public void test_propriety() {
        lerArquivo("proprierty","/home/pamela/Documentos/Projeto/test/csharp/prpty.txt");
        assertTrue(this.mapVxl.containsValue("<prpty name=\"Propriedade2\" access=\"private\" type=\"string\" mod=\"virtual\" loc=\"4\"> </prpty>"));
        assertTrue(this.mapVxl.containsValue("<prpty name=\"Propriedade3\" access=\"public\" type=\"string\" mod=\"static\" loc=\"1\"> </prpty>"));
    }
    @Test
    public void test_lvar() {
        lerArquivo("lvar", "/home/pamela/Documentos/Projeto/test/csharp/lvar.txt");
        assertTrue(this.mapVxl.containsValue("<lvar name=\"LocalVar\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"Unknown\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"ConnectionLost\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"OutlierReading\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"Spring\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"Summer\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"Autumn\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"Winter\"/>"));
    }

    @Test
    public void test_field_csharp() {
        lerArquivo("field","/home/pamela/Documentos/Projeto/test/csharp/field.txt");
        assertTrue(this.mapVxl.containsValue("<field name=\"varInt\" access=\"public\" type=\"int\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"varString\" access=\"public\" type=\"string\" mod=\"abstract\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"varDouble\" access=\"public\" type=\"double\" mod=\"virtual\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"propriedadeCor_\" access=\"private\" type=\"string\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"constFloat\" access=\"public\" type=\"float\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"constInt\" access=\"public\" type=\"int\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"genericList\" access=\"default\" type=\"string\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"dict\" access=\"default\" type=\"Conta\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"list\" access=\"default\" type=\"var\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"varInterna\" access=\"default\" type=\"int\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"varStringInterna\" access=\"default\" type=\"string\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"title\" access=\"public\" type=\"string\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"author\" access=\"public\" type=\"string\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"subject\" access=\"public\" type=\"string\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"book_id\" access=\"public\" type=\"int\" mod=\"default\" loc=\"1\"/>"));
    }
    @Test
    public void test_struct() {
        lerArquivo("struct","/home/pamela/Documentos/Projeto/test/csharp/struct.txt");
        assertTrue(this.mapVxl.containsValue("<struct name=\"structBooks\" access=\"public\" loc=\"\" mod=\"static9\">"));
        assertTrue(this.mapVxl.containsValue("<field name=\"title\" access=\"public\" type=\"string\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"author\" access=\"public\" type=\"string\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"subject\" access=\"public\" type=\"string\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"book_id\" access=\"public\" type=\"int\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// comentário 1 dentro da struct\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// comentário 2 dentro da struct\"/>"));
        assertTrue(this.mapVxl.containsValue("</struct>"));
    }

    @Test
    public void test_enum_csharp() {

        lerArquivo("enum","/home/pamela/Documentos/Projeto/test/csharp/enum.txt");
        assertTrue(this.mapVxl.containsValue("<enum name=\"GerenicEnum1\" access=\"private\" mod=\"default\" loc=\"7\">"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"LocalVar\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"Unknown\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"ConnectionLost\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"OutlierReading\"/>"));
        assertTrue(this.mapVxl.containsValue("</enum>"));
        assertTrue(this.mapVxl.containsValue("<enum name=\"GenericEnum2\" access=\"public\" mod=\"default\" loc=\"15\">"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"Spring\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"Summer\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"Autumn\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"Winter\"/>"));
        assertTrue(this.mapVxl.containsValue("</enum>"));
    }
    @Test
    public void test_comment_csharp() {
        lerArquivo("comment","/home/pamela/Documentos/Projeto/test/csharp/cmmt.txt");
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Variáveis\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Constantes\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Generics\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// Metodo\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Comentario de bloco dentro de metodo */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Propriedades\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Variável local\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//não faz nada\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// Propriedades\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Atributos\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Interface\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// código da interface\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// comentário 1 dentro da struct\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// comentário 2 dentro da struct\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// começo da classe Código Genérico\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Namespace\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Struct\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Enumerate\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// comentário dentro do enum 1\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// comentário dentro do enum 1\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* comentário de bloco dentro do enum 2 */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\" /* Código Generico em C# @author Kilvia Carvalho @author Pamela Lima */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\" /* Outro comentario de bloco do cabeçalho */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//comentario de linha de cabeçalho\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\" /* MAIS UM COMENTARIO DE BLOCO */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\" /* * COMENTARIO * COM * ASTERISTICO */\"/>"));
    }

    @Test
    public void test_methodo() {
        lerArquivo("methodo","/home/pamela/Documentos/Projeto/test/csharp/mth.txt");
        assertTrue(this.mapVxl.containsValue("<mth name=\"metodo()\" access=\"internal\" type=\"default\" mod=\"virtual\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Variáveis\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Constantes\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Generics\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// Metodo\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Comentario de bloco dentro de metodo */\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"Propriedade1(double)\" access=\"public\" type=\"bool\" mod=\"static\" loc=\"1\">"));
        assertTrue(this.mapVxl.containsValue("<arg name=\"valor\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Propriedades\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"Saca(double)\" access=\"public\" type=\"default\" mod=\"virtual\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<arg name=\"valor\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//não faz nada\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"metodoStatico(double)\" access=\"public\" type=\"int\" mod=\"static\" loc=\"4\">"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// Propriedades\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Atributos\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Interface\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// código da interface\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"func(double)\" access=\"public\" type=\"default\" mod=\"default\" loc=\"4\"> </mth>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"func(double)\" access=\"public\" type=\"default\" mod=\"default\" loc=\"4\"> </mth>"));

    }

    @Test
    public void test_namespace() {
        lerArquivo("namespace","/home/pamela/Documentos/Projeto/test/csharp/nmspc.txt");
        assertTrue(this.mapVxl.containsValue("<nmspc name=\"primeiroNamespace\" loc=\"10\">"));
        assertTrue(this.mapVxl.containsValue("<class name=\"interna1\" access=\"default\" mod=\"default\" loc=\"7\">"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"func(double)\" access=\"public\" type=\"default\" mod=\"default\" loc=\"4\"> </mth>"));
        assertTrue(this.mapVxl.containsValue("</class>"));
        assertTrue(this.mapVxl.containsValue("</nmspc>"));
        assertTrue(this.mapVxl.containsValue("<nmspc name=\"segundoNamespace\" loc=\"10\">"));
        assertTrue(this.mapVxl.containsValue("<class name=\"interna2\" access=\"default\" mod=\"default\" loc=\"7\">"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"func(double)\" access=\"public\" type=\"default\" mod=\"default\" loc=\"4\"> </mth>"));
        assertTrue(this.mapVxl.containsValue("</class>"));
        assertTrue(this.mapVxl.containsValue("</nmspc>"));
    }
    @Test
    public void test_classs() {
        lerArquivo("field","/home/pamela/Documentos/Projeto/test/csharp/class.txt");
        assertTrue(this.mapVxl.containsValue("<class name=\"CodigogGenerico\" access=\"public\" mod=\"abstract\" loc=\"70\">"));
        assertTrue(this.mapVxl.containsValue("<field name=\"varInt\" access=\"public\" type=\"int\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"varString\" access=\"public\" type=\"string\" mod=\"abstract\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"varDouble\" access=\"public\" type=\"double\" mod=\"virtual\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"propriedadeCor_\" access=\"private\" type=\"string\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"constFloat\" access=\"public\" type=\"float\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"constInt\" access=\"public\" type=\"int\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"genericList\" access=\"default\" type=\"string\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"dict\" access=\"default\" type=\"Conta\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"list\" access=\"default\" type=\"var\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"metodo()\" access=\"internal\" type=\"default\" mod=\"virtual\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Variáveis\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Constantes\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Generics\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// Metodo\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Comentario de bloco dentro de metodo */\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"Propriedade1(double)\" access=\"public\" type=\"bool\" mod=\"static\" loc=\"1\">"));
        assertTrue(this.mapVxl.containsValue("<arg name=\"valor\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Propriedades\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<prpty name=\"Propriedade2\" access=\"private\" type=\"string\" mod=\"virtual\" loc=\"4\"> </prpty>"));
        assertTrue(this.mapVxl.containsValue("<prpty name=\"Propriedade3\" access=\"public\" type=\"string\" mod=\"static\" loc=\"1\"> </prpty>"));
        assertTrue(this.mapVxl.containsValue("<constr name=\"Cliente(string)\" access=\"public\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<arg name=\"nome\"/>"));
        assertTrue(this.mapVxl.containsValue("</constr>"));
        assertTrue(this.mapVxl.containsValue("<atrbt name=\"Obsolete\"> </atrbt>"));
        assertTrue(this.mapVxl.containsValue("<class name=\"ThisClass\" access=\"public\" mod=\"default\" loc=\"6\">"));
        assertTrue(this.mapVxl.containsValue("<field name=\"varInterna\" access=\"default\" type=\"int\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Variável local\"/>"));
        assertTrue(this.mapVxl.containsValue("</class>"));
        assertTrue(this.mapVxl.containsValue("<class name=\"MySpecialAttribute\" access=\"public\" mod=\"default\" loc=\"1\"> </class>"));
        assertTrue(this.mapVxl.containsValue("<atrbt name=\"MySpecial\"> </atrbt>"));
        assertTrue(this.mapVxl.containsValue("<class name=\"SomeOtherClass\" access=\"public\" mod=\"default\" loc=\"1\"> </class>"));
        assertTrue(this.mapVxl.containsValue("<class name=\"Conta\" access=\"public\" mod=\"abstract\" loc=\"7\">"));
        assertTrue(this.mapVxl.containsValue("<field name=\"varStringInterna\" access=\"default\" type=\"string\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"Saca(double)\" access=\"public\" type=\"default\" mod=\"virtual\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<arg name=\"valor\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//não faz nada\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("</class>"));
        assertTrue(this.mapVxl.containsValue("<intfc name=\"Interface\" access=\"public\" mod=\"static\" loc=\"4\"> </intfc>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"metodoStatico(double)\" access=\"public\" type=\"int\" mod=\"static\" loc=\"4\">"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// Propriedades\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Atributos\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Interface\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// código da interface\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("</class>"));
    }
    @Test
    public void test_inner_class() {
        lerArquivo("inner_class","/home/pamela/Documentos/Projeto/test/csharp/inner_class.txt");
        assertTrue(this.mapVxl.containsValue("<class name=\"interna1\" access=\"default\" mod=\"default\" loc=\"7\">"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"func(double)\" access=\"public\" type=\"default\" mod=\"default\" loc=\"4\"> </mth>"));
        assertTrue(this.mapVxl.containsValue("</class>"));
        assertTrue(this.mapVxl.containsValue("<class name=\"interna2\" access=\"default\" mod=\"default\" loc=\"7\">"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"func(double)\" access=\"public\" type=\"default\" mod=\"default\" loc=\"4\"> </mth>"));
        assertTrue(this.mapVxl.containsValue("</class>"));
    }



    @Test
    public void test_file_csharp() {
        lerArquivo("file","/home/pamela/Documentos/Projeto/test/csharp/file.txt");
        assertTrue(this.mapVxl.containsValue("<project name=\"CodigoGenerico\" revision=\"CodigoGenericoRevision\">"));
        assertTrue(this.mapVxl.containsValue("<csharp-project>"));
        assertTrue(this.mapVxl.containsValue("</csharp-project>"));
        assertTrue(this.mapVxl.containsValue("</project>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\" /* Código Generico em C# @author Kilvia Carvalho @author Pamela Lima */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\" /* Outro comentario de bloco do cabeçalho */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//comentario de linha de cabeçalho\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\" /* MAIS UM COMENTARIO DE BLOCO */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\" /* * COMENTARIO * COM * ASTERISTICO */\"/>"));
    }

}
