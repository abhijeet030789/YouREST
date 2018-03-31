import lombok.Data;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.dwiveddi.utils.csv.annotation.CsvMapped;

import java.io.IOException;
import java.util.Map;

/**
 * Created by dwiveddi on 2/6/2018.
 */
@Data
public class Request {
    @CsvMapped.Column(index = 3) private String queryParams;
    @CsvMapped.Column(index = 4, converterMethod = "convertTopMap") private Map<String,String> headers;
    @CsvMapped.Column(index = 5) private String payload;

    public Map<String, String> convertTopMap(String headers) throws IOException {
        return new ObjectMapper().readValue(headers, Map.class);
    }
    public Request(String queryParams, String payload, Map<String, String> headers) {
        this.queryParams = queryParams;
        this.payload = payload;
        this.headers = headers;
    }

    public Request() {
    }


    @Override
    public String toString() {
        return "Request{" +
                "queryParams='" + queryParams + '\'' +
                ", payload='" + payload + '\'' +
                ", headers=" + headers +
                '}';
    }

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
