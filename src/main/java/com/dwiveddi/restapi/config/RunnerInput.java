package com.dwiveddi.restapi.config;

import com.dwiveddi.testscommon.utils.FileUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by dwiveddi on 4/8/2018.
 */
public class RunnerInput {
    private final String testFile;
    private String testVariableFile;
    private String suiteVariableFile;
    private Set<String> sheetsToIgnore;
    private Set<String> sheetsToInclude;
    private String outputDir;


    public RunnerInput(String testFile) {
        FileUtils.validateFileExists(testFile, true);
        if(!testFile.toLowerCase().endsWith(".xlsx")){
            throw new IllegalArgumentException("Input Test File is not a .xlsx file. testFile = " + testFile);
        }
        this.testFile = testFile;
    }

    public RunnerInput testVariableFile(String testVariableFile){
        FileUtils.validateFileExists(testVariableFile,false);
        this.testVariableFile = testVariableFile;
        return this;
    }

    public RunnerInput suiteVariableFile(String suiteVariableFile){
        FileUtils.validateFileExists(suiteVariableFile,false);
        this.suiteVariableFile = suiteVariableFile;
        return this;
    }

    public RunnerInput sheetsToIgnore(String... sheetsToIgnore){
        if(sheetsToIgnore.length != 0) {
            this.sheetsToIgnore = new HashSet<String>(Arrays.asList(sheetsToIgnore));
        }
        return this;
    }

    public RunnerInput sheetsToInclude(String... sheetsToInclude){
        if(sheetsToInclude.length != 0) {
            this.sheetsToInclude = new HashSet<String>(Arrays.asList(sheetsToInclude));
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

    public String getTestVariableFile() {
        return testVariableFile;
    }

    public String getSuiteVariableFile() {
        return suiteVariableFile;
    }

    public Set<String> getSheetsToIgnore() {
        return sheetsToIgnore;
    }

    public Set<String> getSheetsToInclude() {
        return sheetsToInclude;
    }

    public String getOutputDir() {
        return outputDir;
    }
}
