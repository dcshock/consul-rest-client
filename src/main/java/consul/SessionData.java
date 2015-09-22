package consul;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public class SessionData {
    public enum Behavior {
        RELEASE("release"),
        DELETE("delete");

        private String type;

        private Behavior(String type) {
            this.type = type;
        }

        @JsonValue
        public String getType() {
            return type;
        }
    }

    private Session sessionHandler;
    private String id;
    private String lockDelay;
    private String name;
    private String node;
    private String[] checks;
    private Behavior behavior;
    private String ttl;
    private String createIndex;

    SessionData() {
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

    /**
     * Destroy the session in consul.
     * @return
     * @throws ConsulException
     */
    public boolean destroy() throws ConsulException {
        return sessionHandler.destroy(this);
    }

    /**
     * Renew the session in consul. This works with 0.5.2 and above.
     * @return
     * @throws ConsulException
     */
    public boolean renew()  throws ConsulException {
        final SessionData newData = sessionHandler.renew(this).get(0);

        this.setBehavior(newData.getBehavior())
            .setChecks(newData.getChecks())
            .setCreateIndex(newData.getCreateIndex())
            .setId(newData.getId())
            .setLockDelay(newData.getLockDelay())
            .setName(newData.getName())
            .setNode(newData.getNode())
            .setTtl(newData.getTtl());

        return true;
    }
}
