import dto.Api;
import dto.PayloadStructure;
import dto.RequestResponseCombination;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.codehaus.jackson.map.ObjectMapper;
import org.dwiveddi.utils.excel.ExcelMapper;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import templateengine.FreemarkerTemplateEngine;
import utils.HttpClientUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static utils.HttpClientUtils.convert;
import static utils.HttpClientUtils.getHTTPBase;
import static utils.HttpClientUtils.getHttpClient;

public class RestApiExecutor {
    private static HttpClient httpClient = getHttpClient();
    String keyPrefix = "";


    @DataProvider(name = "combinations")
    public Iterator<Object[]> dataFromConfigFiles()  {
        try {
            List<RequestResponseCombination> combinations = new ArrayList<>();
            String confDir = System.getProperty("confDir");
            List<String> listOfFilePaths = new ArrayList<>();
            populateFileNames(confDir, listOfFilePaths, new String[]{".xlsx", ".json"});
            ExcelMapper<RequestResponseCombination> excelMapper = new ExcelMapper<RequestResponseCombination>(RequestResponseCombination.class);
            for (String filePath : listOfFilePaths) {
                if (filePath.endsWith(".json")) {
                    combinations.addAll(data(filePath));
                } else if (filePath.endsWith(".xlsx")) {
                    combinations.addAll(excelMapper.getList(filePath, 13, 2));
                }
            }
            List<Object[]> list = new ArrayList<>();
            for (RequestResponseCombination combination : combinations) {
                list.add(new Object[]{combination});
            }
            return list.iterator();
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Error while ... + "+e.getMessage() + " - " + e.getCause(), e);
        }
    }

    private void populateFileNames(String filePath, List<String> fileNames, String[] allowedFileExtensions){
        File file = new File(filePath);
        if(!file.exists())
            throw new IllegalArgumentException("File Not Found. filePath = "+filePath);
        if(file.isDirectory()){
            for(File innerFile : file.listFiles()){
                populateFileNames(innerFile.getAbsolutePath(), fileNames, allowedFileExtensions);
            }
        }else{
            for(String allowedFileExtension : allowedFileExtensions){
                if(file.getAbsolutePath().endsWith(allowedFileExtension)){
                    fileNames.add(file.getAbsolutePath()); break;
                }
            }
        }
    }


    public List<RequestResponseCombination> data(String filePath) throws IOException {
        File file =  new File(filePath);
        Api[] apiArr = new ObjectMapper().readValue(file, Api[].class);
        List<RequestResponseCombination> listOfCombinations = new ArrayList<>();
        for(Api api : apiArr){
            for(RequestResponseCombination combination : api.getRequestResponseCombinations()){
                if(null == combination.getUrl()){
                    combination.setUrl(api.getDefaultUrl());
                }
                if(null == combination.getMethod()){
                    combination.setMethod(api.getDefaultMethod());
                }
                if(null == combination.getRequest()){
                    combination.setRequest(api.getDefaultRequest());
                }
                if(null == combination.getRequest().getHeaders()){
                    combination.getRequest().setHeaders(api.getDefaultRequest().getHeaders());
                }
                if(null == combination.getResponse()){
                    combination.setResponse(api.getDefaultResponse());
                }
                if(null == combination.getResponse().getHeaders()){
                    combination.getResponse().setHeaders(api.getDefaultResponse().getHeaders());
                }
                listOfCombinations.add(combination);
            }
        }
        return listOfCombinations;
    }


    @Test(dataProvider = "combinations")
    public void testRestApi(RequestResponseCombination combination) throws IOException {
        System.out.println("BEFORE : " + combination);
        combination.format(FreemarkerTemplateEngine.getInstance().getGlobalMap());
        System.out.println("AFTER  : " + combination);
        Map<String,String> requestHeaders = combination.getRequest().getHeaders();
        HttpRequestBase httpRequestBase = getHTTPBase(combination.getUrl(), combination.getMethod(), combination.getRequest().getHeaders(), combination.getRequest().getPayload());
        HttpResponse response = getHttpClient().execute(httpRequestBase);
        Map<String, String> responseHeaders = convert(response.getAllHeaders());
        int expectedStatusCode= combination.getResponse().getStatusCode();
        String actualPayload = IOUtils.toString(response.getEntity().getContent());
        List expectedJsonAttributes = combination.getResponse().getJsonAttributes();
        //Assertions
        if(0 != combination.getResponse().getStatusCode()){
            Assert.assertEquals(response.getStatusLine().getStatusCode(),combination.getResponse().getStatusCode(),format("Status Code Should Match", combination));
        }
        if(null != combination.getResponse().getPayload() && !combination.getResponse().getPayload().isEmpty()) {
            String expected = jsonOneLine(combination.getResponse().getPayload());
            String actual = jsonOneLine(actualPayload);
            Assert.assertEquals(actual,expected,format("Payload Should match", combination));
        }
        for(Map.Entry<String, String>  expectedHeader : combination.getResponse().getHeaders().entrySet()){
            //Assert.assertTrue("ActualResponseHeader should contain a key = "+expectedHeader.getKey(), responseHeaders.containsKey(expectedHeader.getKey())); //1. Checking presence oof key
            String expectedValue = expectedHeader.getValue();
            String actualValue = responseHeaders.get(expectedHeader.getKey());
            Assert.assertEquals( actualValue, expectedValue);  //2. Match values
        }
        digestPayload(combination.getResponse().isPayloadJsonValdationRequired(), actualPayload, combination.getResponse().getPayloadStructure()
                , expectedJsonAttributes, keyPrefix, combination.getVariableName());


    }

    private String format(String msg, RequestResponseCombination combination){
        return "TestCaseId = "+combination.getId()+"; "+msg;
    }



    private void digestPayload(boolean isValidationRequired, String content, PayloadStructure payloadStructure, List expectedJsonAttributes, String keyPrefix, String variableName){
        Object o = null;
        switch(payloadStructure){
            case STRING:
                o = content; break;
            case ARRAY_OF_STRING   :
                try {
                    o = new ObjectMapper().readValue(content, String[].class);
                }catch(IOException e){
                    Assert.assertTrue( false,"The content is not of type String[] Exception  = "+e.getMessage());
                }
                break;
            case ARRAY_OF_INTEGERS :
                try {
                    o = new ObjectMapper().readValue(content, Integer[].class);
                }catch(IOException e){
                    Assert.assertTrue(false,"The content is not of type Integer[]. Exception = "+e.getMessage());
                }
                break;
            case ARRAY_OF_JSON     :
                try {
                    Map<String, Object>[] mapArr = new ObjectMapper().readValue(content, Map[].class);
                    if(isValidationRequired) {
                        for (Map actualMap : mapArr) {
                            validateJsonStructure(expectedJsonAttributes, actualMap, keyPrefix);
                        }
                    }
                    o = mapArr;
                }catch(IOException e){
                    Assert.assertTrue(false,"The content is not of type Map[]. Exception = "+e.getMessage());
                }
                break;
            case JSON              :
                try {
                    Map<String, Object> actualMap = new ObjectMapper().readValue(content, Map.class);
                    if(isValidationRequired) {
                        validateJsonStructure(expectedJsonAttributes, actualMap, keyPrefix);
                    }
                    o = actualMap;
                }catch(IOException e){
                    Assert.assertTrue(false, "The content is not of type Map. Exception = "+e.getMessage());
                }
                break;
        }
        FreemarkerTemplateEngine.getInstance().putToGlobalMap(variableName, o);
    }

    private void validateJsonStructure(List expectedJsonAttributes, Object objectToValidate,String keyPrefix) throws IOException {
        PayloadStructure payloadStructure = null;
        List list = new ArrayList<>();
        if(objectToValidate instanceof  Map){
            list.add(objectToValidate);
            payloadStructure = PayloadStructure.JSON;
        }else if(objectToValidate instanceof List){
            list = (List)objectToValidate;
            if(list.size() == 0){
                return ;
            }else{
                if(list.get(0) instanceof Map){
                    payloadStructure = PayloadStructure.ARRAY_OF_JSON;
                }else{
                    payloadStructure = PayloadStructure.ARRAY;
                }
            }
        }
        if(payloadStructure.equals(PayloadStructure.ARRAY_OF_JSON) || payloadStructure.equals(PayloadStructure.JSON)){
            for(Object o : list){
                Map actualResponseJsonAsMap = (Map) o;
                for (Object expectedJsonAttribute : expectedJsonAttributes) {
                    if (expectedJsonAttribute instanceof Map) {
                        Map<String, Object> expectedKeyValPair = (Map<String, Object>) expectedJsonAttribute;
                        for (Map.Entry<String, Object> expectedEntry : expectedKeyValPair.entrySet()) {
                            Assert.assertTrue(actualResponseJsonAsMap.containsKey(expectedEntry.getKey()),String.format("The actual response should contain a key with name =  %s%s ", keyPrefix, expectedEntry.getKey()));
                            if (expectedEntry.getValue() instanceof List) {
                                validateJsonStructure((List) expectedEntry.getValue(), actualResponseJsonAsMap.get(expectedEntry.getKey()),keyPrefix+expectedEntry.getKey()+"." );
                            } else {
                                Assert.assertEquals(actualResponseJsonAsMap.get(expectedEntry.getKey()),expectedEntry.getValue(),
                                        String.format("The value of key = '%s%s' should match with expectedValue",keyPrefix,expectedEntry.getKey())
                                );
                            }
                        }
                    } else if (expectedJsonAttribute instanceof String) {
                        Assert.assertTrue(actualResponseJsonAsMap.containsKey(expectedJsonAttribute),"The json response should contain key = " + expectedJsonAttribute);
                    } else {
                        throw new IllegalArgumentException(String.format("o of class = %s, is not handled", expectedJsonAttribute.getClass()));
                    }
                }
            }
        }else if(payloadStructure.equals(PayloadStructure.ARRAY)){
            for (Object expectedJsonAttribute : expectedJsonAttributes) {
                Assert.assertTrue(list.contains(expectedJsonAttribute), String.format("The value of key = '%s', which is a List/Array should contain expectedValue = '%s'", keyPrefix, expectedJsonAttribute));
            }
        }
    }

    private String jsonOneLine(String s) {
        return s.trim().replaceAll(" ","").replaceAll("\\n","");
    }
}
