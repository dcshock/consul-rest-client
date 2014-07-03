package consul;

public class ConsulChain {
    protected Consul consul;

    protected ConsulChain(Consul consul) {
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
