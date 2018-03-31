import lombok.Data;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.ObjectMapper;
import org.dwiveddi.utils.csv.annotation.CsvMapped;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dwiveddi on 2/6/2018.
 */
@Data
public class Response {
     @CsvMapped.Column(index = 6, converterMethod = "getInt") private int statusCode;
     @CsvMapped.Column(index = 7, converterMethod = "convertTopMap") private Map<String,String> headers = new HashMap<>();
     @CsvMapped.Column(index = 8) private String payload;
     @CsvMapped.Column(index = 9, converterMethod = "getBoolean")private boolean payloadJsonValdationRequired = false;
     @CsvMapped.Column(index = 10,converterMethod = "getPayloadStructure" )private PayloadStructure payloadStructure = PayloadStructure.JSON;
     @CsvMapped.Column(index = 11, converterMethod = "getList")private List<Object> jsonAttributes;

     public int getInt(String s){
          return Integer.parseInt(s.trim());
     }
     public boolean getBoolean(String s){
          return s.isEmpty() ? false : Boolean.parseBoolean(s.trim().toLowerCase());
     }
     public List<Object> getList(String s) throws IOException {
          return s.isEmpty() ? null : new ObjectMapper().readValue(s, List.class);
     }
     public PayloadStructure getPayloadStructure(String s){
          return s.isEmpty() ? null : PayloadStructure.valueOf(s);
     }
     public Map<String, String> convertTopMap(String headers) throws IOException {
          return new ObjectMapper().readValue(headers, Map.class);
     }

     @Override
     public String toString() {
          return "Response{" +
                  "statusCode=" + statusCode +
                  ", payload='" + payload + '\'' +
                  ", headers=" + headers +
                  ", payloadJsonValdationRequired=" + payloadJsonValdationRequired +
                  ", payloadStructure=" + payloadStructure +
                  ", jsonAttributes=" + jsonAttributes +
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

     public boolean isPayloadJsonValdationRequired() {
          return payloadJsonValdationRequired;
     }

     public void setPayloadJsonValdationRequired(boolean payloadJsonValdationRequired) {
          this.payloadJsonValdationRequired = payloadJsonValdationRequired;
     }

     public List<Object> getJsonAttributes() {
          return jsonAttributes;
     }

     public void setJsonAttributes(List<Object> jsonAttributes) {
          this.jsonAttributes = jsonAttributes;
     }

     public PayloadStructure getPayloadStructure() {
          return payloadStructure;
     }

     public void setPayloadStructure(PayloadStructure payloadStructure) {
          this.payloadStructure = payloadStructure;
     }
}
