package org.bluebird.Extractor;

import org.antlr.v4.runtime.misc.MultiMap;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import org.bluebird.FileUtils.FileCreator;

import java.util.Set;

public class CallGraph {
    private static Set<String> nodes = new OrderedHashSet<>();
    private static MultiMap<String, String> edges = new MultiMap<>();

    public void setEdge(String source, String target){
        edges.map(source, target);
    }

    public void setNodes(String node) {
        nodes.add(node);
    }

    public static void toDOT(){
        FileCreator.appendToDotFile("digraph G{\n");

        for (String node:nodes) {
            FileCreator.appendToDotFile(node);
            FileCreator.appendToDotFile("; ");
        }

        FileCreator.appendToDotFile("\n");

        for (String src : edges.keySet()){
            for (String trg : edges.get(src)){
                FileCreator.appendToDotFile(" ");
                FileCreator.appendToDotFile("\t" + src);
                FileCreator.appendToDotFile("  ->  ");
                FileCreator.appendToDotFile(trg);
                FileCreator.appendToDotFile(";\n");
            }
        }

        FileCreator.appendToDotFile("}\n");
        nodes.clear();
        edges.clear();
    }
}
