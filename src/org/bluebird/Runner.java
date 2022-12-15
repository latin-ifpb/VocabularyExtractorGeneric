package org.bluebird;

import org.bluebird.Extractor.Setup.ExtractorInit;
import org.bluebird.Extractor.Setup.ExtractorOptions;

public class Runner {

    /**
     * Classe principal do projeto
     *
     * @param args Deve ter a lista de argumentos para o extrator
     */
    public static void main(String[] args) {
        ExtractorInit extractorInit = new ExtractorInit();
        String languageOption = "C", projectName = "codigoExtratorC2.0", projectRevision = "codigoExtratorCRevision2.0", projectPath = "/home/pamela/Documentos/Projeto/codigoGenerico/C", filePath = "/home/pamela/Documentos";

        // final String MANUAL = "You must set the following options:"
        //     + "\n\t-lang: language of the project to be extracted"
        //     + "\n\t-d: the project path must be inserted after this option"
        //     + "\n\t-n: sets the ProjectName"
        //     + "\n\t-r: sets the ProjectRevision"
        //     + "\n\t-f: path to save the resulting file(s)"
        //     + "\n\t-cg: generate call graph"
        //     + "\n\t-vxl: generate vxl"
        //     + "\n\t-txt: generate txt"
        //     + "\n\t-stat: show memory used"
        //     + "\n\n\tEXAMPLE: -lang Java,C,CSharp -n Project_name -r Project_Revision  -d ~/SomeProject/ -f ~/Downloads" +
        //     "-cg -vxl -txt";
        // try {
        // if (args.length == 0) {
        //     //System.out.println(MANUAL);
        //     System.exit(0);
        //}

        // for (int i = 0; i < args.length; i++) {
        //     switch (args[i]) {
        //         case "-lang":
        //             languageOption = args[++i];
        //             break;
        //         case "-d":
        //             projectPath = args[++i];
        //             break;
        //         case "-n":
        //             projectName = args[++i];
        //             break;
        //         case "-r":
        //             projectRevision = args[++i];
        //             break;
        //         case "-f":
        //             filePath = args[++i];
        //             break;
        //         case "-cg":
        //             ExtractorOptions.setCallGraphEnabled(true);
        //             break;
        //         case "-vxl":
        //             ExtractorOptions.setVxlEnabled(true);
        //             break;
        //         case "-txt":
        //             ExtractorOptions.setVocabularyTxtEnabled(true);
        //             break;
        //         case "-stat":
        //             ExtractorOptions.setMemoryRuntimeEnabled(true);
        //             break;
        //         case "-help":
        //         default:
        //             //System.out.println(MANUAL);
        //             System.exit(0);
        //             break;
        //     }
        //}

        // extractorInit.extractVocabulary(languageOption.split(","), projectName, projectPath, filePath, projectRevision);
        ExtractorOptions.setVxlEnabled(true);
        ExtractorOptions.setCallGraphEnabled(true);

        extractorInit.extractVocabulary(languageOption.split(","), projectName, projectPath, filePath, projectRevision);


        // } catch (IndexOutOfBoundsException e) {
        //  System.out.println(MANUAL);
        //}
    }

}