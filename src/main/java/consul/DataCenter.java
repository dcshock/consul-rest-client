package consul;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataCenter extends ConsulChain {
    private String name;

    public DataCenter(Consul consul) {
        super(consul);
    }

    public DataCenter(Consul consul, String name) {
        this(consul);
        this.name = name;
    }

    public List<Node> nodes() throws ConsulException {
        try {
            final List<Node> nodes = new ArrayList<>();
            final HttpResp resp = Http.get(consul().getUrl() + EndpointCategory.Catalog.getUri() + "nodes");
            final JsonNode node = checkResponse(resp);
            if (!node.isArray()) {
                throw new ConsulException("Expected a json array in DataCenter::nodes()");
            }
            final ArrayNode arr = (ArrayNode) node;
            for (int i = 0; i < arr.size(); i++) {
                final JsonNode obj = arr.get(i);
                nodes.add(new Node(consul(), this, obj.get("Node").asText(), obj.get("Address").asText()));
            }
            return nodes;
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public Node node(String name) throws ConsulException {
        for (Node n : nodes()) {
            if (name.equals(n.getName())) {
                return n;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DataCenter [name=" + name + "]";
    }
}
