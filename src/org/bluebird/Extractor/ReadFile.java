package org.bluebird.Extractor;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadFile {

    private static int contLinhaBranco = 0;
    private static int contTotal = 0;
    private static int contSloc = 0;
    private static int contCabecalho = 0;
    private static ArrayList<String> listCabecalho = new ArrayList<>();
    private static String conc = "";

    public int getContLinhaBranco() {
        return contLinhaBranco;
    }

    public int getContTotal() {
        return contTotal;
    }

    public int getContSloc() {
        return contSloc = getContTotal() - getContLinhaBranco();
    }

    public static ArrayList<String> getListCabecalho() {
        return ReadFile.listCabecalho;
    }

    public static void lerArquivo(int intLine, String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            while (br.ready()) {
                String linha = br.readLine();
                if(linha.trim().length()==0){
                    contLinhaBranco = contLinhaBranco + 1;
                }
                if (contCabecalho < intLine) {
                    getCabecalhoCSharp(linha);
                    contCabecalho = contCabecalho + 1;
                }
                contTotal = contTotal + 1;
            }
            br.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void getCabecalhoCSharp(String stringLine) {
        if (stringLine.contains("//")) {
            listCabecalho.add(stringLine);
        }
        else if (stringLine.contains("/*")) {
            conc = conc + " " + stringLine;
        }

        else if (stringLine.contains("*/")) {
            conc = conc + " " + stringLine;
            listCabecalho.add(conc);
            conc = "";
        }
        else if (!stringLine.contains("//") && !stringLine.contains("/*") && !stringLine.contains("*/")) {
            conc = conc + " " + stringLine;
        }
    }
}

