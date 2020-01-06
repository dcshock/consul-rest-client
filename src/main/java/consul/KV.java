package consul;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public class KV {
    String createIndex;
    String modifyIndex;
    String lockIndex;
    String key;
    Integer flags;
    String value;
    String sessionId;

    public KV() {
    }

    public KV(String createIndex, String modifyIndex, String lockIndex, String key, Integer flags, String value, String sessionId) {
        this.createIndex = createIndex;
        this.modifyIndex = modifyIndex;
        this.lockIndex = lockIndex;
        this.key = key;
        this.flags = flags;
        this.value = value;
        this.sessionId = sessionId;
    }

    KV(JsonNode obj) {
        this.createIndex = Optional.ofNullable(obj.get("CreateIndex")).map(JsonNode::asText).orElse("");
        this.modifyIndex = Optional.ofNullable(obj.get("ModifyIndex")).map(JsonNode::asText).orElse("");
        this.lockIndex = Optional.ofNullable(obj.get("LockIndex")).map(JsonNode::asText).orElse("");
        this.key = Optional.ofNullable(obj.get("Key")).map(JsonNode::asText).orElse("");
        this.flags = Optional.ofNullable(obj.get("Flags")).map(JsonNode::asInt).orElse(0);
        this.value = Optional.ofNullable(obj.get("Value")).map(n -> n.asText("")).orElse("");
        this.sessionId = Optional.ofNullable(obj.get("Session")).map(JsonNode::asText).orElse("");
    }

    public String getCreateIndex() {
        return createIndex;
    }

    public void setCreateIndex(String createIndex) {
        this.createIndex = createIndex;
    }

    public String getModifyIndex() {
        return modifyIndex;
    }

    public void setModifyIndex(String modifyIndex) {
        this.modifyIndex = modifyIndex;
    }

    public String getLockIndex() {
        return lockIndex;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setLockIndex(String lockIndex) {
        this.lockIndex = lockIndex;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String toString() {
        return "KV [createIndex=" + createIndex + ", modifyIndex=" + modifyIndex + ", lockIndex=" + lockIndex + ", key=" + key +
                        ", flags=" + flags + ", value=" + value + ", sessionId=" + sessionId + "]";
    }
}
