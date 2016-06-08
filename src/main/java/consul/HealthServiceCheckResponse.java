package consul;

import java.util.List;

public class HealthServiceCheckResponse {
    private String consulIndex;
    private List<HealthServiceCheck> serviceList;

    public HealthServiceCheckResponse(String consulIndex, List<HealthServiceCheck> serviceList) {
        this.consulIndex = consulIndex;
        this.serviceList = serviceList;
    }

    public String getConsulIndex() {
        return consulIndex;
    }

    public List<HealthServiceCheck> getServiceList() {
        return serviceList;
    }
}
