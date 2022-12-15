package org.bluebird.test;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JavaListenerTest {
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
    public void test_lvar() {
        lerArquivo("lvar","/home/pamela/Documentos/Projeto/test/java/lvar.txt");
        assertTrue(this.mapVxl.containsValue("<lvar name=\"local_variavel \" type=\"int\"/>"));
    }

    @Test
    public void test_comments() {
        lerArquivo("comments","/home/pamela/Documentos/Projeto/test/java/cmmt.txt");
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Um comentario de bloco dentro do enum */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// Olha ae um comentario de linha dentro do enum\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Variavel local */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// uma expressao generica\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Comentário de public_atributo_interna */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Comentário de public_atributo_interna */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Constantes */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Métodos */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Atributos */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Comentário de public_atributo */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Comentário de public_atributo */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Metodo abstrato */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Comentario dentro da classe do Enum\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// A agua viva ainda esta na fonte?\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Com Generics */\"/>"));
    }
    @Test
    public void test_field() {
        lerArquivo("field","/home/pamela/Documentos/Projeto/test/java/field.txt");
        assertTrue(this.mapVxl.containsValue("<field name=\"public_atributo\" type=\"float\" acess=\"public\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"private_atributo\" type=\"float\" acess=\"private\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"listaDeStrings\" type=\"List<String>\" acess=\"default\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"public_atributo_interna\" type=\"float\" acess=\"public\" mod=\"final\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"private_atributo__interna\" type=\"float\" acess=\"private\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"stringEnum\" type=\"String\" acess=\"private\" mod=\"default\" loc=\"1\"/>"));

    }
    @Test
    public void test_construtor() {
        lerArquivo("construtor","/home/pamela/Documentos/Projeto/test/java/constr.txt");
        assertTrue(this.mapVxl.containsValue("<constr name=\"Interna()\" acess=\"public\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</constr>"));
        assertTrue(this.mapVxl.containsValue("<constr name=\"GenericEnum2(String parametroEnum)\" acess=\"default\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</constr>"));

    }
    @Test
    public void test_constante() {
        lerArquivo("constante","/home/pamela/Documentos/Projeto/test/java/const.txt");
        assertTrue(this.mapVxl.containsValue("<const name=\"PUBLIC_CONSTANTE\" type=\"int\" acess=\"public\" mod=\"final\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"PRIVATE_CONSTANTE\" type=\"int\" acess=\"private\" mod=\"final\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"RED\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"GREEN\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"BLUE\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"ESSA\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"ESSA\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"ENUMERACAO\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"NUM\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"FAZ\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"NADA\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"MANHA\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"TARDE\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"NOITE\"/>"));
    }

    @Test
    public void test_methodo() {
        lerArquivo("methodo","/home/pamela/Documentos/Projeto/test/java/mth.txt");
        assertTrue(this.mapVxl.containsValue("<mth name=\"public_metodo(int parametro1, int parametro2)\" type=\"float\" access=\"public\" mod=\"final\" loc=\"6\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"/** * Javadoc do método public_metodo * @param parametro1 * @param parametro2 * @return */\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"/** * Javadoc do método public_metodo * @param parametro1 * @param parametro2 * @return */\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"local_variavel \" type=\"int\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Variavel local */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// uma expressao generica\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"private_metodo(String parametro1, boolean parametro2)\" type=\"float\" access=\"private\" mod=\"default\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"coisa()\" type=\"void\" access=\"private\" mod=\"default\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"metodoAbstrato()\" type=\"double\" access=\"public\" mod=\"abstract\" loc=\"1\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"foo(String str)\" type=\"void\" access=\"public\" mod=\"default\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"metodoEnum()\" type=\"String\" access=\"public\" mod=\"default\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
    }
    @Test
    public void test_enum() {
        lerArquivo("lvar","/home/pamela/Documentos/Projeto/test/java/enum.txt");
        assertTrue(this.mapVxl.containsValue("<enum name=\"SideEnum\" acess=\"default\" mod=\"default\" loc=\"9\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"ESSA\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"ENUMERACAO\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"NUM\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"FAZ\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"NADA\"/>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"foo(String str)\" type=\"void\" access=\"public\" mod=\"default\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"//Comentario dentro da classe do Enum\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// A agua viva ainda esta na fonte?\"/>"));
        assertTrue(this.mapVxl.containsValue("<enum name=\"GenericEnum2\" acess=\"default\" mod=\"default\" loc=\"16\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"MANHA\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"TARDE\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"NOITE\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"stringEnum\" type=\"String\" acess=\"private\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<constr name=\"GenericEnum2(String parametroEnum)\" acess=\"default\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</constr>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"metodoEnum()\" type=\"String\" access=\"public\" mod=\"default\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("</enum>"));
    }
    @Test
    public void test_javadoc() {
        lerArquivo("javadoc","/home/pamela/Documentos/Projeto/test/java/javadoc.txt");
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"/** * Javadoc da classe Generica * @author katyusco */\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"/** * Javadoc do método public_metodo * @param parametro1 * @param parametro2 * @return */\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"/** * Javadoc do método public_metodo * @param parametro1 * @param parametro2 * @return */\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
    }

    @Test
    public void test_Clas() {
        lerArquivo("classe","/home/pamela/Documentos/Projeto/test/java/class.txt");
        assertTrue(this.mapVxl.containsValue("<class name=\"com/xml2xsd/GenericClass.java\" intfc=\"n\" access=\"public\" mod=\"final\" inn=\"n\" loc=\"73\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"/** * Javadoc da classe Generica * @author katyusco */\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"PUBLIC_CONSTANTE\" type=\"int\" acess=\"public\" mod=\"final\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"PRIVATE_CONSTANTE\" type=\"int\" acess=\"private\" mod=\"final\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"public_atributo\" type=\"float\" acess=\"public\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"private_atributo\" type=\"float\" acess=\"private\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"listaDeStrings\" type=\"List<String>\" acess=\"default\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<enum name=\"GenericEnum\" acess=\"public\" mod=\"abstract\" loc=\"7\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"RED\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"GREEN\"/>"));
        assertTrue(this.mapVxl.containsValue("<const name=\"BLUE\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Um comentario de bloco dentro do enum */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// Olha ae um comentario de linha dentro do enum\"/>"));
        assertTrue(this.mapVxl.containsValue("</enum>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"public_metodo(int parametro1, int parametro2)\" type=\"float\" access=\"public\" mod=\"final\" loc=\"6\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"/** * Javadoc do método public_metodo * @param parametro1 * @param parametro2 * @return */\"/>"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"/** * Javadoc do método public_metodo * @param parametro1 * @param parametro2 * @return */\"/>"));
        assertTrue(this.mapVxl.containsValue("<lvar name=\"local_variavel \" type=\"int\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Variavel local */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"// uma expressao generica\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"private_metodo(String parametro1, boolean parametro2)\" type=\"float\" access=\"private\" mod=\"default\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<class name=\"com/xml2xsd/Interna.java\" intfc=\"n\" access=\"public\" mod=\"default\" inn=\"y\" loc=\"13\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"public_atributo_interna\" type=\"float\" acess=\"public\" mod=\"final\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<field name=\"private_atributo__interna\" type=\"float\" acess=\"private\" mod=\"default\" loc=\"1\"/>"));
        assertTrue(this.mapVxl.containsValue("<constr name=\"Interna()\" acess=\"public\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</constr>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"coisa()\" type=\"void\" access=\"private\" mod=\"default\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Comentário de public_atributo_interna */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Comentário de public_atributo_interna */\"/>"));
        assertTrue(this.mapVxl.containsValue("</class>"));
        assertTrue(this.mapVxl.containsValue("<class name=\"com/xml2xsd/ClasseAbstrata.java\" intfc=\"n\" access=\"public\" mod=\"abstract\" inn=\"y\" loc=\"3\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("<mth name=\"metodoAbstrato()\" type=\"double\" access=\"public\" mod=\"abstract\" loc=\"1\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</mth>"));
        assertTrue(this.mapVxl.containsValue("</class>"));
        assertTrue(this.mapVxl.containsValue("<class name=\"com/xml2xsd/Interna2.java\" intfc=\"n\" access=\"private\" mod=\"default\" inn=\"y\" loc=\"1\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</class>"));
        assertTrue(this.mapVxl.containsValue("<class name=\"com/xml2xsd/Interna3.java\" intfc=\"n\" access=\"default\" mod=\"final\" inn=\"y\" loc=\"1\">"));
        assertTrue(this.mapVxl.containsValue("<javaDoc descr=\"\"/>"));
        assertTrue(this.mapVxl.containsValue("</class>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Constantes */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Métodos */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Atributos */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Comentário de public_atributo */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Comentário de public_atributo */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Com Generics */\"/>"));
        assertTrue(this.mapVxl.containsValue("<cmmt descr=\"/* Metodo abstrato */\"/>"));
        assertTrue(this.mapVxl.containsValue("</class>"));
    }
    @Test
    public void test_pack () {
        lerArquivo("package","/home/pamela/Documentos/Projeto/test/java/pack.txt");
        assertTrue(this.mapVxl.containsValue("<pack name=\"com/xml2xsd\">"));
        assertTrue(this.mapVxl.containsValue("</pack>"));
    }
    @Test
    public void test_file () {
        lerArquivo("file","/home/pamela/Documentos/Projeto/test/java/file.txt");
        assertTrue(this.mapVxl.containsValue("<project name=\"CodigoGenerico\" revision=\"CodigoGenericoRevision\">"));
        assertTrue(this.mapVxl.containsValue("<java-project>"));
        assertTrue(this.mapVxl.containsValue("</java-project>"));
        assertTrue(this.mapVxl.containsValue("</project>"));
    }






}