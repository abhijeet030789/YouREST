package com.dwiveddi.restapi.utils;

import java.io.BufferedReader;
import java.io.File;
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

    public static void validateFileExists(String filepath, boolean isDirAccepted){
        File file = new File(filepath);
        if (!file.exists()){
            throw new IllegalArgumentException("File not found filePath = "+ filepath );
        }if(!isDirAccepted){
            if(file.isDirectory()){
                throw new IllegalArgumentException("File is a Directory = "+ filepath);
            }
        }
    }
}
