package consul;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

public class Node extends ConsulChain {
    private DataCenter dc;
    private String name;
    private String address;

    Node(Consul consul) {
        super(consul);
    }

    Node(Consul consul, DataCenter dc, String name, String address) {
        this(consul);
        this.dc = dc;
        this.name = name;
        this.address = address;
    }

    public String register(ServiceProvider provider)
      throws ConsulException {
        final JSONArray tags = new JSONArray();
        if (provider.getTags() != null) {
            tags.put(provider.getTags());
        }

        final JSONObject service = new JSONObject();
        service.put("ID", provider.getId());
        service.put("Service", provider.getName());
        service.put("Port", provider.getPort());
        if (tags.length() > 0) {
            service.put("Tags", tags);
        }

        final JSONObject obj = new JSONObject();
        obj.put("Datacenter", this.dc.getName());
        obj.put("Node", this.name);
        obj.put("Address", this.address);
        obj.put("Service", service);

        final HttpResponse<String> resp;
        try {
            resp = Unirest.put(consul().getUrl() + EndpointCategory.Catalog.getUri() + "register").body(obj.toString()).asString();
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }

        return resp.getBody().toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Node [name=" + name + ", address=" + address + "]";
    }
}
