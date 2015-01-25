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

    public void set(String key, String value) throws ConsulException {
        try {
            final HttpResponse<String> resp =
                Unirest.put(consul().getUrl() + EndpointCategory.KV.getUri() + key).body(value).asString();
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }
    }

    public String get(String key) throws ConsulException {
        final HttpResponse<JsonNode> resp;
        try {
            resp = Unirest.get(consul().getUrl() + EndpointCategory.KV.getUri() + key).asJson();
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }

        KV keyValue = new KV(resp.getBody().getArray().getJSONObject(0));
        byte[] valueDecoded= Base64.decodeBase64(keyValue.getValue() );

        return new String(valueDecoded);
    }

    public void delete(String key) throws ConsulException {
        try {
            final HttpResponse<String> resp =
                Unirest.delete(consul().getUrl() + EndpointCategory.KV.getUri() + key).asString();
        } catch (UnirestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
