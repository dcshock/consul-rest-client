import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import consul.Consul;
import consul.ConsulException;
import consul.KeyValue;
import consul.Session;
import consul.SessionData.Behavior;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeyValueTest {
    private static Session s;
    private static KeyValue kv;

    @BeforeClass
    public static void before() throws ConsulException {
        final Consul c = new Consul("http://localhost", 8500);
        s = c.session();
        kv = c.keyStore();

        // Cleanup any left overs from a failed test.
        kv.delete("key");
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
    public void testAcquire() throws Exception {
        String id = s.create("" + System.currentTimeMillis());
        assertNotNull(id);
        assertTrue(kv.acquireLock("key", "value", id));
        assertTrue(kv.releaseLock("key", "value", id));
        assertTrue(kv.acquireLock("key", "value", id));

        String id2 = s.create("" + System.currentTimeMillis());
        assertFalse(kv.acquireLock("key", "value", id2));

        assertTrue(kv.releaseLock("key", "value", id));
    }

    /**
     * Acquire a lock on a key, and verify that a session that is destroyed blocks the key for the session LockDelay.
     * @throws Exception
     */
    @Test
    public void testSessionDeath() throws Exception {
        String id = s.create("" + System.currentTimeMillis(), 2, Behavior.RELEASE, 0);

        assertNotNull(id);
        assertTrue(kv.acquireLock("key", "value", id));

        // When a session is destroyed the lock cannot be acquired or released during a set LockDelay in the session.
        s.info(id).destroy();
        assertFalse(kv.acquireLock("key", "value", id));
        assertFalse(kv.releaseLock("key", "value", id));

        // Wait for the lock delay time on the session.
        Thread.sleep(2000);

        // Verify that we can't release a lock that was auto released.
        assertFalse(kv.releaseLock("key", "value", id));

        // Verify that the key still exists since the behavior is to release.
        assertEquals("value", kv.get("key"));

        // Verify that a new session can create a lock.
        id = s.create("" + System.currentTimeMillis(), 2, Behavior.RELEASE, 0);
        assertTrue(kv.acquireLock("key", "value", id));
        assertTrue(kv.releaseLock("key", "value", id));
    }

    @Test
    public void testSessionDeathDelete() throws Exception {
        String id = s.create("" + System.currentTimeMillis(), 1, Behavior.DELETE, 0);
        assertTrue(kv.acquireLock("key", "value", id));
        s.info(id).destroy();

        Thread.sleep(1000);

        // Verify that the key was cleared.
        assertEquals("", kv.get("key"));
    }
}
