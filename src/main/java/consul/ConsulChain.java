package consul;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConsulChain {
    protected static ObjectMapper mapper = new ObjectMapper();
    private Consul consul;

    protected ConsulChain(Consul consul) {
        if (consul == null)
            throw new IllegalArgumentException("Consul object cannot be null");
        this.consul = consul;
    }

    /**
     * Return the consul object that is reading from the node from which the accessed object was
     * populated.
     * @return
     */
    public Consul consul() {
        return consul;
    }
}
