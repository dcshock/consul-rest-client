package consul;

import org.json.JSONObject;

public class AgentCheck {
    String id;
    String name;
    String notes;
    String script;
    String interval;
    String ttl;

    public AgentCheck() {

    }

    public AgentCheck(String id, String name, String notes, String script, String interval, String ttl) {
        this.id = id;
        this.name = name;
        this.notes = notes;
        this.script = script;
        this.interval = interval;
        this.ttl = ttl;
    }

    AgentCheck(JSONObject obj) {
        id = obj.optString("ID");
        name = obj.optString("Name");
        notes = obj.optString("Notes");
        script = obj.optString("Script");
        interval = obj.optString("Interval");
        ttl = obj.optString("TTL");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getTTL() {
        return ttl;
    }

    public void setTTL(String ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return "AgentCheck [id=" + id + ", name=" + name + ", notes=" + notes + ", script=" + script + ", interval=" + interval +
                        ", ttl=" + ttl + "]";
    }
}
