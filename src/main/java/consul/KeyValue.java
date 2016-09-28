package consul;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;

public class KeyValue extends ConsulChain {

    public KeyValue(Consul consul) {
        super(consul);
    }

    public boolean set(String key, String value) throws ConsulException {
        // Give garbage, get garbage
        if (key == null || key.trim().length() == 0)
            return false;

        try {
            final HttpResponse<String> resp =
                Unirest.put(consul().getUrl() + EndpointCategory.KV.getUri() + key).body(value).asString();

            return resp.getStatus() == 200;
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }
    }

    public String get(String key) throws ConsulException {
        // Give garbage, get garbage
        if (key == null || key.trim().length() == 0)
            return null;

        final KV keyValue = getDetails(key);
        byte[] valueDecoded = Base64.decodeBase64(keyValue.getValue());

        return new String(valueDecoded);
    }

    public KV getDetails(String key) throws ConsulException {
        // Give garbage, get garbage
        if (key == null || key.trim().length() == 0)
            return null;

        final JsonNode resp = checkResponse(Unirest.get(consul().getUrl() + EndpointCategory.KV.getUri() + key));
        return new KV(resp.getArray().getJSONObject(0));
    }

    public boolean delete(String key) throws ConsulException {
        // Give garbage, get garbage
        if (key == null || key.trim().length() == 0)
            return false;

        try {
            final HttpResponse<String> resp =
                Unirest.delete(consul().getUrl() + EndpointCategory.KV.getUri() + key).asString();

            return resp.getStatus() == 200;
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }
    }

    public boolean acquireLock(String key, String value, String sessionId) throws ConsulException {
        return lock(key, value, sessionId, "acquire");
    }

    public boolean releaseLock(String key, String value, String sessionId) throws ConsulException {
        return lock(key, value, sessionId, "release");
    }

    private boolean lock(String key, String value, String sessionId, String type) throws ConsulException {
        // Give garbage, get garbage
        if (key == null || key.trim().length() == 0 ||
            value == null || value.trim().length() == 0 ||
            sessionId == null || sessionId.trim().length() == 0)
            return false;

        try {
            // Allow the lock to be acquired multiple times.
            final KV kv = getDetails(key);
            if (type.equals("acquire") && kv.getSessionId() != null && !kv.getSessionId().equals(""))
                return kv.getSessionId().equals(sessionId);

            final HttpResponse<String> resp =
                Unirest.put(consul().getUrl() + EndpointCategory.KV.getUri() + key + "?" + type + "=" + sessionId).body(value).asString();

            if (resp.getStatus() != 200)
                return false;

           return mapper.readValue(resp.getBody(), Boolean.class);
        } catch (UnirestException | IOException e) {
            throw new ConsulException(e);
        }
    }
}
