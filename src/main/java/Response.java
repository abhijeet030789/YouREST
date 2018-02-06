import lombok.Data;

import java.util.Map;

/**
 * Created by dwiveddi on 2/6/2018.
 */
@Data
public class Response {
     private int statusCode;
     private String payload;
     private Map<String,String> headers;

     @Override
     public String toString() {
          return "Response{" +
                  "statusCode=" + statusCode +
                  ", payload='" + payload + '\'' +
                  ", headers=" + headers +
                  '}';
     }

     public int getStatusCode() {
          return statusCode;
     }

     public void setStatusCode(int statusCode) {
          this.statusCode = statusCode;
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
