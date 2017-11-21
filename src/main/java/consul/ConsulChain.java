package consul;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class ConsulChain {
    static ObjectMapper mapper = new ObjectMapper();
    private Consul consul;

    protected ConsulChain(Consul consul) {
        if (consul == null)
            throw new IllegalArgumentException("Consul object cannot be null");
        this.consul = consul;
    }

    /**
     * Return the consul object that is reading from the node from which the accessed object was populated.
     */
    public Consul consul() {
        return consul;
    }

    public static JsonNode checkResponse(HttpResp response) throws ConsulException {
         if (response.getStatus() >= Http.INTERNAL_SERVER_ERROR) {
             throw new ConsulException("Error Status Code: " + response.getStatus() + " body: " + response.getBody());
         }
         return parseJson(response.getBody());
    }

    public static JsonNode parseJson(String body) throws ConsulException {
        try {
            if (body == null || "".equals(body.trim())) {
                return mapper.createObjectNode();
            }
            return mapper.readTree(body.getBytes());
        } catch (IOException e) {
            throw new ConsulException("Invalid Json found: " + body, e);
        }
    }
}
