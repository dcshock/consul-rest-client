package consul;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;

public class KV {
    Integer createIndex;
    Integer modifyIndex;
    Integer lockIndex;
    String key;
    Integer flags;
    String value;
    String sessionId;

    public KV() {
    }

    public KV(Integer createIndex, Integer modifyIndex, Integer lockIndex, String key, Integer flags, String value, String sessionId) {
        this.createIndex = createIndex;
        this.modifyIndex = modifyIndex;
        this.lockIndex = lockIndex;
        this.key = key;
        this.flags = flags;
        this.value = value;
        this.sessionId = sessionId;
    }

    KV(JsonNode obj) {
        this.createIndex = Optional.ofNullable(obj.get("CreateIndex")).map(JsonNode::asInt).orElse(0);
        this.modifyIndex = Optional.ofNullable(obj.get("ModifyIndex")).map(JsonNode::asInt).orElse(0);
        this.lockIndex = Optional.ofNullable(obj.get("LockIndex")).map(JsonNode::asInt).orElse(0);
        this.key = Optional.ofNullable(obj.get("Key")).map(JsonNode::asText).orElse("");
        this.flags = Optional.ofNullable(obj.get("Flags")).map(JsonNode::asInt).orElse(0);
        this.value = Optional.ofNullable(obj.get("Value")).map(JsonNode::asText).orElse("");
        this.sessionId = Optional.ofNullable(obj.get("Session")).map(JsonNode::asText).orElse("");
    }

    public Integer getCreateIndex() {
        return createIndex;
    }

    public void setCreateIndex(Integer createIndex) {
        this.createIndex = createIndex;
    }

    public Integer getModifyIndex() {
        return modifyIndex;
    }

    public void setModifyIndex(Integer modifyIndex) {
        this.modifyIndex = modifyIndex;
    }

    public Integer getLockIndex() {
        return lockIndex;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setLockIndex(Integer lockIndex) {
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
