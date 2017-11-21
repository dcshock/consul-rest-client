import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import consul.Consul;
import consul.ConsulException;
import consul.HealthService;
import consul.HealthServiceCheck;
import consul.HealthServiceCheckResponse;
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
    public static void before() throws ConsulException {
        c = new Consul("http://localhost", 8500);
    }

    @AfterClass
    public static void cleanup() throws ConsulException {
        c.shutdown();
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
}
