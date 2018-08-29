import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import consul.Consul;
import consul.ConsulException;
import consul.KeyValue;
import consul.Session;
import consul.SessionData.Behavior;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class KeyValueLockTest {
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

    @Test
    public void testAcquireAllNullOrBlank() {
        try {
            assertFalse(kv.acquireLock(null, null, null));
            assertFalse(kv.acquireLock("", "", ""));
        } catch (Exception e) {
            e.printStackTrace();
            fail("testAcquireAllNullOrBlank:" + e);
        }
    }

    @Test
    public void testAcquireNullOrBlankSession() {
        try {
            assertFalse(kv.acquireLock("key", "value", null));
            assertFalse(kv.acquireLock("key", "value", ""));
        } catch (Exception e) {
            e.printStackTrace();
            fail("testAcquireNullOrBlankSession:" + e);
        }
    }

    @Test
    public void testAcquireNullOrBlankKey() {
        try {
            String id = s.create("" + System.currentTimeMillis());
            assertNotNull(id);
            assertFalse(kv.acquireLock(null, "value", id));
            assertFalse(kv.acquireLock("", "value", id));
        } catch (Exception e) {
            e.printStackTrace();
            fail("testAcquireNullOrBlankKey:" + e);
        }
    }

    @Test
    public void testAcquireNullOrBlankValue() {
        try {
            String id = s.create("" + System.currentTimeMillis());
            assertNotNull(id);
            assertFalse(kv.acquireLock("key", null, id));
            assertFalse(kv.acquireLock("key", "", id));
        } catch (Exception e) {
            e.printStackTrace();
            fail("testAcquireNullOrBlankValue:" + e);
        }
    }

    @Test
    public void testRelease() throws Exception {
        String id = s.create("" + System.currentTimeMillis());
        assertNotNull(id);
        final String value = "value";
        assertTrue(kv.acquireLock("key", value, id));
        assertTrue(kv.releaseLock("key", value, id));
        assertEquals(value, kv.get("key"));
    }

    @Test
    public void testReleaseNullValue() throws Exception {
        final String id = s.create("" + System.currentTimeMillis());
        assertNotNull(id);
        final String value = "value";
        try {
            assertTrue(kv.acquireLock("key", value, id));
            assertFalse(kv.releaseLock("key", null, id));
            assertTrue(kv.releaseLock("key", value, id));
            assertEquals(value, kv.get("key"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("testReleaseNullValue:" + e);
        }
    }

    @Test
    public void testReleaseBlankValue() throws Exception {
        final String id = s.create("" + System.currentTimeMillis());
        assertNotNull(id);
        try {
            assertTrue(kv.acquireLock("key", "value", id));
            assertTrue(kv.releaseLock("key", "", id));
            assertEquals("", kv.get("key"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("testReleaseBlankValue:" + e);
        }
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
        assertNotNull(id);
        assertTrue(kv.acquireLock("key", "value", id));
        assertTrue(kv.releaseLock("key", "value", id));
    }

    @Test
    public void testSessionDeathDelete() throws Exception {
        final String id = s.create("" + System.currentTimeMillis(), 1, Behavior.DELETE, 0);
        assertNotNull(id);

        assertTrue(kv.acquireLock("key", "value", id));
        s.info(id).destroy();

        Thread.sleep(1000);

        // Verify that the key was cleared.
        assertEquals("", kv.get("key"));
    }

    @Test
    public void testMultiAcquisition() throws Exception {
        final String id = s.create("" + System.currentTimeMillis(), 1, Behavior.DELETE, 0);
        assertNotNull(id);
        assertTrue(kv.acquireLock("key", "value", id));
        assertTrue(kv.acquireLock("key", "value", id));
        assertTrue(kv.releaseLock("key", "value", id));
    }

    @Test
    public void testTtl() throws Exception {
        final String id = s.create("" + System.currentTimeMillis(), 1, Behavior.DELETE, 10);
        assertNotNull(id);
        assertTrue(kv.acquireLock("key", "value", id));

        String id2 = s.create("" + System.currentTimeMillis(), 1, Behavior.RELEASE, 10);
        assertNotNull(id2);
        assertTrue(kv.acquireLock("key2", "value", id2));

        // Consul doesn't guarantee a ttl to the second so we have to wait longer than 10...
        Thread.sleep(22000);

        // Verify that the key was cleared.
        assertEquals("", kv.getDetails("key").getSessionId());
        assertEquals("", kv.get("key"));
        assertEquals("", kv.getDetails("key2").getSessionId());
        assertEquals("value", kv.get("key2"));

        // Finally, the session no longer exists
        assertNull(s.info(id));
    }
}
