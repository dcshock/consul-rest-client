package consul;

public enum EndpointCategory {
    Catalog("/v1/catalog/"),
    Agent("/v1/agent/"),
    KV("/v1/kv/");


    private String uri;

    private EndpointCategory(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
