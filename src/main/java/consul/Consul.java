package consul;
import com.mashape.unirest.http.Unirest;

/**
 * Orchestrates access to Consul's http api.
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

    public static void main(String args[])
      throws Exception {
        try {
            final Consul c = new Consul("http://localhost", 8500);
            System.out.println(c.catalog().datacenters());
            System.out.println(c.catalog().datacenter("dc1"));
            System.out.println(c.catalog().datacenter("dc1").nodes());
            System.out.println(c.catalog().datacenter("dc1").nodes().get(0).register(new ServiceProvider("test1", "test", 8080, null)));
        } finally {
            Unirest.shutdown();
        }
    }

    /**
     * Get the catalog from the consul node.
     * @return - Catalog
     */
    public Catalog catalog() {
        return new Catalog(this);
    }

    /**
     * Url is a combo of uri + ":" + port
     * @return - Url
     */
    String getUrl() {
        return uri + ":" + port;
    }
}
