package consul;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Service extends ConsulChain {
    private List<ServiceProvider> providers = new ArrayList<ServiceProvider>();

    Service(Consul consul) {
        super(consul);
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

    @Override
    public String toString() {
        return "Service [providers=" + providers + "]";
    }
}
