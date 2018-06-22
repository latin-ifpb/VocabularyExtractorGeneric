package org.bluebird;

import org.bluebird.Extractor.Setup.ExtractorInit;
import org.bluebird.Extractor.Setup.ExtractorOptions;

public class Runner {

    /**
     * Classe principal do projeto
     * @param args Deve ter a lista de argumentos para o extrator
     */
    public static void main(String[] args) {
        ExtractorInit extractorInit = new ExtractorInit();
        String languageOption = "", projectName = "", projectRevision = "", projectPath = "", filePath = "";
        final String MANUAL = "You must set the following options:"
                + "\n\t-lang: language of the project to be extracted"
                + "\n\t-d: the project path must be inserted after this option"
                + "\n\t-n: sets the ProjectName"
                + "\n\t-r: sets the ProjectRevision"
                + "\n\t-f: path to save the resulting file(s)"
                + "\n\t-cg: generate call graph"
                + "\n\t-vxl: generate vxl"
                + "\n\t-txt: generate txt"
                + "\n\n\tEXAMPLE: -lang Java,C,CSharp -n Project_name -r Project_Revision  -d ~/SomeProject/ -f ~/Downloads" +
                "-cg -vxl -txt";

        try {
            for(int i = 0; i < args.length; i++) {
                if (args[i].equals("-lang")) {
                    languageOption = args[++i];
                } else if (args[i].equals("-d")) {
                    projectPath = args[++i];
                } else if (args[i].equals("-n")) {
                    projectName = args[++i];
                } else if (args[i].equals("-r")) {
                    projectRevision = args[++i];
                } else if (args[i].equals("-f")) {
                    filePath = args[++i];
                } else if (args[i].equals("-cg")) {
                    ExtractorOptions.setCallGraphEnabled(true);
                } else if (args[i].equals("-vxl")) {
                    ExtractorOptions.setVxlEnabled(true);
                } else if (args[i].equals("-txt")) {
                    ExtractorOptions.setVocabularyTxtEnabled(true);
                } else if (args[i].equals("-help")) {
                    System.out.println(MANUAL);
                    System.exit(0);
                }
            }

            extractorInit.extractVocabulary(languageOption.split(","), projectName, projectPath, filePath, projectRevision);
        } catch (IndexOutOfBoundsException e) {
            System.out.println(MANUAL);
        }
    }
}