package consul;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mashape.unirest.http.exceptions.UnirestException;

public class Agent extends ConsulChain {
    Agent(Consul consul) {
        super(consul);
    }

    public Service services() throws UnirestException {
        return consul.services(EndpointCategory.Agent);
    }

    public Service service(String name) throws UnirestException {
        return consul.service(EndpointCategory.Agent, name);
    }

    public String register(ServiceProvider provider) throws UnirestException {
        final JSONArray tags = new JSONArray();
        if (provider.getTags() != null) {
            tags.put(provider.getTags());
        }

        final JSONObject service = new JSONObject();
        service.put("ID", provider.getId());
        service.put("Name", provider.getName());
        service.put("Port", provider.getPort());
        if (tags.length() > 0) {
            service.put("Tags", tags);
        }

        final HttpResponse<String> resp =
            Unirest.put(consul.getUrl() + EndpointCategory.Agent.getUri() + "service/register").body(service.toString()).asString();

        return resp.getBody().toString();
    }
}
