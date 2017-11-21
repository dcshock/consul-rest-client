package consul;

import consul.SessionData.Behavior;

import java.io.IOException;
import java.util.List;

public class Session extends ConsulChain {
    // This is a consul limitation
    public static final int MIN_TTL_SEC = 10;

    Session(Consul consul) {
        super(consul);
    }

    public String create(String name) throws ConsulException {
        return create(name, 15, Behavior.RELEASE, 0);
    }

    public String create(String name, int lockDelay, Behavior behavior, int ttl) throws ConsulException {
        if (name == null || name.trim().length() == 0 || lockDelay < 0 || (ttl < MIN_TTL_SEC && ttl != 0))
            return null;

        try {
            final String createStr = mapper.writeValueAsString(new SessionData()
                .setName(name)
                .setLockDelay(lockDelay + "s")
                .setChecks("serfHealth")
                .setBehavior(behavior)
                .setTtl(ttl + "s"));

            final HttpResp resp = Http.put(consul().getUrl() + EndpointCategory.Session.getUri() + "create", createStr);
            if (resp.getStatus() != Http.OK) {
                throw new ConsulException("Unable to create session: " + resp.getStatus());
            }
            return mapper.readValue(resp.getBody(), SessionData.class).getId();
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public boolean destroy(SessionData session) throws ConsulException {
        // Give garbage, get garbage
        if (session == null) {
            return false;
        }
        return destroy(session.getId());
    }

    public boolean destroy(String sessionId) throws ConsulException {
        // Give garbage, get garbage
        if (sessionId == null) {
            return false;
        }
        try {
            final HttpResp resp = Http.put(consul().getUrl() + EndpointCategory.Session.getUri() + "destroy/" + sessionId);
            return resp.getStatus() == Http.OK;
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public List<SessionData> renew(SessionData session) throws ConsulException {
        // Give garbage, get garbage
        if (session == null)
            return null;

        return renew(session.getId());
    }

    public List<SessionData> renew(String sessionId) throws ConsulException {
        // Give garbage, get garbage
        if (sessionId == null) {
            return null;
        }
        try {
            final HttpResp resp = Http.put(consul().getUrl() + EndpointCategory.Session.getUri() + "renew/" + sessionId);
            if (resp.getStatus() != Http.OK) {
                throw new ConsulException("Session lookup failed with status: " + resp.getStatus());
            }
            return tieSelf(mapper.readValue(resp.getBody(), mapper.getTypeFactory().constructCollectionType(List.class, SessionData.class)));
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public SessionData info(String uuid) throws ConsulException {
        // Give garbage, get garbage
        // consul expects a UUID
        try {
            java.util.UUID.fromString(uuid);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
        try {
            final HttpResp resp = Http.get(consul().getUrl() + EndpointCategory.Session.getUri() + "info/" + uuid);
            if (resp.getStatus() != Http.OK) {
                throw new ConsulException("Session lookup failed");
            }
            final List<SessionData> sessions =
                tieSelf(mapper.readValue(resp.getBody(), mapper.getTypeFactory().constructCollectionType(List.class, SessionData.class)));
            if (sessions == null || sessions.size() == 0) {
                return null;
            }
            return sessions.get(0);
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public List<SessionData> node(String node) throws ConsulException {
        // Give garbage, get garbage
        if (node == null || node.trim().length() == 0) {
            return null;
        }
        try {
            final HttpResp resp = Http.get(consul().getUrl() + EndpointCategory.Session.getUri() + "node/" + node);
            if (resp.getStatus() != Http.OK) {
                throw new ConsulException("Session lookup failed");
            }
            return tieSelf(mapper.readValue(resp.getBody(), mapper.getTypeFactory().constructCollectionType(List.class, SessionData.class)));
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public List<SessionData> all() throws ConsulException {
        try {
            final HttpResp resp = Http.get(consul().getUrl() + EndpointCategory.Session.getUri() + "list");
            return tieSelf(mapper.readValue(resp.getBody(), mapper.getTypeFactory().constructCollectionType(List.class, SessionData.class)));
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    private List<SessionData> tieSelf(List<SessionData> sessions) {
        if (sessions != null)
            sessions.forEach((session) -> session.setSessionHandler(this));
        return sessions;
    }
}
