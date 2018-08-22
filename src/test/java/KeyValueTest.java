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
    public void testSetGet() throws Exception {
        final String key = "key";
        final String value = "value";
        assertTrue(kv.set(key, value));
        assertEquals(value, kv.get(key));
    }

    @Test
    public void testSetBlankKey() throws Exception {
        final String key = "";
        final String value = "value";
        assertFalse(kv.set(key, value));
    }

    @Test
    public void testSetNullKey() throws Exception {
        final String key = null;
        final String value = "value";
        assertFalse(kv.set(key, value));
    }

    @Test
    public void testSetBlankValue() throws Exception {
        final String key = "key";
        final String value = "";
        assertTrue(kv.set(key, value));
        assertEquals(value, kv.get(key));
    }

    @Test
    public void testSetNullValue() throws Exception {
        final String key = "key";
        final String value = null;
        assertFalse(kv.set(key, value));
    }

    @Test
    public void testChangeToBlankValue() throws Exception {
        final String key = "key";
        final String value = "value";
        assertTrue(kv.set(key, value));
        assertEquals(value, kv.get(key));

        assertTrue(kv.set(key, ""));
        assertEquals("", kv.get(key));
    }

    @Test
    public void testChangeToNullValue() throws Exception {
        final String key = "key";
        final String value = "value";
        assertTrue(kv.set(key, value));
        assertEquals(value, kv.get(key));

        assertFalse(kv.set(key, null));
        assertEquals(value, kv.get(key));
    }
}
