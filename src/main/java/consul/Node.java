package consul;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    public String register(ServiceProvider provider) throws ConsulException {

        final Map<String, Object> service = new HashMap<>();
        service.put("ID", provider.getId());
        service.put("Service", provider.getName());
        service.put("Port", provider.getPort());
        if (provider.getTags() != null && provider.getTags().length > 0) {
            service.put("Tags", Arrays.asList(provider.getTags()));
        }

        final Map<String, Object> obj = new HashMap<>();
        obj.put("Datacenter", this.dc.getName());
        obj.put("Node", this.name);
        obj.put("Address", this.address);
        obj.put("Service", service);

        final HttpResp resp;
        try {
            resp = Http.put(
                consul().getUrl() + EndpointCategory.Catalog.getUri() + "register",
                mapper.writeValueAsString(obj)
            );
        } catch (IOException e) {
            throw new ConsulException(e);
        }

        return resp.getBody();
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
