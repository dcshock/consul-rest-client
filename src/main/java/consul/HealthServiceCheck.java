package consul;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HealthServiceCheck {
    ServiceProvider provider;
    List<ServiceCheck> checks;

    HealthServiceCheck(JsonNode node) {
        final ArrayNode checks = (ArrayNode) node.get("Checks");
        this.provider = new ServiceProvider(node.get("Service"));
        this.checks = new ArrayList<>(checks.size());
        this.provider.address = node.get("Node").get("Address").asText();
        this.provider.node = node.get("Node").get("Node").asText();
        for(int i = 0; i < checks.size(); i++) {
            this.checks.add(new ServiceCheck(checks.get(i)));
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
