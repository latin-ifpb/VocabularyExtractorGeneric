package org.bluebird.Extractor.Setup;

public class ExtractorOptions {

    private static boolean callGraphEnabled;
    private static boolean vxlEnabled;
    private static boolean VocabularytxtEnabled;

    /**
     * Verifica se a opção de gerar Call Graph foi marcada
     * @return Boolean da opção do Call Graph
     */
    public static boolean isCallGraphEnabled() {
        return ExtractorOptions.callGraphEnabled;
    }

    /**
     * Seta a opção de gerar Call Graph
     * @param callGraphEnabled True ou False para gerar o Call Graph
     */
    public static void setCallGraphEnabled(boolean callGraphEnabled) {
        ExtractorOptions.callGraphEnabled = callGraphEnabled;
    }

    /**
     * Verifica se a opção de gerar Vxl foi marcada
     * @return Boolean da opção do Vxl
     */
    public static boolean isVxlEnabled() {
        return ExtractorOptions.vxlEnabled;
    }

    /**
     * Seta a opção de gerar Vxl
     * @param vxlEnabled True ou False para gerar o Vxl
     */
    public static void setVxlEnabled(boolean vxlEnabled) {
        ExtractorOptions.vxlEnabled = vxlEnabled;
    }

    /**
     * Verifica se a opção de gerar o txt do vocabulario foi marcada
     * @return Boolean da opção do txt do vocabulario
     */
    public static boolean isVocabularytxtEnabled() {
        return ExtractorOptions.VocabularytxtEnabled;
    }

    /**
     * Seta a opção de gerar txt do vocabulario
     * @param vocabularytxtEnabled True ou False para gerar o txt do vocabulario
     */
    public static void setVocabularytxtEnabled(boolean vocabularytxtEnabled) {
        ExtractorOptions.VocabularytxtEnabled = vocabularytxtEnabled;
    }
}
