package com.dwiveddi.restapi;

import com.dwiveddi.restapi.config.RunnerInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import java.util.*;

/**
 * Created by dwiveddi on 4/6/2018.
 */
public class RestApiRunner {

    public static class Result{
        private boolean isPassed = true;
        private String resultFile = "";

        @Override
        public String toString() {
            return "Result{" +
                    "isPassed=" + isPassed +
                    ", resultFile='" + resultFile + '\'' +
                    '}';
        }

        public Result(boolean isPassed, String resultFile) {
            this.isPassed = isPassed;
            this.resultFile = resultFile;
        }
    }
    public static Result run(RunnerInput runnerInput){
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
        System.out.println(testNG.hasFailure());
        System.out.println(testNG.hasSkip());
        System.out.println(testNG.getStatus());
        System.out.println("Detailed Report can be found at: "+testNG.getOutputDirectory()+"/index.html");
        return new Result(0 == testNG.getStatus() && !RestApiExecutor.isDataProviderFailed, "file:///"+testNG.getOutputDirectory()+"/index.html");
    }

    public static void main(String[] args) throws Exception {
        //run(new RunnerInput("conf/Book1.xlsx"));
        //run(new RunnerInput("conf/Book1.xlsx").confFile("conf/book1Variables.json"));
        //run(new RunnerInput("conf/Book1.xlsx").outputDir("C:/REPORTS"));
        //run(new RunnerInput("conf/Book1.xlsx").sheetsToIgnore("QueryParam", "NestedArray", "PayloadPropagation", "Initial"));
        System.out.println(run(new RunnerInput("conf/Book1.xlsx").confFile("conf/book1Variables.json").outputDir("C:/REPORTS").sheetsToIgnore("ConfigFileInput","QueryParam", "NestedArray", "PayloadPropagation","Initial", "RandomString")));
        //System.out.println(((Map<String, Object>) GlobalVariables.INSTANCE.get("headers")).get("ContentType"));
        //System.out.println(((Map<String, Object>) GlobalVariables.INSTANCE.get("headers")));
        //System.out.println(((Map<String, Object>) GlobalVariables.INSTANCE.get("abc")));
       /* System.out.println(GlobalVariables.eval("${abc.hello}"));
        System.out.println(GlobalVariables.evalJson("${abc}"));
        System.out.println(GlobalVariables.eval("${headers.arrayY[0]}"));
        System.out.println(GlobalVariables.eval("${headers.ContentType}"));
        List<Integer> list = (List<Integer>)GlobalVariables.evalObject("${abc.z}");
        for(Integer integer : list){
            System.out.println(integer);
        }*/

        //System.out.println(GlobalVariables.eval("Vendorly-${randomString(10)}@gmail.com"));
        //run(new RunnerInput("conf/Book1.xlsx").confFile("conf/variables.json").outputDir("C:/REPORTS").sheetsToIgnore(new String[]{"QueryParam", "NestedArray", "OutputPropagation"}));
    }

}
