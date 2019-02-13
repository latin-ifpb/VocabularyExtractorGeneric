package org.bluebird.Extractor.Setup;

public class ExtractorOptions {

    private static boolean callGraphEnabled;
    private static boolean vxlEnabled;
    private static boolean vocabularyTxtEnabled;
    private static boolean memoryRuntimeEnabled;
    private static boolean tokenExtractionEnabled;
    private static boolean caractereExtractionEnabled;

    /**
     * Verifica se a opção de gerar Call Graph foi marcada
     *
     * @return Boolean da opção do Call Graph
     */
    public static boolean isCallGraphEnabled() {
        return ExtractorOptions.callGraphEnabled;
    }

    /**
     * Seta a opção de gerar Call Graph
     *
     * @param callGraphEnabled True ou False para gerar o Call Graph
     */
    public static void setCallGraphEnabled(boolean callGraphEnabled) {
        ExtractorOptions.callGraphEnabled = callGraphEnabled;
    }

    /**
     * Verifica se a opção de gerar Vxl foi marcada
     *
     * @return Boolean da opção do Vxl
     */
    public static boolean isVxlEnabled() {
        return ExtractorOptions.vxlEnabled;
    }

    /**
     * Seta a opção de gerar Vxl
     *
     * @param vxlEnabled True ou False para gerar o Vxl
     */
    public static void setVxlEnabled(boolean vxlEnabled) {
        ExtractorOptions.vxlEnabled = vxlEnabled;
    }

    /**
     * Verifica se a opção de gerar o txt do vocabulario foi marcada
     *
     * @return Boolean da opção do txt do vocabulario
     */
    public static boolean isVocabularyTxtEnabled() {
        return ExtractorOptions.vocabularyTxtEnabled;
    }

    /**
     * Seta a opção de gerar txt do vocabulario
     *
     * @param vocabularyTxtEnabled True ou False para gerar o txt do vocabulario
     */
    public static void setVocabularyTxtEnabled(boolean vocabularyTxtEnabled) {
        ExtractorOptions.vocabularyTxtEnabled = vocabularyTxtEnabled;
    }

    /**
     * Verifica se a opção de gerar dados de runtime foi marcada
     *
     * @return Boolean da opção do memoryRuntime
     */
    public static boolean isMemoryRuntimeEnabled() {
        return ExtractorOptions.memoryRuntimeEnabled;
    }

    /**
     * Seta a opção de gerar dados de runtime
     *
     * @param memoryRuntimeEnabled True ou False para os dados de runtime
     */
    public static void setMemoryRuntimeEnabled(boolean memoryRuntimeEnabled) {
        ExtractorOptions.memoryRuntimeEnabled = memoryRuntimeEnabled;
    }

    /**
     * Verifica se a opção de extrair os tokens foi marcada
     *
     * @return Boolean da opção de extração dos tokens
     */
    public static boolean isTokenExtractionEnabled() {
        return ExtractorOptions.tokenExtractionEnabled;
    }

    /**
     * Seta a opção de extrair tokens do código
     *
     * @param tokenExtractionEnabled True ou False para a extração de tokens
     */
    public static void setTokenExtractionEnabled(boolean tokenExtractionEnabled) {
        ExtractorOptions.tokenExtractionEnabled = tokenExtractionEnabled;
    }

    /**
     * Verifica se a opção de extrair os caracteres foi marcada
     *
     * @return Boolean da opção de extração dos caracteres
     */
    public static boolean isCaractereExtractionEnabledd() {
        return ExtractorOptions.caractereExtractionEnabled;
    }

    /**
     * Seta a opção de extrair caracteres do código
     *
     * @param caractereExtractionEnabled True ou False para a extração de caracteres
     */
    public static void setCaractereExtractionEnabled(boolean caractereExtractionEnabled) {
        ExtractorOptions.caractereExtractionEnabled = caractereExtractionEnabled;
    }
}
