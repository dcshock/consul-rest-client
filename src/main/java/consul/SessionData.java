package consul;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SessionData {
    public enum Behavior {
        RELEASE,
        DELETE
    }

    private Session sessionHandler;
    private String id;
    private String lockDelay = "15s";
    private String name;
    private String node;
    private String[] checks;
    private Behavior behavior = Behavior.RELEASE;
    private String ttl = "0s";
    private String createIndex;

    SessionData() {
    }

    @JsonIgnore
    public Session sessionHandler() {
        return this.sessionHandler;
    }

    @JsonProperty("ID")
    public String getId() {
        return id;
    }

    @JsonProperty("LockDelay")
    public String getLockDelay() {
        return lockDelay;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    @JsonProperty("Node")
    public String getNode() {
        return node;
    }

    @JsonProperty("Checks")
    public String[] getChecks() {
        return checks;
    }

    @JsonProperty("Behavior")
    public Behavior getBehavior() {
        return behavior;
    }

    @JsonProperty("TTL")
    public String getTtl() {
        return ttl;
    }

    @JsonProperty("CreateIndex")
    public String getCreateIndex() {
        return createIndex;
    }

    @JsonProperty("LockDelay")
    public SessionData setLockDelay(String lockDelay) {
        this.lockDelay = lockDelay;
        return this;
    }

    @JsonProperty("Name")
    public SessionData setName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("Node")
    public SessionData setNode(String node) {
        this.node = node;
        return this;
    }

    @JsonProperty("Checks")
    public SessionData setChecks(String... checks) {
        this.checks = checks;
        return this;
    }

    @JsonProperty("Behavior")
    public SessionData setBehavior(Behavior behavior) {
        this.behavior = behavior;
        return this;
    }

    @JsonProperty("TTL")
    public SessionData setTtl(String ttl) {
        this.ttl = ttl;
        return this;
    }

    @JsonProperty("ID")
    public SessionData setId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("CreateIndex")
    public SessionData setCreateIndex(String createIndex) {
        this.createIndex = createIndex;
        return this;
    }

    SessionData setSessionHandler(Session sessionHandler) {
        this.sessionHandler = sessionHandler;
        return this;
    }

    public boolean destroy() throws ConsulException {
        return sessionHandler().destroy(this);
    }
}
