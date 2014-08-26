package org.gardella.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

public class IOUtils {

    
    /**
     * Reads the entire contents from the specified input.
     * @param input the inpur Reader
     * @return the String representation of the input.
     * @throws IOException
     */
    public static String readFully(Reader input) throws IOException {
        BufferedReader bufferedReader = input instanceof BufferedReader
                ? (BufferedReader) input
                : new BufferedReader(input);
        StringBuffer result = new StringBuffer();
        char[] buffer = new char[4 * 1024];
        int charsRead;
        while ((charsRead = bufferedReader.read(buffer)) != -1) {
            result.append(buffer, 0, charsRead);
        }
        return result.toString();
    }
}
