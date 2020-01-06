package consul;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RolloverTest {
    @Test
    public void testInteger() throws ConsulException {
        JsonNode node = ConsulChain.parseJson("{\"ModifyIndex\": 2}");
        KV kv = new KV(node);
        assertEquals(Long.toString(2), kv.modifyIndex);
    }

    @Test
    public void testLong() throws ConsulException {
        JsonNode node = ConsulChain.parseJson("{\"ModifyIndex\": 4294967294}");
        KV kv = new KV(node);
        assertEquals(Long.toString(4294967294L), kv.modifyIndex);
    }
}
