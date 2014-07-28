package consul;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.codec.binary.Base64;

public class KeyValue extends ConsulChain {

    public KeyValue(Consul consul) {
        super(consul);
    }

    public void set(String key, String value) throws UnirestException {
        final HttpResponse<String> resp =
            Unirest.put(consul().getUrl() + EndpointCategory.KV.getUri() + key).body(value).asString();
    }

    public String get(String key) throws UnirestException {
        final HttpResponse<JsonNode> resp =
            Unirest.get(consul().getUrl() + EndpointCategory.KV.getUri() + key).asJson();

        KV keyValue = new KV(resp.getBody().getArray().getJSONObject(0));
        byte[] valueDecoded= Base64.decodeBase64(keyValue.getValue() );

        return new String(valueDecoded);
    }

    public void delete(String key) throws UnirestException {
        final HttpResponse<String> resp =
            Unirest.delete(consul().getUrl() + EndpointCategory.KV.getUri() + key).asString();
    }
}
