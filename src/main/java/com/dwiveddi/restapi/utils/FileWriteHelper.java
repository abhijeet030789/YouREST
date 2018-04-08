package com.dwiveddi.restapi.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by dwiveddi on 12/29/2016.
 */
public class FileWriteHelper<T> {

    public void writeAFile(List<T> list, String absoluteFilePath , String headerLine){
        String parentFolderPathStr = new File(absoluteFilePath).getParent();
        new File(parentFolderPathStr).mkdirs();
        Path filePath = Paths.get(absoluteFilePath);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, Charset.forName("utf-8"))) {//try-with-resource, BufferedWriter is autoclosed after try block ends
            String line = headerLine;
            if(null != line){//For writing the headerline
                writer.write(line, 0, line.length());
                writer.newLine();
            }
            for(T t : list){//for writing the result from the input list
                line = t.toString();
                writer.write(line, 0, line.length());
                writer.newLine();
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception occured while writing a file", e);
        }
    }
}
