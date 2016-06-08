package consul;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HealthServiceCheck {
    ServiceProvider provider;
    List<ServiceCheck> checks;

    HealthServiceCheck(JSONObject node) {
        JSONArray checks = node.getJSONArray("Checks");

        this.provider = new ServiceProvider(node.getJSONObject("Service"));
        this.checks = new ArrayList<>(checks.length());
        this.provider.address = node.getJSONObject("Node").getString("Address");
        this.provider.node = node.getJSONObject("Node").getString("Node");

        for(int i = 0; i < checks.length(); i++) {
            this.checks.add(new ServiceCheck(checks.getJSONObject(i)));
        }
    }

    public ServiceProvider getProvider() {
        return provider;
    }

    public List<ServiceCheck> getChecks() {
        return checks;
    }

    @Override
    public String toString() {
        return "HealthServiceCheck{" +
               "provider=" + provider +
               ", checks=" + String.join(",", checks.stream().map(ServiceCheck::toString).collect(Collectors.toList())) +
                                              '}';
    }
}
