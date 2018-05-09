package consul;

import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class HealthService extends ConsulChain {
    public static final String INDEX_HEADER = "X-Consul-Index".toLowerCase();

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

    // Need this to 'soften' the IOException for use with CompletableFuture.
    private HttpResp doGet(final String url) {
        try {
            return Http.get(url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        return checkAcrossDatacenters(name, consulIndex, waitTimeSeconds, passing, Executors.newSingleThreadExecutor());
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
     * @param executorService An executor service for use with completable future
     * @throws ConsulException
     */
    public HealthServiceCheckResponse check(
        String name, String consulIndex, int waitTimeSeconds, boolean passing, ExecutorService executorService, String datacenter
    ) throws ConsulException {
        HttpResp resp;
        String prefix = "?";
        String params = "";
        if (consulIndex != null) {
            params = params + prefix + "index=" + consulIndex;
            params = params + "&wait=" + waitTimeSeconds + "s";
            prefix = "&";
        }
        if (passing) {
            params = params + prefix + "passing=true";
        }
        if (!Objects.equals(datacenter, "")) {
            params = params + prefix + "dc=" + datacenter;
        }
        final String p = params; // ugh! java lambdas
        try {
            resp = CompletableFuture.supplyAsync(
                () -> doGet(consul().getUrl() + EndpointCategory.HealthService.getUri() + name + p),
                executorService
            ).get((long)Math.ceil(1.1f * waitTimeSeconds), TimeUnit.SECONDS);
        } catch (RuntimeException | ExecutionException | InterruptedException | TimeoutException e) {
           throw new ConsulException(e);
        }
        final String newConsulIndex = resp.getFirstHeader(INDEX_HEADER);
        final List<HealthServiceCheck> serviceChecks = new ArrayList<>();
        if (resp.getStatus() >= Http.INTERNAL_SERVER_ERROR) {
            throw new ConsulException("Error Status Code: " + resp.getStatus() + "  body: " + resp.getBody());
        }
        final JsonNode arr = parseJson(resp.getBody());
        for (int i = 0; i < arr.size(); i++) {
            serviceChecks.add(new HealthServiceCheck(arr.get(i)));
        }
        return new HealthServiceCheckResponse(newConsulIndex, serviceChecks);
    }

    public HealthServiceCheckResponse checkAcrossDatacenters(
                    String name, String consulIndex, int waitTimeSeconds, boolean passing, ExecutorService executorService
    ) throws ConsulException {
        ConsulException lastException = null;
        for (DataCenter datacenter : this.consul().catalog().datacenters()) {
            try {
                return this.check(name, consulIndex, waitTimeSeconds, passing, executorService, datacenter.getName());
            } catch(ConsulException e) {
                // Try next datacenter
                lastException = e;
            }
        }
        throw new ConsulException("Failed to find health check across datacenters: " + lastException);
    }
}
