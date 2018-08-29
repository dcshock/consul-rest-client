import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
 
import org.junit.Test;

import java.util.Optional;

public class SerializeTest {
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testNonNullValue() throws Exception {
        final String keyValue = "key";
        final String jsonText = "{ \"key\":\"" + keyValue + "\"}";
        final JsonNode node = mapper.readTree(jsonText.getBytes());
        final String key = Optional.ofNullable(node.get("key")).map(JsonNode::asText).orElse("");
        assertEquals("key !match", keyValue, key);
    }

    @Test
    public void testNoValue() throws Exception {
        final String jsonText = "{}";
        final JsonNode node = mapper.readTree(jsonText.getBytes());
        final String key = Optional.ofNullable(node.get("key")).map(JsonNode::asText).orElse("");
        assertEquals("key !blank", "", key);
    }

    @Test
    public void testNullValue() throws Exception {
        final String jsonText = "{ \"key\":null}";
        final JsonNode node = mapper.readTree(jsonText.getBytes());
        final String key = Optional.ofNullable(node.get("key")).map(n -> n.asText("")).orElse("");
        assertEquals("key !blank", "", key);
    }
}
