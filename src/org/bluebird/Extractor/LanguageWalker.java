package org.bluebird.Extractor;

import java.io.File;
import java.io.IOException;

public interface LanguageWalker {

    void walkFileTree(File file) throws IOException;

    String languageFormat();
}
