package consul;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public class ServiceCheck {
    String id;
    String name;
    String serviceId;
    String serviceName;
    String status;
    String notes;
    boolean useable;

    ServiceCheck(JsonNode obj) {
        id = Optional.ofNullable(obj.get("CheckID")).map(JsonNode::asText).orElse("");
        name = Optional.ofNullable(obj.get("Name")).map(JsonNode::asText).orElse("");
        serviceId = Optional.ofNullable(obj.get("ServiceID")).map(JsonNode::asText).orElse("");
        serviceName = Optional.ofNullable(obj.get("ServiceName")).map(JsonNode::asText).orElse("");
        status = Optional.ofNullable(obj.get("Status")).map(JsonNode::asText).orElse("");
        notes = Optional.ofNullable(obj.get("Notes")).map(JsonNode::asText).orElse("");
        if ("passing".equalsIgnoreCase(status)) {
            useable = true;
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isUseable() {
        return useable;
    }

    @Override
    public String toString() {
        return "ServiceCheck [id=" + id + ", name=" + name + ", serviceId=" + serviceId + ", serviceName=" + serviceName +
                        ", status=" + status + ", notes=" + notes + "]";
    }
}
