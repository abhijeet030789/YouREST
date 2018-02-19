import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
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

    private static HttpClient httpClient = HttpClientUtils.getHttpClient();

    @BeforeClass
    public static void setup(){}
    private RequestResponseCombination combination;

    @Parameterized.Parameters
    public static RequestResponseCombination[] data() throws IOException {
        File file =  new File("C:/OpenSource/junit-rest-api/conf/rest-api-contracts.json");
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
        RequestResponseCombination[] arr = new RequestResponseCombination[listOfCombinations.size()];
        return listOfCombinations.toArray(arr);
    }
    public RestExecutor(RequestResponseCombination combination) {
        super();
        this.combination = combination;
    }

    @Test
    public void shouldCalculateCorrectFee() throws IOException {
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
        if(null != combination.getResponse().getPayload()) {
            Assert.assertEquals(format("Payload Should match"), combination.getResponse().getPayload(), actualPayload);
        }
        for(Map.Entry<String, String>  expectedHeader : combination.getResponse().getHeaders().entrySet()){
            //Assert.assertTrue("ActualResponseHeader should contain a key = "+expectedHeader.getKey(), responseHeaders.containsKey(expectedHeader.getKey())); //1. Checking presence oof key
            String expectedValue = expectedHeader.getValue();
            String actualValue = responseHeaders.get(expectedHeader.getKey());
            //Assert.assertEquals(expectedValue, actualValue);  //2. Match values
        }
        if(combination.getResponse().isPayloadJsonValdationRequired()) {
            validate(actualPayload, combination.getResponse().getPayloadStructure(), expectedJsonAttributes);
        }
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

    private void validate(String content, PayloadStructure payloadStructure, List expectedJsonAttributes){
        switch(payloadStructure){
            case ARRAY_OF_STRING   :
                try {
                    new ObjectMapper().readValue(content, String[].class);
                }catch(IOException e){
                    Assert.assertTrue("The content is not of type String[] Exception  = "+e.getMessage(), false);
                }
                return;
            case ARRAY_OF_INTEGERS :
                try {
                    new ObjectMapper().readValue(content, Integer[].class);
                }catch(IOException e){
                    Assert.assertTrue("The content is not of type Integer[]. Exception = "+e.getMessage(), false);
                }
                return;
            case ARRAY_OF_JSON     :
                try {
                    Map<String, Object>[] mapArr = new ObjectMapper().readValue(content, Map[].class);
                    for (Map actualMap : mapArr) {
                        validateJsonStructure(expectedJsonAttributes, actualMap);
                    }
                }catch(IOException e){
                    Assert.assertTrue("The content is not of type Map[]. Exception = "+e.getMessage(), false);
                }
                return;
            case JSON              :
                try {
                    Map<String, Object> actualMap = new ObjectMapper().readValue(content, Map.class);
                    validateJsonStructure(expectedJsonAttributes, actualMap);
                }catch(IOException e){
                    Assert.assertTrue("The content is not of type Map. Exception = "+e.getMessage(), false);
                }
                return;
        }
    }


    private void validateJsonStructure(List expectedJsonAttributes, Map<String, Object> actualResponseJsonAsMap) throws IOException {
            for (Object expectedJsonAttribute : expectedJsonAttributes) {
                if (expectedJsonAttribute instanceof Map) {
                    Map<String, Object> keyValPair = (Map<String, Object>) expectedJsonAttribute;
                    for (Map.Entry<String, Object> entry : keyValPair.entrySet()) {
                        Assert.assertTrue("The actual response should contain a key with name =  " + entry.getKey(), actualResponseJsonAsMap.containsKey(entry.getKey()));
                        if (entry.getValue() instanceof List) {
                            validateJsonStructure((List) entry.getValue(), (Map) actualResponseJsonAsMap.get(entry.getKey()));
                        } else {
                            Assert.assertEquals(String.format("The value of key = '%s' should match with expectedValue", entry.getKey()), entry.getValue(), actualResponseJsonAsMap.get(entry.getKey()));
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

