import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
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

        //Assertions
        if(0 != combination.getResponse().getStatusCode()){
            Assert.assertEquals(format("Status Code Should Match"), combination.getResponse().getStatusCode(), response.getStatusLine().getStatusCode());
        }
        String actualPayload = IOUtils.toString(response.getEntity().getContent());
        if(null != combination.getResponse().getPayload()) {
            Assert.assertEquals(format("Payload Should match"), combination.getResponse().getPayload(), actualPayload);
        }
        for(Map.Entry<String, String>  expectedHeader : combination.getResponse().getHeaders().entrySet()){
            //Assert.assertTrue("ActualResponseHeader should contain a key = "+expectedHeader.getKey(), responseHeaders.containsKey(expectedHeader.getKey())); //1. Checking presence oof key
            String expectedValue = expectedHeader.getValue();
            String actualValue = responseHeaders.get(expectedHeader.getKey());
            //Assert.assertEquals(expectedValue, actualValue);  //2. Match values
        }

        if(combination.getResponse().isPayloadJsonValdationRequired()){
            Map<String, Object> jsonAsMap = new ObjectMapper().readValue(actualPayload, Map.class);
            for(Object o : combination.getResponse().getJsonAttributes()){
                if(o instanceof Map){
                    //value bhi compare karna hai
                }else if(o instanceof List){
                    //sub doc
                }else if (o instanceof String){
                    Assert.assertTrue("The json response should contain key = "+o, jsonAsMap.containsKey(o));
                }else{
                    throw new IllegalArgumentException(String.format("o of class = %s, is not handled", o.getClass()));
                }

            }
        }
        //System.out.println(String.format("api = %s", api));
        //for (Header header : responseHeaders){System.out.println("Key : " + header.getName() + " Value : " + header.getValue());}

    }


    private void validateJsonStructure(List<Object> expectedJsonAttributes,InputStream content, String keyPrefix){


    }
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

}
