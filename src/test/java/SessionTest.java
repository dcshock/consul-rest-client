import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import consul.Consul;
import consul.ConsulException;
import consul.Session;
import consul.SessionData;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

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
        String id = s.create("" + System.currentTimeMillis());
        assertNotNull(id);
    }

    @Test
    public void testAll() throws ConsulException {
        final String id  = s.create("" + System.currentTimeMillis());

        final List<SessionData> sessions = s.all();
        assertEquals(1, sessions.size());
        assertEquals(id, sessions.get(0).getId());
    }

    @Test
    public void testInfo() throws ConsulException {
        final String id = s.create("" + System.currentTimeMillis());

        final List<SessionData> sessionInfos = s.info(id);
        assertEquals(1, sessionInfos.size());
        assertEquals(id, sessionInfos.get(0).getId());
    }

    @Test
    public void testNode() throws ConsulException {
        final String id = s.create("" + System.currentTimeMillis());
        final String node = s.info(id).get(0).getNode();

        final List<SessionData> nodeSessions = s.node(node);
        assertEquals(1, nodeSessions.size());
        assertEquals(id, nodeSessions.get(0).getId());
    }
}
