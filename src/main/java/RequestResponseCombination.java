import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dwiveddi on 2/6/2018.
 */
@Data
public class RequestResponseCombination {
    private Request request;
    private Response response;

    @Override
    public String toString() {
        return "RequestResponseCombination{" +
                "request=" + request +
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
}
