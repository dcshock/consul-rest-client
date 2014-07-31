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
    public Catalog(Consul consul) {
        super(consul);
    }

    public void deregister() {
        throw new RuntimeException("Not yet implemented.");
    }

    public List<DataCenter> datacenters()
      throws ConsulException {
        final List<DataCenter> list = new ArrayList<DataCenter>();
        final HttpResponse<JsonNode> resp;
        try {
            resp = Unirest.get(consul().getUrl() + EndpointCategory.Catalog.getUri() + "datacenters").asJson();
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }

        final JSONArray arr = resp.getBody().getArray();
        for (int i = 0; i < arr.length(); i++) {
            list.add(new DataCenter(consul(), arr.getString(i)));
        }

        return list;
    }

    public DataCenter datacenter(String name)
      throws ConsulException {
        for (DataCenter dc : datacenters()) {
            if (name.equals(dc.getName()))
                return dc;
        }
        return null;
    }

    /**
     * Call the services api of Consul using the given endpoint.
     *
     * @param category
     * @return
     * @throws ConsulException
     */
    public List<Service> services() throws ConsulException {
        final List<Service> services = new ArrayList<Service>();

        final HttpResponse<JsonNode> resp;
        try {
            resp = Unirest.get(consul().getUrl() + EndpointCategory.Catalog.getUri() + "services").asJson();
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }

        final JSONObject obj = resp.getBody().getObject();

        for (Object key : obj.keySet()) {


            final JSONArray arr = (JSONArray)obj.get(key.toString());
            final String[] tags = new String[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                tags[i] = arr.getString(i);
            }

            final Service s = new Service(consul(), key.toString(), tags);
            services.add(s);
        }

        return services;
    }

    public Service service(String name)
      throws ConsulException {
        return consul().service(EndpointCategory.Catalog, name);
    }
}
