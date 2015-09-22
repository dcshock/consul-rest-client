consul-rest-client
==================

Java REST client for Consul.io

Welcome! Hopefully this helps someone get started using Consul from java applications. This code has been production tested at this point, but will remain beta release until all the Consul endpoints are implemented. Feel free to submit issues, or pull requests to contribute. 

Latest Version Available 0.8

## Maven Dependency
```
<dependency>
  <groupId>com.github.dcshock</groupId>
  <artifactId>consul-rest-client</artifactId>
  <version>0.8</version>
</dependency>
```

## SBT Dependency
```
"com.github.dcshock" % "consul-rest-client" % "0.8"
```

## Example Usage
Starting point of using the client is to create a new Consul object which provides an interface with a Consul agent. 

```java
// Create a new consul client for a given host/port combo. 
Consul consul = new Consul("http://localhost", 8500);
```

A Consul client provides all of the necessary methods to interact with the basic Consul REST end points. 

```java
// Access the consul catalog endpoints
consul.catalog();

// Access the consul agent endpoints
consul.agent();
```

## Registering a service
```java
// Create the consul client
final Consul consul = new Consul("http://localhost", 8500);

// Register a provider of a service with consul. 
String[] tags;
consul.agent().register(new ServiceProvider("id", "name", 8302, tags));
```

## Deregister a service
```java
consul.agent().deregister("id");
```

## Register a health check
```java
consul.agent().checkRegister(new AgentCheck("id", "checkid", "These are some notes", "/usr/local/bin/check_mem.py", "10s", "15s"));
```

## Sessions
```java
Consul consul = new Consul("http://localhost", 8500);

// Create a session - defaults to release bahavior, 15 second lock delay, and a indefinite ttl.
String sessionId = consul.session().create("name");

// Create a session with a specific setup.
// 30 second lock delay
// DELETE behavior - Keys locked by this session will be deleted after the session is destroyed.
// RELEASE behavior - Keys locked by this session will be retained after the session is destroyed, but the session lock will be removed.
// 10 second ttl
String sessionId = consul.session().create("name", 30, Behavior.DELETE, 10); 

// Destroy a session
boolean destroyed = consul.session().info(sessionId).destroy();

// Renew a session
consul.session().info(sessionId).renew();

## Accessing Key Value Storage
```java
Consul consul = new Consul("http://localhost", 8500);

KeyValue kv = consul.keyStore();

// Set a value
kv.set("key", "value");

// Get a value
kv.get("key");

// Acquire a lock with a session.
boolean acquired = kv.acquire("key", "value", "sessionId");

// Release a lock.
boolean released = kv.release("key", "value", "sessionId");
```

## Dependency Notes
Since the consul-rest-client relies on Unirest to provide REST calls you need to shutdown the Unirest thread pool when stopping a process. 

```java
consul.shutdown();
```

In instances where a framework may shutdown loaded beans during hot deploys it is necessary to restart the Unirest thread pool. 

```java
// This happens when the consul bean is first instantiated, but sometimes the Unirest rug can get pulled, and require a manual jump start. 
consul.startup();
```

# Release Notes

### 0.7
Fix invalid JSON array generation for tags.

### 0.6
Updated build dependencies for SBT users. The project didn't require Scala, and it was causing issues with prior versions if it was included.

### 0.5
Initial release of the client.
