## Alternate Kubernetes Deployments

The Sock Shop services can be deployed in a number of different topologies.
In a Coherence application a JVM can be a cluster member or a client that uses either Coherence Extend (TCP) or gRPC.
In this demo the default deployment creates on big Coherence cluster where all the JVMs are storage enabled (that means
they all store a sub-set of cache data).

A few of the possible deployments are:

- All microservices are part of one single Coherence cluster and are all storage enabled (this is the default used in this demo)
- All microservices are part of one single Coherence cluster but are storage disabled. A separate set of JVMs is used to store Coherence cache data.
- Each microservice is a separate Coherence cluster, each JVM in the microservice is also storage enabled to store cache data for that service
- All microservices are part of one single Coherence cluster and are storage disabled. A separate set of JVMs are used to store Coherence cache data.
    

### Single Coherence Cluster with Isolated Cache Services
                            
Although the default Sock Shop deployment is a single Coherence Cluster, the caches for each microservice are 
isolated from each other. In fact, the caches for a specific microservice are only present in the Pods for that 
service. This is done by making sure that the caches in each microservice map to a Coherence cache service with a 
name unique to that service.

One advantage of this type of deployment is that all the services caches are still isolated, but they appear as 
a single Coherence cluster for things like monitoring. All the Coherence metrics scraped from all the services
will have the same cluster name. It is then easy to see them all together in monitoring applications such as Prometheus,
and also see all the data together on a single set of Grafana dashboards.

In Coherence, all caches are managed by a cache service and each Cache Service has a name, configured in the 
Coherence cache configuration file. When Coherence starts, the cache configuration file is loaded and the services 
are started. Most applications just use the same, or similar configuration in all the cluster members, so all member
will be running the same cache services. But, this is not mandatory, it is possible to have different cache configurations
in different cluster members and hence each JVM in the cluster can run a different set of cache services.

In the Sock Shop demo, each microservice has a single Coherence cache service, but each has a unique name. 
There are six microservices in the demo, so in the Coherence cluster there will be six cache services with names
`Carts`, `Catalog`, `Orders`, `Payment`, `Shipping` and `Users`. 
But, each microservice can only see its own cache service and hence only see its own caches. For example, 
the Carts microservice Pods so not run any of the other five cache services. 

You might wonder how this is done, when the Sock Shop microservices all use the default cache configuration file that is
built into the `coherence.jar`. How can they all have a different service name? 
In a Coherence configuration file, it is possible to use macros to replace values in the file with properties. 
This can be system properties, or environment variables, or in the case of Helidon CDI, they can be Helidon configuration
properties from the `src/main/resources/META-INF/microprofile-config.properties` file.

The definition of the distributed cache service in the Coherence default cache configuration file looks like this:

```xml
<distributed-scheme>
  <scheme-name>server</scheme-name>
  <service-name>${coherence.service.name PartitionedCache}</service-name>
  <local-storage system-property="coherence.distributed.localstorage">true</local-storage>
  <partition-count system-property="coherence.distributed.partitions">257</partition-count>
  <backing-map-scheme>
    <local-scheme>
      <high-units>{back-limit-bytes 0B}</high-units>
    </local-scheme>
  </backing-map-scheme>
  <autostart>true</autostart>
</distributed-scheme>
```

You can see that the value of the `<service-name>` element is a macro `${coherence.service.name PartitionedCache}`.
This means that the value for the `<service-name>` element will be set from the value of the
`coherence.service.name`system property, or `COHERENCE_SERVICE_NAME` environment variable, or the
`coherence.service.name` property in `src/main/resources/META-INF/microprofile-config.properties`. 
If none of those properties are set, the default service name will be `PartitionedCache`.

Each of the microservices in the Sock Shop demo has a `src/main/resources/META-INF/microprofile-config.properties`
file, that sets a unique value for the `coherence.service.name` property name and hence each microservice runs
a unique cache service.

### Distinct Microservices

In this topology, each microservice is a completely separate Coherence cluster.
All the yaml files are identical to those under the default Sock Shop deployment, but they have the `cluster` field removed.

For example, the first part of the `coherence/carts/app.yaml` looks like the snippet shown below where the Coherence 
cluster name is set to `SockShop` using the field `cluster: SockShop`. This same `cluster` field is in all the 
default yaml files, so although each microservice has its own yaml file and is a separate `Coherence` resource in
Kubernetes, they all form a single Coherence cluster named `SockShop`.

```yaml
apiVersion: coherence.oracle.com/v1
kind: Coherence
metadata:
  name: carts
spec:
  cluster: SockShop
  role: Carts
  replicas: 1
```
                   
We can remove the `cluster` field, as shown in the [Carts yaml file](./distinct-services/carts.yaml) 

```yaml
apiVersion: coherence.oracle.com/v1
kind: Coherence
metadata:
  name: carts
spec:
  role: Carts
  replicas: 1
```

When no `cluster` field is set, the Coherence Operator will set the Coherence cluster name to be the same as 
the `metadata.name` field in the yaml. In this case the cluster name will be `carts`. After doing the same for 
all the other yaml files, we will end up with six separate Coherence clusters, carts, catalog, orders, payment, 
shipping and users. 

### Distinct Microservices with Separate Storage

This topology is an extension of the "Distinct Microservices" topology. Each microservice is still a completely 
separate Coherence cluster, but now the microservice has two tiers. There is the service tier and then there is 
the storage tier. 

**Advantages**
* There is a separation of concerns so a heavily loaded web server will not put so much impact on Coherence storage performance, and vice-versa Coherence requests will not impact the web tier.
* Both tiers can be scaled, and updated independently

**Disadvantages**
* A more complex deployment, each service is now two yaml files and two tiers to manage.

For this type of deployment, each microservice will require two `Coherence` resources creating, one for the
microservice itself, and one for the Coherence storage enabled Pods. We will use the Carts service as an example.
  
This is the yaml for the Carts microservice:

```yaml
apiVersion: coherence.oracle.com/v1
kind: Coherence
metadata:
  name: carts
spec:
  role: Carts
  replicas: 1
  image: ghcr.io/oracle/coherence-helidon-sockshop-carts:latest
  readinessProbe:
    initialDelaySeconds: 10
    periodSeconds: 10
  env:
    - name: TRACING_HOST
      value: "jaeger-collector"
    - name: JAEGER_SAMPLER_TYPE
      value: "const"
    - name: JAEGER_SAMPLER_PARAM
      value: "1"
  application:
    type: helidon
  jvm:
    memory:
      heapSize: 2g
    args:
      - "-Dhelidon.serialFilter.ignoreFiles=true"
      - "-Dhelidon.serialFilter.pattern=*"
      - "-Dhelidon.serialFilter.failure.action=WARN"
  coherence:
    storageEnabled: false
    metrics:
      enabled: true
  ports:
    - name: http
      port: 7001
      service:
        name: carts
        port: 80
      serviceMonitor:
        enabled: true
    - name: metrics
      serviceMonitor:
        enabled: true

```

The yaml above is identical to the `carts.yaml` yaml from the "Distinct Microservices" but Coherence is configured
to be storage disabled using this yaml:

```yaml
  coherence:
    storageEnabled: false
```

The yaml for the Carts storage enabled tier is shown below. 
It is very similar to the Carts microservice yaml, and uses the same image name.
The `metadata.name` field for each resource of the same kind in Kubernetes has to be unique. 
We just add the suffix "-storage" to the microservice name to give the storage tier name, in this case `carts-storage`.

```yaml
#
# Copyright (c) 2020, 2024, Oracle and/or its affiliates.
#
# Licensed under the Universal Permissive License v 1.0 as shown at
# https://oss.oracle.com/licenses/upl.
#
apiVersion: coherence.oracle.com/v1
kind: Coherence
metadata:
  name: carts-storage
spec:
  role: CartsStorage
  replicas: 1
  image: ghcr.io/oracle/coherence-helidon-sockshop-carts:latest
  readinessProbe:
    initialDelaySeconds: 10
    periodSeconds: 10
  env:
    - name: TRACING_HOST
      value: "jaeger-collector"
    - name: JAEGER_SAMPLER_TYPE
      value: "const"
    - name: JAEGER_SAMPLER_PARAM
      value: "1"
  jvm:
    memory:
      heapSize: 2g
    args:
      - "-Dhelidon.serialFilter.ignoreFiles=true"
      - "-Dhelidon.serialFilter.pattern=*"
      - "-Dhelidon.serialFilter.failure.action=WARN"
  coherence:
    storageEnabled: true
    management:
      enabled: true
    metrics:
      enabled: true
  ports:
    - name: management
    - name: metrics
      serviceMonitor:
        enabled: true
```

The storage enabled tier must obviously be storage enabled, and we also enabled Coherence 
REST management and metrics:

```yaml
  coherence:
    storageEnabled: true
    management:
      enabled: true
    metrics:
      enabled: true
```

The next difference is that the storage tier does not need to be a Helidon CDI application, it can just run
Coherence. This is not mandatory, there are use cases where the storage tier can use things like CDI.
In this case though we do not want the storage tier to be running the Helidon webserver. The `application`
field has been removed:

```yaml
  application:
    type: helidon
```

Finally, the storage enabled tier also does not expose the `http` port because it is not running a Helidon webserver
so only the Coherence management and metrics ports are exposed: 
                           
```yaml
  ports:
    - name: management
    - name: metrics
      serviceMonitor:
        enabled: true
```

#### Deployment Order

When using a separate storage tier order of deployment might become important.
For example, if the Carts microservice is deployed before the Carts Storage is deployed the microservice might
start serving web requests or some other processing that requires it to access a Coherence cache, but the
cache will not be available yet as there is no storage. In this case the storage tier must be deployed and ready before 
the microservice tier is deployed. 