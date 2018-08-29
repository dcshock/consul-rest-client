package consul;

public enum EndpointCategory {
    Catalog("/catalog"),
    Check("/health/checks"),
    HealthService("/health/service"),
    Agent("/agent"),
    KV("/kv"),
    Session("/session");

    private String uri;

    EndpointCategory(String uri) {
        this.uri = "/v1" + uri + "/";
    }

    public String getUri() {
        return uri;
    }
}
