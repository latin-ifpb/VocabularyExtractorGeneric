// Este comentário não faz parte da classe Generica
package com.xml2xsd;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.util.LinkedList;

/**
 * Javadoc da classe Generica
 * @author katyusco
 */
public class GenericClass {

    /* Constantes */

    public static final int PUBLIC_CONSTANTE = 1;
    private static final int PRIVATE_CONSTANTE = 2;

    /* Atributos */
    public float public_atributo; /* Comentário de public_atributo */
    private float private_atributo; /* Comentário de public_atributo */


    /* Com Generics */
    List<String> listaDeStrings = new ArrayList<String>();

    public enum GenericEnum {

        // Olha ae um comentario de linha dentro do enum
        RED, GREEN, BLUE;

        /* Um comentario de bloco dentro do enum */
    }

    /* Métodos */

    /**
     * Javadoc do método public_metodo
     * @param parametro1
     * @param parametro2
     * @return
     */
    public float public_metodo(int parametro1, int parametro2) {
        /* Variavel local */
        // uma expressao generica
        int local_variavel = (parametro1 + parametro2)*(parametro2);
        return local_variavel;
    }

    private float private_metodo(String parametro1, boolean parametro2) {
        return 0;
    }


    public class Interna {
        public float public_atributo_interna; /* Comentário de public_atributo_interna */
        private float private_atributo__interna; /* Comentário de public_atributo_interna */

        public Interna() {
            System.out.println("nada");
        }

        private void coisa() {
            System.out.println("Generic Class");
        }

    }
    
    /* Metodo abstrato */
    public abstract class Funcionario {
        public abstract double getBonificacao();
    }


    private class Interna2 extends Interna {}
    final class Interna3 extends Interna {}
}

enum SideEnum {

    // A agua viva ainda esta na fonte?
    ESSA, ENUMERACAO, NUM, FAZ, NADA;

    public void foo(String str){
        //Comentario dentro da classe do Enum
    }
}


enum SideEnum2 {

    NUM("num"),
    FAZ("faz"),
    NADA("nada");
    /*
    Comentario de bloco dentro do enum
    */

    private String descricao;

    Turno(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
