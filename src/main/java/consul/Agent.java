package consul;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Agent extends ConsulChain {
    Agent(Consul consul) {
        super(consul);
    }

    public Self self() throws ConsulException {
        try {
            final HttpResp resp = Http.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "self");
            final JsonNode checked = checkResponse(resp);
            final JsonNode member = checked.get("Member");
            return new Self(
                member.get("Addr").asText(),
                member.get("Port").asInt(),
                member.get("Name").asText()
            );
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    /**
     * Returns a list of all services offered.
     * @throws ConsulException
     */
    public List<ServiceProvider> services() throws ConsulException {
        try {
            final Self self = self();
            final List<ServiceProvider> providers = new ArrayList<>();
            final HttpResp resp = Http.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "services");
            final JsonNode obj = checkResponse(resp);
            for (final Iterator<String> itr = obj.fieldNames(); itr.hasNext(); ) {
                final JsonNode service = obj.get(itr.next());
                final ServiceProvider provider = new ServiceProvider();
                provider.setId(service.get("ID").asText());
                provider.setName(service.get("Service").asText());
                provider.setPort(service.get("Port").asInt());
                // Map tags
                String[] tags = null;
                if (service.has("Tags") && service.get("Tags").isArray()) {
                    final ArrayNode arr = (ArrayNode)service.get("Tags");
                    tags = new String[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        tags[i] = arr.get(i).asText();
                    }
                }
                provider.setTags(tags);
                provider.setAddress(self.getAddress());
                provider.setNode(self.getNode());
                providers.add(provider);
            }
            return providers;
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public String register(ServiceProvider provider) throws ConsulException {
        final Set<String> tags = new TreeSet<>();
        if (provider.getTags() != null) {
            for (String tag : provider.getTags()) {
                tags.add(tag);
            }
        }
        final Map<String, Object> service = new HashMap<>();
        service.put("ID", provider.getId());
        service.put("Name", provider.getName());
        service.put("Port", provider.getPort());
        if (tags.size() > 0) {
            service.put("Tags", tags);
        }
        final HttpResp resp;
        try {
            resp = Http.put(
                consul().getUrl() + EndpointCategory.Agent.getUri() + "service/register",
                mapper.writeValueAsString(service)
            );
        } catch (IOException e) {
            throw new ConsulException(e);
        }
        return resp.getBody();
    }

    public void deregister(String serviceId) throws ConsulException {
        try {
            Http.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "service/deregister/" + serviceId);
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public String getChecks() throws ConsulException {
        final HttpResp resp;
        try {
            resp = Http.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "checks");
        } catch (IOException e) {
            throw new ConsulException(e);
        }
        return resp.getBody();
    }

    public String checkRegister(AgentCheck check) throws ConsulException {
        final Map<String, Object> agentCheck = new HashMap<>();
        agentCheck.put("ID", check.getId());
        agentCheck.put("Name", check.getName());
        agentCheck.put("Notes", check.getNotes());
        agentCheck.put("Script", check.getScript());
        agentCheck.put("Interval", check.getInterval());
        agentCheck.put("TTL", check.getTTL());
        HttpResp resp;
        try {
            resp = Http.put(
                consul().getUrl() + EndpointCategory.Agent.getUri() + "check/register",
                mapper.writeValueAsString(agentCheck)
            );
        } catch (IOException e) {
            throw new ConsulException(e);
        }
        return resp.getBody();
    }

    public void checkDeregister(String checkId) throws ConsulException {
        try {
            Http.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "check/deregister/" + checkId);
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public void checkPass(String checkId) throws ConsulException {
        try {
            Http.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "check/pass/" + checkId);
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public void checkWarn(String checkId) throws ConsulException {
        try {
            Http.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "check/warn/" + checkId);
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }

    public void checkFail(String checkId) throws ConsulException {
        try {
            Http.get(consul().getUrl() + EndpointCategory.Agent.getUri() + "check/fail/" + checkId);
        } catch (IOException e) {
            throw new ConsulException(e);
        }
    }
}
