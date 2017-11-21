import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import consul.Consul;
import consul.ConsulException;
import consul.Session;
import consul.SessionData;
import consul.SessionData.Behavior;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

@Ignore
public class SessionTest {
    private static Session s;

    @BeforeClass
    public static void before() {
        s = new Consul("http://localhost", 8500).session();
    }

    @After
    public void cleanup() throws ConsulException {
        s.all().forEach((session) -> {
            try {
                session.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testCreate() throws Exception {
        String sessionId = s.create("testCreate" + System.currentTimeMillis());
        SessionData data = s.info(sessionId);
        assertNotNull(data);
        assertEquals("15000000000", data.getLockDelay());
        assertEquals("0s", data.getTtl());
        assertEquals(1, data.getChecks().length);
        assertEquals("serfHealth", data.getChecks()[0]);
        assertEquals(Behavior.RELEASE, data.getBehavior());
        assertTrue(data.destroy());

        sessionId = s.create("testCreate2" + System.currentTimeMillis(), 12, Behavior.DELETE, 60);
        data = s.info(sessionId);
        assertEquals("12000000000", data.getLockDelay());
        assertEquals("60s", data.getTtl());
        assertEquals(1, data.getChecks().length);
        assertEquals("serfHealth", data.getChecks()[0]);
        assertEquals(Behavior.DELETE, data.getBehavior());
        assertTrue(data.destroy());
    }

    @Test
    public void testGetInfoRandomSession() {
        try {
            String id = UUID.randomUUID().toString();
            assertNull(s.info(id));
        } catch (Exception e) {
            e.printStackTrace();
            fail("testGetInfoRandomSession:" + e);
        }
    }

    @Test
    public void testGetInfoNullOrBlankSession() {
        try {
            assertNull(s.info(null));
            assertNull(s.info(""));
        } catch (Exception e) {
            e.printStackTrace();
            fail("testGetInfoNullOrBlankSession:" + e);
        }
    }

    @Test
    public void testRenew() throws ConsulException {
        final String id = s.create(UUID.randomUUID().toString());
        assertNotNull(id);
        assertTrue(s.info(id).renew());
    }

    @Test
    public void testAll() throws ConsulException {
        final String id = s.create(UUID.randomUUID().toString());

        final List<SessionData> sessions = s.all();
        assertEquals(1, sessions.size());
        assertEquals(id, sessions.get(0).getId());
    }

    @Test
    public void testInfo() throws ConsulException {
        final String id = s.create(UUID.randomUUID().toString());

        final SessionData data = s.info(id);
        assertNotNull(data);
        assertEquals(id, data.getId());
        assertEquals("15000000000", data.getLockDelay());
        assertEquals("0s", data.getTtl());
        assertEquals(1, data.getChecks().length);
        assertEquals("serfHealth", data.getChecks()[0]);
        assertEquals(Behavior.RELEASE, data.getBehavior());
    }

    @Test
    public void testNode() throws ConsulException {
        final String id = s.create(UUID.randomUUID().toString());
        final String node = s.info(id).getNode();

        final List<SessionData> nodeSessions = s.node(node);
        assertEquals(1, nodeSessions.size());
        assertEquals(id, nodeSessions.get(0).getId());
    }
}
