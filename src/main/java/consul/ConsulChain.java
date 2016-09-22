package consul;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import org.json.JSONException;

public class ConsulChain {
    protected static ObjectMapper mapper = new ObjectMapper();
    private Consul consul;

    protected ConsulChain(Consul consul) {
        if (consul == null)
            throw new IllegalArgumentException("Consul object cannot be null");
        this.consul = consul;
    }

    /**
     * Return the consul object that is reading from the node from which the accessed object was
     * populated.
     * @return
     */
    public Consul consul() {
        return consul;
    }

    public static JsonNode checkResponse(HttpRequest request) throws ConsulException {
         String body;
         try {
             HttpResponse<String> response = request.asString();
             if (response.getStatus() > 404) {
                 throw new ConsulException("Error Status Code: " + response.getStatus());
             }

             body = response.getBody();
             return parseJson(body);
         } catch (UnirestException e) {
             throw new ConsulException(e);
         }
    }

    public static JsonNode parseJson(String body) throws ConsulException {
        try {
            return new com.mashape.unirest.http.JsonNode(body);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof JSONException) {
                throw new ConsulException("Invalid Json found: " + body, (JSONException)e.getCause());
            }
            throw e;
        }
    }
}
