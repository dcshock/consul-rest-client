package consul;

public class ConsulException extends Exception {
    private static final long serialVersionUID = 4250618907481541722L;

    public ConsulException(String s) {
        super(s);
    }

    public ConsulException(Exception e) {
        super(e);
    }

    public ConsulException(String msg, Exception e) {
        super(msg, e);
    }
}
