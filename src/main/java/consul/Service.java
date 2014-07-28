package consul;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Service extends ConsulChain {
    private String name;
    private String[] tags;
    private List<ServiceProvider> providers = new ArrayList<ServiceProvider>();

    Service(Consul consul) {
        super(consul);
    }

    Service(Consul consul, String name, String[] tags) {
        this(consul);
        this.name = name;
        this.tags = tags;
    }

    void add(JSONObject obj) {
        providers.add(new ServiceProvider(obj));
    }

    void add(String name, String[] tags) {
        providers.add(new ServiceProvider(name, tags));
    }

    ServiceProvider provider(String id) {
        for (ServiceProvider provider : providers) {
            if (provider.id.equals(id))
                return provider;
        }

        return null;
    }

    public String getName() {
        return name;
    }

    public String[] getTags() {
        return tags;
    }

    public List<ServiceProvider> getProviders() {
        return providers;
    }

    @Override
    public String toString() {
        return "Service [name=" + name + ", tags=" + Arrays.toString(tags) + ", providers=" + providers + "]";
    }
}
