# Helidon Sock Shop 

This project is an implementation of the microservices based application
using [Helidon Microservices Framework](https://helidon.io/).

The application is an online store that sells, well, socks, and is based
on the canonical [SockShop Microservices Demo](https://microservices-demo.github.io)
originally written and published under Apache 2.0 license by [Weaveworks](https://go.weave.works/socks).

You can see a working demo of the original application [here](http://socks.weave.works/).

This demo still uses the original front end implementation provided by Weaveworks,
but all back end services have been re-implemented from scratch to use Helidon MP 
and showcase its features and best practices.

# Table of Contents

* [Architecture](#architecture)
* [Project Structure](#project-structure)
* [Pre-Requisites](#pre-requisites)
* [Quick Start](#quick-start)
* [Complete Application Deployment](./doc/complete-application-deployment.md#complete-application-deployment)
  * [Additional Pre-Requisites](./doc/complete-application-deployment.md#additional-pre-requisites)
  * [Install Prometheus and Grafana](./doc/complete-application-deployment.md#install-prometheus-and-grafana)
  * [Expose Application via a Load Balancer](./doc/complete-application-deployment.md#expose-application-via-a-load-balancer)
  * [Install the Jaeger Operator](./doc/complete-application-deployment.md#install-the-jaeger-operator)
  * [Access Swagger](./doc/complete-application-deployment.md#access-swagger)
  * [Cleanup](./doc/complete-application-deployment.md#cleanup)  
* [Development](./doc/development.md#development)
  * [Checking Out the Project](./doc/development.md#checking-out-the-project)
  * [Building the Code](./doc/development.md#building-the-code)
  * [Creating Container Images](./doc/development.md#creating-container-images)
  * [Running Modified Application](./doc/development.md#running-modified-application)
* [License](#license)


## Architecture

The application consists of 6 back end services (rewritten from the ground up on top of
Helidon, implementing the API that the legacy `front-end` service expects).

![Architecture Diagram](./doc/images/architecture.png)

- **[Product Catalog](./catalog)**, which provides 
REST API that allows you to search product catalog and retrieve individual product details;

- **[Shopping Cart](./carts)**, which provides 
REST API that allows you to manage customers' shopping carts;

- **[Orders](./orders)**, which provides REST API 
that allows customers to place orders;

- **[Payment](./payment)**, which provides REST API 
that allows you to process payments;

- **[Shipping](./shipping)**, which provides REST API 
that allows you to ship orders and track shipments;

- **[Users](./users)**, which provides REST API 
that allows you to manage customer information and provides registration and 
authentication functionality for the customers.

You can find more details for each service within documentation pages for individual
services, which can be accessed using the links above.

## Project Structure

Each back end service described above has its own Github repo, so it can be versioned
and released independently from other services. 

In addition to that, there is also a main 
[Sock Shop](.) repository (the one you are 
currently in), which contains Kubernetes deployment files for the whole application, 
top-level POM file which allows you to easily build the whole project and import it 
into your favorite IDE, and a _bash_ script that makes it easy to checkout and update 
all project repositories at once.

## Pre-Requisites

1. Install `helm`

    You must have at least version `v2.14.3` of `helm`. See [here](https://helm.sh/docs/intro/install/)
    for information on installing `helm` for your platform.
    
    > Note: The `helm` commands below are for helm 3.3.

1. Add the following `helm` repositories

    ```bash
    $ helm repo add stable https://charts.helm.sh/stable
    $ helm repo add coherence https://oracle.github.io/coherence-operator/charts
    $ helm repo update
    ```   

## Quick Start

Kubernetes scripts depend on Kustomize, so make sure that you have a newer 
version of `kubectl` that supports it (at least 1.14 or above).
   
The easiest way to try the demo is to use Kubernetes deployment scripts from this repo. 

If you do, you can simply run the following command from the `coherence-helidon-sockshop-sample` directory.

We create a namespace called `sockshop`.

* **Installing a Back-end**

    ```bash
    $ kubectl create namespace sockshop
    namespace/sockshop-coherence created

    $ helm install --namespace sockshop --version 3.1.5 \
                   coherence-operator coherence/coherence-operator

    $ kubectl apply -k k8s/coherence --namespace sockshop
    ```

> Note: The above helm command is for helm version 3, use the following command
> If you are using helm version 2:
> ```bash
> $ helm install coherence/coherence-operator --version 3.1.5 \
>       --namespace sockshop --name coherence-operator   
> ```

This will merge all the files under the specified directory and create all Kubernetes 
resources defined by them, such as deployment and service for each microservice.

### (Optional) Install the Original WeaveSocks Front End

> Warning: The original WeaveSocks Front End has a few bugs, as well as some security issues, 
> and it hasn't been actively maintained for a few years. However, if you want to deploy
> it nevertheless to see how it interacts with our back-end services, please follow
> the steps below.

Install the `front-end` service by running the following command:

```bash
$ kubectl apply -f k8s/optional/original-front-end.yaml --namespace sockshop
```

Port-forward to the `front-end` UI using the following

**Mac/Linux**

```bash
$ export FRONT_END_POD=$(kubectl get pods --namespace sockshop -o jsonpath='{.items[?(@.metadata.labels.app == "front-end")].metadata.name}')
$ kubectl port-forward --namespace sockshop $FRONT_END_POD 8079:8079
```

**Windows**

```bash
kubectl get pods --namespace sockshop -o jsonpath='{.items[?(@.metadata.labels.app == "front-end")].metadata.name}' > pod.txt
SET /P FRONT_END_POD=<pod.txt
kubectl port-forward --namespace sockshop %FRONT_END_POD% 8079:8079
```

> Note: If you have installed into a namespace then add the `--namespace` option to all `kubectl` commands in these instructions.

You should be able to access the home page for the application by pointing your browser to http://localhost:8079/.

You should then be able to browse product catalog, add products to shopping cart, register as a new user, place an order,
browse order history, etc.

Once you are finished, you can clean up the environment by executing the following:

```bash
$ kubectl delete -f k8s/optional/original-front-end.yaml --namespace sockshop
$ kubectl delete -k k8s/coherence --namespace sockshop
```

### Scale Back-End

If you wish to scale the back-end you can issue the following command

```bash
# Scale the orders statefulset
$ kubectl scale coherence --namespace sockshop orders --replicas=3 

# Scale all statefulsets 
$ for pod in carts catalog orders payment shipping users
    do kubectl scale coherence --namespace sockshop $pod --replicas=3
done
```

## License

The Universal Permissive License (UPL), Version 1.0
