package consul;

public class Self {
    private String address;
    private Integer port;
    private String node;

    public Self(String address, Integer port, String node) {
        this.address = address;
        this.port = port;
        this.node = node;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPort() {
        return port;
    }

    public String getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "Self [address=" + address + ", port=" + port + ", node=" + node + "]";
    }
}
