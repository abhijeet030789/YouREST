package com.dwiveddi.restapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public static boolean run(String testFile, String confFile, String outputDir, Set<String> sheetsToIgnore){
        System.setProperty("testFile", testFile);
        System.setProperty("confFile", confFile);
        try {
            System.setProperty("sheetsToIgnore",new ObjectMapper().writeValueAsString(sheetsToIgnore));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Exception while serializing sheetsToIgnore", e);
        }
        XmlSuite suite = new XmlSuite();
        XmlTest test = new XmlTest(suite);
        test.setClasses(Arrays.asList(new XmlClass("com.dwiveddi.restapi.RestApiExecutor")));
        TestNG testNG = new TestNG();
        testNG.setOutputDirectory(outputDir);
        testNG.setXmlSuites(Arrays.asList(suite));
        testNG.run();
        return 0 == testNG.getStatus();
    }

    public static void main(String[] args) throws Exception {
         run("conf/Book1.xlsx","conf/variables.json","C:/REPORTS",new HashSet<>());
        // new HashSet<String>(Arrays.asList("QueryParam","NestedArray","OutputPropagation"))
    }

}
