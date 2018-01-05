package org.bluebird.Extractor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

public class ReadFile {

        public static String readFile(File file, Charset encoding) throws IOException {
            byte[] encoded = Files.readAllBytes(file.toPath());
            return new String(encoded, encoding);
        }
}
