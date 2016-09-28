package consul;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.GetRequest;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HealthService extends ConsulChain {
    public static final String INDEX_HEADER = "X-Consul-Index" .toLowerCase();

    public HealthService(Consul c) {
        super(c);
    }

    /**
     * Calls consul's health check for service, only returns services with passing health checks
     *
     * https://www.consul.io/docs/agent/http/health.html#health_service
     * @param name Service name to look up
     * @return List of HealServiceChecks
     * @throws ConsulException
     */
    public List<HealthServiceCheck> check(String name) throws ConsulException {
        return check(name, null, 30, true).getServiceList();
    }

    /**
     * Call the health service check consul end point
     *
     * When consul index is specified the consul service will wait to to return services until either the waitTime has expired or
     * a change has happened to the specified service.
     *
     * More details: https://www.consul.io/docs/agent/watches.html
     *
     * @param name Service name to look up
     * @param consulIndex When not null passes this index to consul to allow a blocked query
     * @param waitTimeSeconds time to pass to consul to wait for changes to a service
     * @param passing if true only returns services that are passing their health check.
     * @return Response object with Consul-Index and a list of services
     * @throws ConsulException
     */
    public HealthServiceCheckResponse check(String name, String consulIndex, int waitTimeSeconds, boolean passing) throws ConsulException {
        HttpResponse<String> resp;
        GetRequest request = Unirest.get(consul().getUrl() + EndpointCategory.HealthService.getUri() + "{name}")
                                    .routeParam("name", name);
        if (consulIndex != null) {
            request.queryString("index", consulIndex);
            request.queryString("wait", waitTimeSeconds + "s");
        }
        if (passing) {
            request.queryString("passing", "true");
        }
        try {
            resp = request.asStringAsync().get((long)Math.ceil(1.1f * waitTimeSeconds), TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
           throw new ConsulException(e);
        }

        String newConsulIndex = resp.getHeaders().getFirst(INDEX_HEADER);
        List<HealthServiceCheck> serviceChecks = new ArrayList<>();
        if (resp.getStatus() >= 500) {
            throw new ConsulException("Error Status Code: " + resp.getStatus() + "  body: " + resp.getBody());
        }
        JSONArray arr = parseJson(resp.getBody()).getArray();
        for (int i = 0; i < arr.length(); i++) {
            serviceChecks.add(new HealthServiceCheck(arr.getJSONObject(i)));
        }
        return new HealthServiceCheckResponse(newConsulIndex, serviceChecks);
    }
}
