package consul;

public enum EndpointCategory {
    Catalog("/v1/catalog/"),
    Check("/v1/health/checks/"),
    HealthService("/v1/health/service/"),
    Agent("/v1/agent/"),
    KV("/v1/kv/"),
    Session("/v1/session/");

    private String uri;

    EndpointCategory(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
