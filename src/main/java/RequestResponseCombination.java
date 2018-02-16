import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dwiveddi on 2/6/2018.
 */
@Data
public class RequestResponseCombination {
    private String id;
    private String url;
    private String method;
    private Request request;
    private Response response;

    @Override
    public String toString() {
        return "RequestResponseCombination{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", request=" + request +
                ", response=" + response +
                '}';
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
