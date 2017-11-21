package consul;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Catalog extends ConsulChain {
    public Catalog(Consul consul) {
        super(consul);
    }

    public void deregister() {
        throw new RuntimeException("Not yet implemented.");
    }

    public List<DataCenter> datacenters() throws ConsulException {
        try {
            final List<DataCenter> list = new ArrayList<>();
            HttpResp resp = Http.get(consul().getUrl() + EndpointCategory.Catalog.getUri() + "datacenters");
            final JsonNode node = checkResponse(resp);
            if (!node.isArray()) {
                throw new ConsulException("Expected an array result in Catalog::datacenters()");
            }
            final ArrayNode arr = (ArrayNode) node;
            for (int i = 0; i < arr.size(); i++) {
                list.add(new DataCenter(consul(), arr.get(i).asText()));
            }
            return list;
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public DataCenter datacenter(String name) throws ConsulException {
        for (DataCenter dc : datacenters()) {
            if (name.equals(dc.getName())) {
                return dc;
            }
        }
        return null;
    }

    /**
     * Call the services api of Consul using the given endpoint.
     * @throws ConsulException
     */
    public List<Service> services() throws ConsulException {
        try {
            final List<Service> services = new ArrayList<>();
            final HttpResp resp = Http.get(consul().getUrl() + EndpointCategory.Catalog.getUri() + "services");
            final JsonNode obj = checkResponse(resp);
            for (final Iterator<String> itr = obj.fieldNames(); itr.hasNext(); ) {
                final String key = itr.next();
                final JsonNode node = obj.get(key);
                if (!node.isArray()) {
                    throw new ConsulException("Expected a json array in Catalog::services()");
                }
                final ArrayNode arr = (ArrayNode) node;
                final String[] tags = new String[arr.size()];
                for (int i = 0; i < arr.size(); i++) {
                    tags[i] = arr.get(i).asText();
                }
                final Service s = new Service(consul(), key, tags);
                services.add(s);
            }
            return services;
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    /**
     * Return the current status from any service check tied to the serviceName.
     * @param serviceName
     * @throws ConsulException
     */
    public List<ServiceCheck> checks(String serviceName) throws ConsulException {
        try {
            final List<ServiceCheck> checks = new ArrayList<>();
            final HttpResp resp = Http.get(consul().getUrl() + EndpointCategory.Check.getUri() + serviceName);
            final JsonNode node = checkResponse(resp);
            if (!node.isArray()) {
                throw new ConsulException("Expected an array result in Catalog::checks(" + serviceName + ")");
            }
            final ArrayNode arr = (ArrayNode) node;
            for (int i = 0; i < arr.size(); i++) {
                final ServiceCheck s = new ServiceCheck(arr.get(i));
                checks.add(s);
            }
            return checks;
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public Service service(String name) throws ConsulException {
        return consul().service(EndpointCategory.Catalog, name);
    }
}
