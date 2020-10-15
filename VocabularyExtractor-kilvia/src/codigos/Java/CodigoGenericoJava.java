package codigos.Java;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.*;
import java.lang.reflect.*;
import java.util.Random; // Single type declaration
import java.util.*;
import static java.lang.System.out; //'out' is a static field in java.lang.System


public class CodigoGenericoJava {

    /**
     * This is a documentation comment.
     *
     * @author
     */


    public enum ColorName {
        RED, BLUE, GREEN
    };

    public class LexerTest {
        void main(String[] args) {
            long creditCardNumber = 1234_5678_9012_3456L;
            long socialSecurityNumber = 999_99_9999L;
            float pi = 3.14_15F;
            long hexBytes = 0xFF_EC_DE_5E;
            long hexWords = 0xCAFE_BABE;
            long maxLong = 0x7fff_ffff_ffff_ffffL;
            byte nybbles = 0b0010_0101;
            long bytes = 0b11010010_01101001_10010100_10010010;
            long lastReceivedMessageId = 0L;
            double hexDouble1 = 0x1.0p0;
            double hexDouble2 = 0x1.956ad0aae33a4p117;
            int octal = 01234567;
            long hexUpper = 0x1234567890ABCDEFL;
            long hexLower = 0x1234567890abcedfl;

            int x2 = 5_2;              // OK (decimal literal)
            int x4 = 5_______2;        // OK (decimal literal)

            int x7 = 0x5_2;            // OK (hexadecimal literal)

            int x9 = 0_52;             // OK (octal literal)
            int x10 = 05_2;            // OK (octal literal)

            int x = 0, y = 0;
            int result;

            // Operadores aritméticos
            result = x + y;
            result = x - y;
            result = x * y;
            result = y / x;
            result = x % 3;

        }
    }

    public class HelloWorld {
        public void main (String[] args) {
            /* A seguinte linha é equivalente a:
            System.out.println("Hello World!");
            e estaria incorreto sem a declaração de importação. */


            int a = 1;
            int b = 2;
            int minVal = (a < b) ? a : b;

            // Tipos de referência -------------------
            // Arrays
            int[] numbers = new int[5];
            numbers[0] = 2;
            int x = numbers[0];

            // Inicializadores -------------------
            // Sintaxe Long
            int[] numbers3 = new int[] {20, 1, 42, 15, 34};
            // Sintaxe short syntax
            int[] numbers2 = {20, 1, 42, 15, 34};

            // Matrizes
            int[][] numbers4 = new int[3][3];
            numbers4[1][2] = 2;

            int[][] numbers5 = new int[2][]; // Inicialização apenas da lista
            numbers5[0] = new int[3];
            numbers5[1] = new int[2];

        }
    }


    // Classes -------------------
    // Classe de nível superior
    class Foo {
        //Membros da classe
    }

    // Classe interna
    class Foo1 { // Classe de nível superior
        class Bar { // Classe interna
        }

         void inner_class_constructor() {
            Foo1 foo = new Foo1();
            Foo1.Bar fooBar1 = foo.new Bar();
            Foo1.Bar fooBar2 = new Foo1().new Bar();
        }
    }

    // Classe anônima
    class Foo2 {
        void bar() {

            new Object() {// Criação de uma nova classe anônima estendendo Object
            };
        }
    }

    // Modificadores de acesso
    public class Foo3 {
        int go() {
            return 0;
        }

        private class Bar {
        }
    }

    // Construtores e inicializadores
    class Foo4 {
        String str;

        Foo4() { // Construtor sem argumentos
            // Initialization
        }

        Foo4(String str) { // Construtor com um argumento
            this.str = str;
        }
    }

    // Métodos -------------------
    class Foo5 {
        public Foo5() {
        }
        public Foo5(int a, int b) {
        }
        int bar(int a, int b) {
            return (a*2) + b;
        }

        /* Método sobrepostos com o mesmo nome, mas com diferentes conjuntos de argumentos */
        int bar(int a) {
            return a*2;
        }
    }

    // Enumerations -------------------
    enum Season {
        WINTER, SPRING, SUMMER, AUTUMN
    }

    public enum Season2 {
        WINTER("Cold"), SPRING("Warmer"), SUMMER("Hot"), AUTUMN("Cooler");

        Season2(String description) {
            // comentário dentro do construtor do enum
            this.description = description;
        }

        private final String description;

        /* esse é um comentário
           de bloco dentro do enum. */

        public String getDescription() {
            return description;
        }

        // esse é um comentário final no enum
    }
}


