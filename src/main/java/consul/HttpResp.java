package consul;

import org.apache.http.HttpResponse;

/**
 * A HTTP response delegate.
 */
public final class HttpResp {
    private final HttpResponse response;
    private final String body;

    public HttpResp(final HttpResponse response, final String body) {
        this.response = response;
        this.body = body;
    }

    public int getStatus() {
        return response.getStatusLine().getStatusCode();
    }

    public String getBody() {
        return this.body;
    }

    public String getFirstHeader(final String name) {
        return this.response.getFirstHeader(name).getValue();
    }
}
