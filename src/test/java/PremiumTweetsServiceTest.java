import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClients;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


@RunWith(Parameterized.class)
public class PremiumTweetsServiceTest {

    private static HttpClient httpClient = HttpClientUtils.getHttpClient();

    @BeforeClass
    public static void setup(){

    }

    private Api api;

    @Parameterized.Parameters
    public static Api[] data() throws IOException {
        File file =  new File("D:/OpenSource/rest-api-test/src/test/resources/rest-api-contracts.json");
        return new ObjectMapper().readValue(file, Api[].class);
    }

    public PremiumTweetsServiceTest(Api api) {
        super();
        this.api = api;
    }

    @Test
    public void shouldCalculateCorrectFee() {
        HttpRequestBase httpRequestBase = HttpClientUtils.getHttpClient(api.getUrl(), api.getMethod(), api.getRequestResponseCombinations().get(0).getRequest().getPayload());
        System.out.println(String.format("api = %s", api));
    }


}
