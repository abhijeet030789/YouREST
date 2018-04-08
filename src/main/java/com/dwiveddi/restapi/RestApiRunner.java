package com.dwiveddi.restapi;

import com.dwiveddi.restapi.config.RunnerInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.Runner;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by dwiveddi on 4/6/2018.
 */
public class RestApiRunner {
    public static boolean run(RunnerInput runnerInput){
        System.setProperty("testFile", runnerInput.getTestFile());
        if(runnerInput.getConfFile() != null) {
            System.setProperty("confFile", runnerInput.getConfFile());
        }
        if(null != runnerInput.getSheetsToIgnore()) {
            try {
                System.setProperty("sheetsToIgnore", new ObjectMapper().writeValueAsString(runnerInput.getSheetsToIgnore()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Exception while serializing sheetsToIgnore", e);
            }
        }
        XmlSuite suite = new XmlSuite();
        XmlTest test = new XmlTest(suite);
        test.setClasses(Arrays.asList(new XmlClass("com.dwiveddi.restapi.RestApiExecutor")));
        TestNG testNG = new TestNG();
        if(null != runnerInput.getOutputDir()) {
            testNG.setOutputDirectory(runnerInput.getOutputDir());
        }
        testNG.setXmlSuites(Arrays.asList(suite));
        testNG.run();
        return 0 == testNG.getStatus();
    }

    public static void main(String[] args) throws Exception {
        //run(new RunnerInput("conf/Book1.xlsx"));
        //run(new RunnerInput("conf/Book1.xlsx").confFile("conf/variables.json"));
        //run(new RunnerInput("conf/Book1.xlsx").outputDir("C:/REPORTS"));
        //run(new RunnerInput("conf/Book1.xlsx").sheetsToIgnore("QueryParam", "NestedArray", "OutputPropagation"));
        run(new RunnerInput("conf").confFile("conf/variables.json").sheetsToIgnore("QueryParam", "NestedArray", "OutputPropagation", "Backup"));

        //run(new RunnerInput("conf/Book1.xlsx").confFile("conf/variables.json").outputDir("C:/REPORTS").
        //        sheetsToIgnore(new String[]{"QueryParam", "NestedArray", "OutputPropagation"}));
    }

}
