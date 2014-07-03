package consul;
import com.mashape.unirest.http.Unirest;


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
            System.out.println(c.catalog().services());

            System.out.println(c.catalog().service("mumble"));
        } finally {
            Unirest.shutdown();
        }
    }

    public Catalog catalog() {
        return new Catalog(this);
    }

    String getUrl() {
        return uri + ":" + port;
    }
}
