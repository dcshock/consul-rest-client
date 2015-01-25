package consul;

import org.json.JSONObject;

public class ServiceCheck {
    String id;
    String name;
    String serviceId;
    String serviceName;
    String status;
    String notes;
    boolean useable;

    ServiceCheck(JSONObject obj) {
        id = obj.optString("CheckID");
        name = obj.optString("Name");
        serviceId = obj.optString("ServiceID");
        serviceName = obj.optString("ServiceName");
        status = obj.optString("Status");
        notes = obj.optString("Notes");

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
