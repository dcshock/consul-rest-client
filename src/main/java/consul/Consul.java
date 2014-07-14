package consul;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;
import com.mashape.unirest.http.Unirest;

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
            System.out.println(c.catalog().datacenters());
            System.out.println(c.catalog().datacenter("dc1"));
            System.out.println(c.catalog().datacenter("dc1").nodes());
            System.out.println(c.catalog().datacenter("dc1").nodes().get(0).register(new ServiceProvider("test1", "test", 8080, null)));
            System.out.println(c.agent().register(new ServiceProvider("test1", "test", 8080, null)));
            System.out.println(c.agent().services());
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

    /**
     * Call the services api of Consul using the given endpoint.
     *
     * @param category
     * @return
     * @throws UnirestException
     */
    public Service services(EndpointCategory category) throws UnirestException {
        final HttpResponse<JsonNode> resp = Unirest.get(getUrl() + category.getUri() + "services").asJson();

        final Service s = new Service(this);

        final JSONObject obj = resp.getBody().getObject();
        for (Object key : obj.keySet()) {
            final JSONArray arr = (JSONArray)obj.get(key.toString());
            final String[] tags = new String[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                tags[i] = arr.getString(i);
            }
            s.add(key.toString(), tags);
        }

        return s;
    }

    /**
     * Call the service api of consul using the given endpoint.
     *
     * @param category
     * @param name
     * @return
     * @throws UnirestException
     */
    public Service service(EndpointCategory category, String name) throws UnirestException {
        final HttpResponse<JsonNode> resp =
            Unirest.get(this.getUrl() + category.getUri() + "service/{name}").routeParam("name", name).asJson();

        final Service s = new Service(this);

        final JSONArray arr = resp.getBody().getArray();
        for (int i = 0; i < arr.length(); i++) {
            s.add(arr.getJSONObject(i));
        }

        return s;
    }

    /**
     * Url is a combo of uri + ":" + port
     *
     * @return - Url
     */
    String getUrl() {
        return uri + ":" + port;
    }
}
