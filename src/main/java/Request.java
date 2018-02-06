import lombok.Data;

import java.util.Map;

/**
 * Created by dwiveddi on 2/6/2018.
 */
@Data
public class Request {
    private String queryParams;
    private String payload;
    private Map<String,String> headers;

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
