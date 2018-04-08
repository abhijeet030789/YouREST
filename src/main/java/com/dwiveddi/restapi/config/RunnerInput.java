package com.dwiveddi.restapi.config;

import com.dwiveddi.restapi.utils.FileUtils;
import org.junit.runner.Runner;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dwiveddi on 4/8/2018.
 */
public class RunnerInput {
    private final String testFile;
    private String confFile;
    private Set<String> sheetsToIgnore;
    private String outputDir;


    public RunnerInput(String testFile) {
        FileUtils.validateFileExists(testFile,true);
        this.testFile = testFile;
    }

    public RunnerInput confFile(String confFile){
        FileUtils.validateFileExists(confFile,false);
        this.confFile = confFile;
        return this;
    }

    public RunnerInput sheetsToIgnore(String... sheetsToIgnore){
        if(sheetsToIgnore.length != 0) {
            this.sheetsToIgnore = new HashSet<String>(Arrays.asList(sheetsToIgnore));
        }
        return this;
    }

    public RunnerInput outputDir(String outputDir){
        this.outputDir = outputDir;
        return this;
    }


    public String getTestFile() {
        return testFile;
    }

    public String getConfFile() {
        return confFile;
    }

    public Set<String> getSheetsToIgnore() {
        return sheetsToIgnore;
    }

    public String getOutputDir() {
        return outputDir;
    }
}
