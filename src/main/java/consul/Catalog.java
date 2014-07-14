package consul;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Catalog extends ConsulChain {
    public Catalog(Consul consul) {
        super(consul);
    }

    public void deregister() {
        throw new RuntimeException("Not yet implemented.");
    }

    public List<DataCenter> datacenters()
      throws UnirestException {
        final List<DataCenter> list = new ArrayList<DataCenter>();
        final HttpResponse<JsonNode> resp = Unirest.get(consul.getUrl() + EndpointCategory.Catalog.getUri() + "datacenters").asJson();

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
        return consul.services(EndpointCategory.Catalog);
    }

    public Service service(String name)
      throws UnirestException {
        return consul.service(EndpointCategory.Catalog, name);
    }
}
