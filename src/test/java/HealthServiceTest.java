import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import consul.Consul;
import consul.ConsulException;
import consul.HealthService;
import consul.HealthServiceCheck;
import consul.HealthServiceCheckResponse;
import consul.Http;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HealthServiceTest {
    private static Consul c;
    @BeforeClass
    public static void before() throws ConsulException, Exception {
        c = new Consul("http://localhost", 8500);
        HealthServiceTest.createService("id1", "testService");
    }

    @AfterClass
    public static void cleanup() throws ConsulException, Exception {
        c.shutdown();
        HealthServiceTest.removeService("id1");
    }

    @Test
    public void testCheck() throws ConsulException {
        List<HealthServiceCheck> testService = c.health().service().check("testService");
        testService.forEach(System.out::println);
        assertEquals(1, testService.size());
    }
    @Test
    @Ignore("requires bring up more then one service")
    public void testCheck2() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        HealthService service = c.health().service();
        HealthServiceCheckResponse firstCheck = service.check("testService", null, 30, true);
        long startTime = System.currentTimeMillis();
        CompletableFuture.supplyAsync(() -> {
            try {
                return service.check("testService", firstCheck.getConsulIndex(), 5, true);
            } catch (ConsulException e) {
               throw new RuntimeException(e);
            }
        })
        .whenComplete((r,t) -> {
            if (t == null) {
                System.out.println("Complete: " + r.getServiceList().stream()
                                                         .map(x -> x.getProvider().getName())
                                                         .collect(Collectors.joining(", ")));
            }
            latch.countDown();
        });
        latch.await(6, TimeUnit.SECONDS);
        assertTrue(System.currentTimeMillis() - startTime > 4950);
    }
    
    private static void createService(String id, String name) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("Node", "unittest")
                        .put("Address", "127.0.0.1");
        node.set("Service", mapper.createObjectNode()
                        .put("ID", id)
                        .put("Service", name)
                        .put("Address", "127.0.0.1")
                        .put("Port", 8001));
        node.set("Check", mapper.createObjectNode()
                        .put("Node", "unittest")
                        .put("CheckID", "service:" + id)
                        .put("Name", "Fake check for " + id)
                        .put("Notes", "Fake check")
                        .put("Status", "passing")
                        .put("ServiceID", id));
        Http.put("http://localhost:" + 8500 + "/v1/catalog/register", node.toString());
    }

    private static void removeService(String id) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("Node", "unittest").put("ServiceID", id);
        Http.put("http://localhost:" + 8500 + "/v1/catalog/deregister", node.toString());
    }
}
