package consul;

import org.apache.commons.codec.binary.Base64;

import org.json.JSONObject;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class KeyValue extends ConsulChain {

    public KeyValue(Consul consul) {
        super(consul);
    }

    public void setKeyValue(String key, String value) throws UnirestException {
        final HttpResponse<String> resp =
                        Unirest.put(consul().getUrl() + EndpointCategory.KV.getUri() + key).body(value).asString();
    }

    public String getValue(String key) throws UnirestException {
        final HttpResponse<JsonNode> resp =
                        Unirest.get(consul().getUrl() + EndpointCategory.KV.getUri() + key).asJson();

        KV keyValue = new KV(resp.getBody().getArray().getJSONObject(0));
        byte[] valueDecoded= Base64.decodeBase64(keyValue.getValue() );

        return new String(valueDecoded);
    }

    public void deleteKey(String key) throws UnirestException {
        final HttpResponse<String> resp =
                        Unirest.delete(consul().getUrl() + EndpointCategory.KV.getUri() + key).asString();
    }
}
