package consul;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.Objects;

/**
 * Orchestrates access to Consul's http api.
 *
 * @author mconroy
 *
 */
public class Consul {
    private String uri;
    private int port;

    public Consul(String uri, int port) {
        this.uri = uri;
        this.port = port;
    }

    public static void main(String args[]) throws Exception {
        final Consul c = new Consul("http://localhost", 8500);
        c.agent().register(new ServiceProvider("test1", "test", 8302, null));
        c.agent().checkRegister(new AgentCheck("test2", "check", "These are some notes", "/usr/local/bin/check_mem.py", "10s", "15s"));
        c.catalog().services().stream().forEach(System.out::println);
        for (Service s : c.catalog().services()) {
            c.catalog().service(s.getName()).getProviders().stream().forEach(System.out::println);
        }
        System.out.println(c.agent().self());
        c.agent().services().stream().forEach(System.out::println);
        c.agent().deregister("test1");
        c.agent().checkDeregister("test2");
        c.agent().services().stream().forEach(System.out::println);
        final KeyValue kv = new KeyValue(c);
        kv.set("this", "that");
        System.out.println(kv.get("this"));
        kv.delete("this");
    }

    /**
     * Get the catalog from the consul node.
     *
     * @return - Catalog
     */
    public Catalog catalog() {
        return new Catalog(this);
    }

    /**
     * An agent instance.
     */
    public Agent agent() {
        return new Agent(this);
    }

    public Session session() {
        return new Session(this);
    }

    public KeyValue keyStore() {
        return new KeyValue(this);
    }

    /**
     * Call the service api of consul using the given endpoint.
     * @throws ConsulException
     */
    public Service service(EndpointCategory category, String name, String datacenter) throws ConsulException {
        try {
            if (!Objects.equals(datacenter, "")) {
                datacenter = "?dc=" + datacenter;
            }
            final Service s = new Service(this);
            final HttpResp resp = Http.get(this.getUrl() + category.getUri() + "service/" + name + datacenter);
            final JsonNode node = ConsulChain.checkResponse(resp);
            final ArrayNode arr = (ArrayNode)node;
            for (int i = 0; i < arr.size(); i++) {
                s.add(arr.get(i));
            }
            return s;
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public Health health() {
        return new Health(this);
    }

    /**
     * With some frameworks it is necessary to refresh state during runtime.
     */
    public void startup() {
        // No-op
    }

    public void shutdown() {
        // No-op
    }

    /**
     * Url is a combo of uri + ":" + port
     *
     * @return - Url
     */
    public String getUrl() {
        return uri + ":" + port;
    }
}
