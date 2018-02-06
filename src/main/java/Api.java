import lombok.Data;

import java.util.List;

/**
 * Created by dwiveddi on 2/6/2018.
 */
@Data
public class Api {

    private String url;
    private String method;
    private List<RequestResponseCombination> requestResponseCombinations;

    @Override
    public String toString() {
        return "Api{" +
                "url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", requestResponseCombinations=" + requestResponseCombinations +
                '}';
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

    public List<RequestResponseCombination> getRequestResponseCombinations() {
        return requestResponseCombinations;
    }

    public void setRequestResponseCombinations(List<RequestResponseCombination> requestResponseCombinations) {
        this.requestResponseCombinations = requestResponseCombinations;
    }
}
