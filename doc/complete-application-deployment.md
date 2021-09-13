## Complete Application Deployment

* [Additional Pre-Requisites](#additional-pre-requisites)
* [Install Prometheus and Grafana](#install-prometheus-and-grafana)
* [Expose Application via a Load Balancer](#expose-application-via-a-load-balancer)
* [Install the Jaeger Operator](#install-the-jaeger-operator)
* [Access Swagger](#access-swagger)
* [Cleanup](#cleanup)  

The Quick Start shows how you can run the application locally, but that may not
be enough if you want to experiment by scaling individual services, look at tracing data in Jaeger,
monitor services via Prometheus and Grafana, or make API calls directly via Swagger UI.

To do all of the above, you need to deploy the services into a managed Kubernetes cluster
in the cloud, by following the same set of steps described above (except for port forwarding,
which is not necessary), and performing a few additional steps.

### Additional Pre-Requisites

1. Install `envsubst`

   You must download and install `envsubst` for your platform from
   [https://github.com/a8m/envsubst](https://github.com/a8m/envsubst) and make it
   available in your `PATH`.

### Install Prometheus and Grafana

   Install the Prometheus Operator, as documented in the Prometheus Operator [Quick Start](https://prometheus-operator.dev/docs/prologue/quick-start/) page.

   Prometheus can then be accessed as documented in the
[Access Prometheus section of the Quick Start](https://prometheus-operator.dev/docs/prologue/quick-start/#access-prometheus) page.

   **IMPORTANT**
   If installing Prometheus into a RBAC enabled k8s cluster you may need to create the required RBAC resources
as described in the [Prometheus RBAC](https://prometheus-operator.dev/docs/operator/rbac/) documentation.


   **IMPORTANT**

  If you installed the Sock Shop back-end into K8s before you installed Prometheus Operator, you must
  run the following to delete and re-add the deployments for Prometheus to pick up the Pods. This is because the Coherence Operator will not have been able to create Prometheus `ServiceMonitor` resources before Prometheus was installed.

   ```bash 
   $ kubectl delete -k k8s/coherence --namespace sockshop

   $ kubectl apply -k k8s/coherence --namespace sockshop
   ```

#### Import the Grafana Dashboards

   A set of Grafana dashboards can be downloaded from the Coherence Operator GitHub repository and imported into Grafana.
   Full instructions for importing dashboards into Grafana can be found in the
   [Coherence Operator documentation](https://oracle.github.io/coherence-operator/docs/latest/#/metrics/030_importing).

   There is an additional Sock Shop specific Grafana dashboard located in the project source  `k8s/optional/grafana-dashboards/sockshop-dashboard.json` This file can be manually imported into Grafana using the instructions in the [Add a Datasource](https://grafana.com/docs/grafana/latest/datasources/add-a-data-source/) section of the Grafana documentation.

### Expose Application via a Load Balancer

> Note: This is assuming you have deployed one of the back-ends via the instructions in
> the previous section.

1. Create the Load Balancer

    ```bash
    $ kubectl apply -f k8s/optional/ingress-controller.yaml 

    $ kubectl get services --namespace ingress-nginx
    NAME            TYPE           CLUSTER-IP       EXTERNAL-IP       PORT(S)                      AGE
    ingress-nginx   LoadBalancer   AAA.BBB.CCC.DDD   WWW.XXX.YYY.ZZZ  80:31475/TCP,443:30578/TCP   17s
    ```

   Once you have been assigned an external IP address, continue to the next step.

1. Setup Domains

   You must have access to a top level domain for which you can create sub-domains to
   allow access to the application via a Load Balancer (LB).

   For example if your top level domain is `mycompany.com` then you
   should create a single wildcard DNS entry `*.sockshop.mycompany.com` to
   point to your external load balancer IP address.

1. Create the ingress

   Each time you use a different back-end you will need to create a new ingress.

   In your terminal, export (or SET for Windows) your top level domain
   and the backend you are using.

   For example for domain `sockshop.mycompany.com` use the following

    ```bash
    $ export SOCKSHOP_DOMAIN=sockshop.mycompany.com

    $ envsubst -i k8s/optional/ingress.yaml | kubectl apply --namespace sockshop -f -

    $ kubectl get ingress --namespace sockshop

    NAME               HOSTS                                                                                            ADDRESS           PORTS   AGE
    mp-ingress         mp.core.sockshop.mycompany.com                                                                   XXX.XXX.XXX.XXX   80      12d
    sockshop-ingress   core.sockshop.mycompany.com,jaeger.core.sockshop.mycompany.com,api.core.sockshop.mycompany.com   XXX.XXX.XXX.XXX   80      12d
    ```

1. Install a Service Monitor

   > Note: This is only required for non `coherence` backends.

   Each time you use a different back-end you will need to create a new service monitor.

    ```bash
    $ envsubst -i k8s/optional/prometheus-service-monitor.yaml | kubectl create --namespace monitoring -f -
    ```

1. Create the ingress for Grafana and Prometheus

   Ensuring you have the `SOCKSHOP_DOMAIN` environment variable set and issue the following:

    ```bash
    $ envsubst -i k8s/optional/ingress-grafana.yaml | kubectl apply --namespace monitoring -f - 

    $ kubectl get ingress --namespace monitoring

    NAME              HOSTS                                                             ADDRESS          PORTS   AGE
    grafana-ingress   grafana.sockshop.mycompany.com,prometheus.sockshop.mycompany.com  XXX.YYY.XXX.YYY  80      12s
    ```

   The following URLs can be used to access Grafana and Prometheus:
    * http://grafana.sockshop.mycompany.com/ - username: `admin`, initial password `prom-operator`
    * http://prometheus.sockshop.mycompany.com/

1. Access the application

   Access the application via the endpoint http://core.sockshop.mycompany.com/

### Install the Jaeger Operator

1. Install the Jaeger Operator

   The command below will create `monitoring` namespace and install Jaeger Operator into it.
   You only need to do this once, regardless of the number of backends you want to deploy.

    ```bash
    $ kubectl create -f k8s/optional/jaeger-operator.yaml
    ```

1. Deploy All-in-One Jaeger Instance

    ```bash
    $ kubectl create -f k8s/optional/jaeger.yaml --namespace sockshop
    ```

1. Exercise the Application and access Jaeger

   Accessing the Jaeger UI at http://jaeger.core.sockshop.mycompany.com/,
   you should see the trace information similar to the images below, allowing you
   to see how long each individual operation in the call tree took.

   ![Jaeger Search](./images/jaeger-search.png)

   ![Jaeger Detail](./images/jaeger-detail.png)

### Access Swagger

1. Deploy Swagger UI

    ```bash
    $ kubectl create -f k8s/optional/swagger.yaml --namespace sockshop
    ```

   Access the Swagger UI at http://mp.core.sockshop.mycompany.com/swagger/.

   Enter /carts/openapi into the Explore field at the top of the screen and click on Explore button. 
   You should see the screen similar to the following, showing you all the endpoints for the Carts 
   service (and their payloads), and allowing you to make API requests to it directly from your browser.

   ![Swagger UI](./images/swagger.png)

### Cleanup

1. Cleanup the ingress for applications

   To cleanup the ingress for your deployment, execute the following:

    ```bash 
    $ export SOCKSHOP_DOMAIN=sockshop.mycompany.com

    $ envsubst -i k8s/optional/ingress.yaml| kubectl delete -f - --namespace sockshop
    ```

2. Cleanup the ingress for Grafana and Prometheus

   If you installed Prometheus Operator, execute the following:

    ```bash
    $ envsubst -i k8s/optional/ingress-grafana.yaml | kubectl delete --namespace monitoring -f -
    ```

3. Remove the deployed services

   To cleanup the deployed services, execute the following:

    ```bash
    $ export SOCKSHOP_DOMAIN=sockshop.mycompany.com
    $ kubectl delete -k k8s/coherence --namespace sockshop
    ```

4. Remove the Load Balancer

   If you wish to remove your load balancer, execute the following:

    ```bash
    $ kubectl delete -f k8s/optional/ingress-controller.yaml
    ```

5. Remove Jaeger

    ```bash
    $ kubectl delete -f k8s/optional/jaeger-operator.yaml 
    ```

   Execute the following:

    ```bash
    $ kubectl delete -f k8s/optional/jaeger.yaml --namespace sockshop
    ```

6. Remove Swagger

   Execute the following:

    ```bash
   $ kubectl delete -f k8s/optional/swagger.yaml --namespace sockshop
    ```

7. Remove Prometheus and Grafana

   To remove the Prometheus Operator follow the instructions in the [Remove kube-prometheus](https://prometheus-operator.dev/docs/prologue/quick-start/#remove-kube-prometheus) section of the Prometheus Operator Quick Start.

