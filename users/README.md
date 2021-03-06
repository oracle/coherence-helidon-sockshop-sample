# Users Service 

The Users Service covers user account storage, provide user login, register and
retrieval for user, cards and addresses.

It contains service implementation, including
[domain model](src/main/java/com/oracle/coherence/examples/sockshop/helidon/users/User.java),
[REST API](src/main/java/com/oracle/coherence/examples/sockshop/helidon/users/UserResource.java),
as well as the [data repository abstraction](src/main/java/com/oracle/coherence/examples/sockshop/helidon/users/UserRepository.java)
and its [Coherence](https://coherence.java.net/) [implementation](src/main/java/com/oracle/coherence/examples/sockshop/helidon/users/CoherenceUserRepository.java).

## API

The service exposes REST API on port 7001. 

TBD (add OpenAPI support/link)

## Building the service

In order to build project and create container images for the service, simply run the 
following commands:

```bash
$ mvn clean install
$ mvn package -Pcontainer -DskipTests
``` 

The first command will build project, run unit and integration tests, and install the
artifacts that need to be included into the container images into the local Maven repo.

The second command will then package those artifacts, and all of their dependencies, into
the local container image.

You can then manually push generated image to a container repository of your choice in order
to make it available to other environments.

Alternatively, you can build and push the image directly to a remote container repository by
running the following command instead:

```bash
$ mvn package -Pcontainer -DskipTests -Dcontainer.repo=<your_container_repo> -Djib.goal=build
```

You should replace `<your_container_repo>` in the command above with the name of the 
container repository that you can push images to.

## Running the service

Coherence is embedded into your application and runs as part
of your application container so it allows you to easily scale your service to hundreds of **stateful**,
and optionally **persistent** nodes.

To run Coherence implementation of the service, simply execute

```bash
$ docker run -p 7001:7001 ghcr.io/coherence-helidon-sockshop-sample/users
``` 

As a basic test, you should be able to perform an HTTP GET against `/customers` endpoint:

```bash
$ curl http://localhost:7001/customers
``` 
which should return 200 response code and a list of customers.


To learn how to run the service in Kubernetes, as part of a larger Sock Shop application,
please refer to the [main documentation page](../README.md).

## License

The Universal Permissive License (UPL), Version 1.0
