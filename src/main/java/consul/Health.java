package consul;

public class Health extends ConsulChain {
    public Health(Consul c) {
        super(c);
    }

    public HealthService service() {
        return new HealthService(consul());
    }
}
