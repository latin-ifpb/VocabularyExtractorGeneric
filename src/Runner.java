import org.bluebird.Extractor.CSharp.CSharpWalker;

import java.io.File;

public class Runner {

    public static void main(String[] args) {
        CSharpWalker kaka = new CSharpWalker();
        try{
        kaka.walkFileTree(new File("/home/witoriamanuely/Projeto_POO/src/org/bluebird/Extractor/CSharp/Teste.cs"));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}