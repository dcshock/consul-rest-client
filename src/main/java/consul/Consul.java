package consul;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.options.Options;
import org.json.JSONArray;

import java.io.IOException;

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
        try {
            final Consul c = new Consul("http://localhost", 8500);
            c.agent().register(new ServiceProvider("test1", "test", 8302, null));
            c.agent().checkRegister(new AgentCheck("test2", "check", "These are some notes", "/usr/local/bin/check_mem.py", "10s", "15s"));
            System.out.println(c.catalog().services());
            for (Service s : c.catalog().services())
                System.out.println(c.catalog().service(s.getName()));
            System.out.println(c.agent().self());
            System.out.println(c.agent().services());
            c.agent().deregister("test1");
            c.agent().checkDeregister("test2");
            System.out.println(c.agent().services());
            final KeyValue kv = new KeyValue(c);
            kv.set("this", "that");
            System.out.println(kv.get("this"));
            kv.delete("this");
        } finally {
            Unirest.shutdown();
        }
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
     *
     * @return
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
     *
     * @param category
     * @param name
     * @return
     * @throws ConsulException
     */
    public Service service(EndpointCategory category, String name) throws ConsulException {
        final Service s = new Service(this);
        final JSONArray arr = ConsulChain.checkResponse(Unirest.get(this.getUrl() + category.getUri() + "service/{name}")
                                                               .routeParam("name", name)).getArray();
        for (int i = 0; i < arr.length(); i++) {
            s.add(arr.getJSONObject(i));
        }

        return s;
    }

    public Health health() {
        return new Health(this);
    }

    /**
     * With some frameworks it is necessary to refresh the unirest connection pool state
     * during runtime. This method does just that.
     */
    public void startup() {
        Options.refresh();
    }

    public void shutdown() {
        try {
            Unirest.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
