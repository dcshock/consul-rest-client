package consul;

import com.mashape.unirest.http.Unirest;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public List<Node> nodes()
      throws ConsulException {
        final List<Node> nodes = new ArrayList<>();
        final JSONArray arr = checkResponse(Unirest.get(consul().getUrl() + EndpointCategory.Catalog.getUri() + "nodes")).getArray();
        for (int i = 0; i < arr.length(); i++) {
            final JSONObject obj = arr.getJSONObject(i);
            nodes.add(new Node(consul(), this, obj.getString("Node"), obj.getString("Address")));
        }

        return nodes;
    }

    public Node node(String name)
      throws ConsulException {
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
