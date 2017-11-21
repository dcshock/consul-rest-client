package consul;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

/**
 * Fluent HTTP client helper.
 */
public final class Http {

    // Hide the implementation of apache fluent status codes
    public static int OK = HttpStatus.SC_OK;
    public static int NOT_FOUND = HttpStatus.SC_NOT_FOUND;
    public static int INTERNAL_SERVER_ERROR = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    static Executor executor;

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
        cm.setMaxTotal(100);
        cm.setDefaultMaxPerRoute(100);
        executor = Executor.newInstance(client);
    }

    // Disabled constructor
    private Http() {
    }

    /**
     * Process and capture HTTP response.
     */
    private static HttpResp toHttpResp(final HttpResponse response) throws IOException {
        final HttpEntity entity = response.getEntity();
        final String body = (entity == null) ? "" : EntityUtils.toString(entity);
        return new HttpResp(response, body);
    }

    /**
     * Issue a GET to the given URL.
     */
    public static HttpResp get(final String url) throws IOException {
        return Http.toHttpResp(executor.execute(Request.Get(url))
                .returnResponse());
    }

    /**
     * Issue a DELETE to the given URL.
     */
    public static HttpResp delete(final String url) throws IOException {
        return Http.toHttpResp(executor.execute(Request.Delete(url))
                .returnResponse());
    }

    /**
     * Issue a PUT to the given URL with a JSON body.
     */
    public static HttpResp put(final String url, final String body) throws IOException {
        return Http.toHttpResp(executor.execute(Request.Put(url)
                .bodyString(body, ContentType.APPLICATION_JSON))
                .returnResponse()
        );
    }

    /**
     * Issue a PUT to the given URL without a body.
     */
    public static HttpResp put(final String url) throws IOException {
        return Http.toHttpResp(executor.execute(Request.Put(url))
                .returnResponse()
        );
    }
}
