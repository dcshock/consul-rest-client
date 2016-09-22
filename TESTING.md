consul-rest-client testing notes
==================

You can fire up consul in docker and bring children up or down to force consul to return 500 errors.

### Docker Compose Command 
``` 
docker-compose up -d consul && docker-compose scale consul-children=2 && docker-compose logs
```

### Sample docker-compose.yml 
```
version: '2'
services:
  ####################################################
  #             Dependent Services
  ####################################################
  consul:
    image: gliderlabs/consul-server
    container_name: consul
    hostname: "consul"
    #network_mode: "host"
    ports:
      - "8300:8300"
      - "8400:8400"
      - "8500:8500"
      - "8600:8600"
      - "8600:8600/udp"
    environment:
      - SERVICE_IGNORE=true
    command: -server -bootstrap-expect 2 --data-dir=/tmp/consul

  consul-children:
    image: gliderlabs/consul-server
    #container_name: consul2
    #network_mode: "host"
    expose:
      - "8400"
      - "8500"
      - "8600"
    environment:
      - SERVICE_IGNORE=true
    command: -server --data-dir=/tmp/consul -join consul
```
