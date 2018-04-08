package com.dwiveddi.restapi.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by dwiveddi on 2/19/2018.
 */
public class FileUtils {

    public static String readFileAsString(String fileName) throws IOException {
        FileReader fr = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fr);
        String line;
        StringBuilder sb = new StringBuilder();
        while(  (line = br.readLine()) != null){
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
