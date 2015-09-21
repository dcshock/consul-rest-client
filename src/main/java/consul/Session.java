package consul;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.IOException;
import java.util.List;

public class Session extends ConsulChain {
    private static ObjectMapper mapper = new ObjectMapper();

    Session(Consul consul) {
        super(consul);
    }

    public String create(String name) throws ConsulException {
        try {
            final String createStr = mapper.writeValueAsString(new SessionData()
                .setName(name)
                .setLockDelay("15s")
                .setChecks("serfHealth")
                .setBehavior(SessionData.Behavior.RELEASE)
                .setTtl("0s"));

            final HttpResponse<String> resp = Unirest.put(consul().getUrl() + EndpointCategory.Session.getUri() + "create")
                .body(createStr)
                .asString();

            if (resp.getStatus() != 200)
                throw new ConsulException("Unable to create session");

            return mapper.readValue(resp.getBody(), SessionData.class).getId();
        } catch (UnirestException | IOException e) {
            throw new ConsulException(e);
        }
    }

    public boolean destroy(SessionData session) throws ConsulException {
        try {
            final HttpResponse<String> resp = Unirest.put(consul().getUrl() + EndpointCategory.Session.getUri() + "destroy/" + session.getId())
                .asString();

            return resp.getStatus() == 200;
        } catch (UnirestException e) {
            throw new ConsulException(e);
        }
    }

    public List<SessionData> info(String id) throws ConsulException {
        try {
            final HttpResponse<String> resp = Unirest.get(consul().getUrl() + EndpointCategory.Session.getUri() + "info/" + id)
                .asString();

            if (resp.getStatus() != 200)
                throw new ConsulException("Session lookup failed");

            return tieSelf(mapper.readValue(resp.getBody(), mapper.getTypeFactory().constructCollectionType(List.class, SessionData.class)));
        } catch (UnirestException | IOException e) {
            throw new ConsulException(e);
        }
    }

    public List<SessionData> node(String node) throws ConsulException {
        try {
            final HttpResponse<String> resp = Unirest.get(consul().getUrl() + EndpointCategory.Session.getUri() + "node/" + node)
                .asString();

            if (resp.getStatus() != 200)
                throw new ConsulException("Session lookup failed");

            return tieSelf(mapper.readValue(resp.getBody(), mapper.getTypeFactory().constructCollectionType(List.class, SessionData.class)));
        } catch (UnirestException | IOException e) {
            throw new ConsulException(e);
        }
    }

    public List<SessionData> all() throws ConsulException {
        try {
            final HttpResponse<String> resp = Unirest.get(consul().getUrl() + EndpointCategory.Session.getUri() + "list")
                .asString();

            return tieSelf(mapper.readValue(resp.getBody(), mapper.getTypeFactory().constructCollectionType(List.class, SessionData.class)));
        } catch (UnirestException | IOException e) {
            throw new ConsulException(e);
        }
    }

    private List<SessionData> tieSelf(List<SessionData> sessions) {
        if (sessions != null)
            sessions.forEach((session) -> session.setSessionHandler(this));
        return sessions;
    }
}
