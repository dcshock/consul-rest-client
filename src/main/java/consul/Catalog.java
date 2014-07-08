package consul;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Catalog extends ConsulChain {
    static final String uri = "/v1/catalog/";

    public Catalog(Consul consul) {
        super(consul);
    }

    public void deregister() {
        throw new RuntimeException("Not yet implemented.");
    }

    public List<DataCenter> datacenters()
      throws UnirestException {
        final List<DataCenter> list = new ArrayList<DataCenter>();
        final HttpResponse<JsonNode> resp = Unirest.get(consul.getUrl() + this.uri + "datacenters").asJson();

        final JSONArray arr = resp.getBody().getArray();
        for (int i = 0; i < arr.length(); i++) {
            list.add(new DataCenter(consul, arr.getString(i)));
        }

        return list;
    }

    public DataCenter datacenter(String name)
      throws UnirestException {
        for (DataCenter dc : datacenters()) {
            if (name.equals(dc.getName()))
                return dc;
        }
        return null;
    }

    public Service services()
      throws UnirestException {
        final HttpResponse<JsonNode> resp = Unirest.get(consul.getUrl() + "/v1/catalog/services").asJson();

        final Service s = new Service(consul);

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

    public Service service(String name)
      throws UnirestException {
        final HttpResponse<JsonNode> resp = Unirest.get(consul.getUrl() + "/v1/catalog/service/{name}")
            .routeParam("name", name)
            .asJson();

        final Service s = new Service(consul);

        final JSONArray arr = resp.getBody().getArray();
        for (int i = 0; i < arr.length(); i++) {
            s.add(arr.getJSONObject(i));
        }

        return s;
    }
}
