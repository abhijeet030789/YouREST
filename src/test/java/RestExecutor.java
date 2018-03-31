import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.dwiveddi.utils.excel.ExcelMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(Parameterized.class)
public class RestExecutor {

    private Map<String, Object> testCaseVariableMap = new HashMap<>();
    private static HttpClient httpClient = HttpClientUtils.getHttpClient();
    String keyPrefix = "";
    @BeforeClass
    public static void setup(){}
    private RequestResponseCombination combination;

    @Parameterized.Parameters
    public static List<RequestResponseCombination> dataFromExcel() throws IOException {
        ExcelMapper<RequestResponseCombination> mapper = new ExcelMapper<>(RequestResponseCombination.class);
        List<RequestResponseCombination> combinations = mapper.getList("C:/OpenSource/rest-api-test/conf/Book1.xlsx", 12, 2);
        return combinations;
    }
    //@Parameterized.Parameters
    public static List<RequestResponseCombination> data() throws IOException {
        File file =  new File("C:/OpenSource/rest-api-test/conf/rest-api-contracts.json");
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
    public RestExecutor(RequestResponseCombination combination) {
        super();
        this.combination = combination;
    }

    @Test
    public void testRestApi() throws IOException {
        HttpRequestBase httpRequestBase = HttpClientUtils.getHTTPBase(combination.getUrl(), combination.getMethod(), combination.getRequest().getPayload());
        HttpResponse response = HttpClientUtils.getHttpClient().execute(httpRequestBase);
        Map<String, String> responseHeaders = convert(response.getAllHeaders());
        int expectedStatusCode= combination.getResponse().getStatusCode();
        String actualPayload = IOUtils.toString(response.getEntity().getContent());
        List expectedJsonAttributes = combination.getResponse().getJsonAttributes();
        //Assertions
        if(0 != combination.getResponse().getStatusCode()){
            Assert.assertEquals(format("Status Code Should Match"), combination.getResponse().getStatusCode(), response.getStatusLine().getStatusCode());
        }
        if(null != combination.getResponse().getPayload() && !combination.getResponse().getPayload().isEmpty()) {
            Assert.assertEquals(format("Payload Should match"), combination.getResponse().getPayload(), actualPayload);
        }
        for(Map.Entry<String, String>  expectedHeader : combination.getResponse().getHeaders().entrySet()){
            //Assert.assertTrue("ActualResponseHeader should contain a key = "+expectedHeader.getKey(), responseHeaders.containsKey(expectedHeader.getKey())); //1. Checking presence oof key
            String expectedValue = expectedHeader.getValue();
            String actualValue = responseHeaders.get(expectedHeader.getKey());
            Assert.assertEquals(expectedValue, actualValue);  //2. Match values
        }
        digestPayload(combination.getResponse().isPayloadJsonValdationRequired(), actualPayload, combination.getResponse().getPayloadStructure()
                , expectedJsonAttributes, keyPrefix, combination.getVariableName());


    }
        //System.out.println(String.format("api = %s", api));
        //for (Header header : responseHeaders){System.out.println("Key : " + header.getName() + " Value : " + header.getValue());}
    private String format(String msg){
        return "TestCaseId = "+combination.getId()+"; "+msg;
    }

    private Map<String, String> convert(Header[] headers){
        Map<String, String> map = new HashMap<>();
        for (Header header : headers) {
            map.put(header.getName(), header.getValue());
        }
        return map;
    }

    private void digestPayload(boolean isValidationRequired, String content, PayloadStructure payloadStructure, List expectedJsonAttributes,String keyPrefix, String variableName){
        Object o = null;
        switch(payloadStructure){
            case STRING:
                o = content; break;
            case ARRAY_OF_STRING   :
                try {
                    o = new ObjectMapper().readValue(content, String[].class);
                }catch(IOException e){
                    Assert.assertTrue("The content is not of type String[] Exception  = "+e.getMessage(), false);
                }
                break;
            case ARRAY_OF_INTEGERS :
                try {
                    o = new ObjectMapper().readValue(content, Integer[].class);
                }catch(IOException e){
                    Assert.assertTrue("The content is not of type Integer[]. Exception = "+e.getMessage(), false);
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
                    Assert.assertTrue("The content is not of type Map[]. Exception = "+e.getMessage(), false);
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
                    Assert.assertTrue("The content is not of type Map. Exception = "+e.getMessage(), false);
                }
                break;
        }
        testCaseVariableMap.put(variableName, o);
    }



    private void validateJsonStructure(List expectedJsonAttributes, Map<String, Object> actualResponseJsonAsMap,String keyPrefix) throws IOException {
            for (Object expectedJsonAttribute : expectedJsonAttributes) {
                if (expectedJsonAttribute instanceof Map) {
                    Map<String, Object> keyValPair = (Map<String, Object>) expectedJsonAttribute;
                    for (Map.Entry<String, Object> entry : keyValPair.entrySet()) {
                        Assert.assertTrue(String.format("The actual response should contain a key with name =  %s%s ",keyPrefix, entry.getKey()), actualResponseJsonAsMap.containsKey(entry.getKey()));
                        if (entry.getValue() instanceof List) {
                            validateJsonStructure((List) entry.getValue(), (Map) actualResponseJsonAsMap.get(entry.getKey()),keyPrefix+entry.getKey()+"." );
                        } else {
                            Assert.assertEquals(String.format("The value of key = '%s%s' should match with expectedValue",keyPrefix,entry.getKey()), entry.getValue(), actualResponseJsonAsMap.get(entry.getKey()));
                        }
                    }
                } else if (expectedJsonAttribute instanceof String) {
                    Assert.assertTrue("The json response should contain key = " + expectedJsonAttribute, actualResponseJsonAsMap.containsKey(expectedJsonAttribute));

                } else {
                    throw new IllegalArgumentException(String.format("o of class = %s, is not handled", expectedJsonAttribute.getClass()));
                }
            }
        }
    }

