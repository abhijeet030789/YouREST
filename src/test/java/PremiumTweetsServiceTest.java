import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class PremiumTweetsServiceTest {

    private Api api;
    private static boolean isReuiredToPass = true;

    @Parameterized.Parameters
    public static Collection data() throws IOException {
        File file =  new File("D:/OpenSource/rest-api-test/src/test/resources/rest-api-contracts.json");
        Api[] apiArr
                = new ObjectMapper().readValue(file, Api[].class);

        return Arrays.asList(apiArr);
                /*{99, 9.90}, {100, 10.00}, {101, 10.08}, {200, 18},
                {499, 41.92}, {500, 42}, {501, 42.05}, {1000, 67},
                {10000, 517},});*/
    }

    public PremiumTweetsServiceTest(Api api) {
        super();
        this.api = api;
    }

    @Test
    public void shouldCalculateCorrectFee() {
        System.out.println(String.format("api = %s", api));
        isReuiredToPass = false;
    }
}
