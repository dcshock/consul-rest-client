package consul;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;
import java.util.Optional;

public class ServiceProvider {
    String id;
    String address;
    String node;
    String name;
    Integer port;
    String datacenter;
    String[] tags;
    boolean critical;

    public ServiceProvider() {

    }

    public ServiceProvider(String id, String name, Integer port, String[] tags) {
        this.id = id;
        this.name = name;
        this.port = port;
        this.tags = tags;
    }

    ServiceProvider(String name, String[] tags) {
        this.name = name;
        this.tags = tags;
    }

    ServiceProvider(JsonNode obj) {
        id = obj.has("ServiceID") ? obj.get("ServiceID").asText() : Optional.ofNullable(obj.get("ID")).map(JsonNode::asText).orElse("");
        address = Optional.ofNullable(obj.get("Address")).map(JsonNode::asText).orElse("");
        node = Optional.ofNullable(obj.get("Node")).map(JsonNode::asText).orElse("");
        name = obj.has("ServiceName") ? obj.get("ServiceName").asText() : Optional.ofNullable(obj.get("Service")).map(JsonNode::asText).orElse("");
        port = obj.has("ServicePort") ? obj.get("ServicePort").asInt() : Optional.ofNullable(obj.get("Port")).map(JsonNode::asInt).orElse(0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    public boolean isCritical() {
        return critical;
    }

    public void setDatacenter(String datacenter) {
        this.datacenter = datacenter;
    }

    public String getDatacenter() {
        return datacenter;
    }

    @Override
    public String toString() {
        return "ServiceProvider [id=" + id + ", address=" + address + ", node=" + node + ", name=" + name + ", port=" + port +
                        ", datacenter=" + datacenter + ", tags=" + Arrays.toString(tags) + ", critical=" + critical + "]";
    }
}
