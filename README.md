###### refarch-cloudnative-wfd-ui

### Microservices Reference Application - What's For Dinner User-Interface Service

*This project is part of the 'IBM Cloud Architecture - Microservices Reference Application for Netflix OSS' suite, available at
https://github.com/ibm-cloud-architecture/refarch-cloudnative-netflix*

#### Introduction

This project is built to demonstrate how to build a Spring Boot application for use in a microservices-based architecture:
 - Leverage Spring Boot framework to build a microservices application.
 - Integrate with [Netflix Eureka](https://github.com/Netflix/eureka) and [Ribbon](https://github.com/Netflix/ribbon) framework.
 - Display a simpler user interface, based on Spring MVC, with the content of the [What's for Dinner Menu Service](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-menu).
 - Deployment options for local, Cloud Foundry, or Docker Container-based runtimes (including [IBM Container Service](https://console.ng.bluemix.net/docs/containers/container_index.html) on [Bluemix](https://new-console.ng.bluemix.net/#overview)).

#### APIs in this application
You can access the user interface directly in a browser via `http://<hostname>:8181/`

#### Pre-requisite:
- You need a Docker machine running on localhost to host container(s). [Click for instructions](https://docs.docker.com/machine/get-started/).

#### Build the application
1. Clone git repository.
    ```
    git clone https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-ui
    cd refarch-cloudnative-wfd-ui
    ```

2. Build the application.  A utility script is provided to easily build using either Gradle (default) or Maven.  You can optionally specify the `-d` parameter to build the associated Docker image as well.  The default Gradle build instructions use a Gradle wrapper requiring no further installation.  The Maven build instructions require Maven to be installed locally.

  2.1 Build the application using Gradle:
    ```
    ./build-microservice.sh [-d]
    ```

  2.2 Build the application using Maven:
    ```
    ./build-microservice.sh -m [-d]
    ```

#### Run UI Service on localhost
In this section you will deploy the Spring Boot application to run on your localhost.

1.  [Setup Eureka](https://github.com/ibm-cloud-architecture/refarch-cloudnative-netflix-eureka#run-the-application-component-locally) to run locally.

2.  Run the required downstream services:  
  2.1.  [Appetizer](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-appetizer#run-appetizer-service-on-localhost)
  2.2.  [Entree](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-entree#run-entree-service-on-localhost)
  2.3.  [Desserts](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-dessert#run-dessert-service-on-localhost)
  2.4.  [Menu](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-menu#run-menu-service-on-localhost)

3. Run the application on localhost (assuming default Gradle build).  If Eureka is not running locally, you will need to pass the location of the Eureka server as a command-line paramter.
    ```
    java [-Deureka.client.serviceUrl.defaultZone=http://eureka-host:port/eureka/] -jar build/libs/wfd-ui-0.0.1-SNAPSHOT.jar
    ```

4. Validate via `http://localhost:8181` in your browser.

#### Run UI Service application on local Docker container
In this section you will deploy the Spring Boot application to run in a local docker container.

1. Build service and container image:
    ```
    ./build-microservice.sh -d
    ```

2. If not done so already, [Setup Eureka](https://github.com/ibm-cloud-architecture/refarch-cloudnative-netflix-eureka#run-the-application-component-locally) to run locally.

3.  Run the required downstream services:  
  3.1.  [Appetizer](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-appetizer#run-appetizer-service-on-local-docker-container)
  3.2.  [Entree](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-entree#run-entree-service-on-local-docker-container)
  3.3.  [Desserts](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-dessert#run-dessert-service-on-local-docker-container)
  3.4.  [Menu](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-menu#run-menu-service-application-on-local-docker-container)

4. Start the application in docker container.
    ```
    docker run -d -p 8181:8181 --name wfd-ui --rm --env "eureka.client.serviceUrl.defaultZone=http://eureka-host:port/eureka/"  wfd-ui
    ```

5. Validate via `http://localhost:8181` in your browser.  

#### Deploy UI Service to Cloud Foundry runtime, on IBM Bluemix

1. Log in to your Bluemix account.
    ```
    cf login -a <bluemix-api-endpoint> -u <your-bluemix-user-id>
    ```

2. Set target to use your Bluemix Org and Space.
    ```
    cf target -o <your-bluemix-org> -s <your-bluemix-space>
    ```

3. [Setup Eureka on Bluemix](https://github.com/ibm-cloud-architecture/refarch-cloudnative-netflix-eureka#run-the-application-component-on-bluemix).

4.  Run the required downstream services:  
  4.1.  [Appetizer](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-appetizer#deploy-appetizer-service-to-cloud-foundry-runtime-on-ibm-bluemix)
  4.2.  [Entree](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-entree#deploy-entree-service-to-cloud-foundry-runtime-on-ibm-bluemix)
  4.3.  [Desserts](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-dessert#deploy-dessert-service-to-cloud-foundry-runtime-on-ibm-bluemix)
  4.4.  [Menu](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-menu#deploy-menu-service-to-cloud-foundry-runtime-on-ibm-bluemix)

5.  Create a user-provided service for Eureka, so the services running in Cloud Foundry can bind to it:

    ```
    cf create-user-provided-service eureka-service-discovery -p "{\"uri\": \"http://{eureka-host}/eureka/\"}"
    ```

6. Start the application in a Cloud Foundry runtime on IBM Bluemix.

  The following commands push the service code to Bluemix and creates a Cloud Foundry application.  It then sets the desired Spring Boot profile for the application to configure itself correctly, as well as binds the user-provided service with the Eureka endpoint information.  Finally, it restages the application code to ensure it receives all the configuration changes and then starts the application.  

  _NOTE_: There is no need for a port in the Eureka parameter, as the Container Group running Eureka is listening on port 80 (the default HTTP port) and will forward to the necessary port of 8761 that Eureka is listening on.  

    ```
    cf push -p build/libs/wfd-ui-0.0.1-SNAPSHOT.jar -d mybluemix.net -n wfd-ui-{your-bluemix-user-id} --no-start

    cf set-env wfd-ui SPRING_PROFILES_ACTIVE cloud

    cf bind-service wfd-ui eureka-service-discovery

    cf restage wfd-ui

    cf start wfd-ui
    ```

7. Validate via `http://localhost:8181` in your browser.  


#### Deploy UI Service to Docker Container, on IBM Bluemix

1. Log in to your Bluemix account.
    ```
    cf login -a <bluemix-api-endpoint> -u <your-bluemix-user-id>
    ```

2. Set target to use your Bluemix Org and Space.
    ```
    cf target -o <your-bluemix-org> -s <your-bluemix-space>
    ```

3. Log in to IBM Containers plugin.
    ```
    cf ic init
    ```

4. Tag and push the local docker image to bluemix private registry.
    ```
    docker tag wfd-ui registry.ng.bluemix.net/$(cf ic namespace get)/wfd-ui:latest
    docker push registry.ng.bluemix.net/$(cf ic namespace get)/wfd-ui:latest
    ```

5. [Setup Eureka on Bluemix](https://github.com/ibm-cloud-architecture/refarch-cloudnative-netflix-eureka#run-the-application-component-on-bluemix).

6.  Run the required downstream services:  
  6.1.  [Appetizer](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-appetizer#deploy-appetizer-service-to-docker-container-on-ibm-bluemix)
  6.2.  [Entree](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-entree#deploy-entree-service-to-docker-container-on-ibm-bluemix)
  6.3.  [Desserts](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-dessert#deploy-desserts-service-to-docker-container-on-ibm-bluemix)
  6.4.  [Menu](https://github.com/ibm-cloud-architecture/refarch-cloudnative-wfd-menu#deploy-menu-service-to-docker-container-on-ibm-bluemix)

7. Start the application in an IBM Bluemix Container. Replace `{eureka-host}` with the public route configured in the deployment of Eureka to Bluemix.  

  _NOTE_: There is no need for a port in the Eureka parameter, as the Container Group running Eureka is listening on port 80 (the default HTTP port) and will forward to the necessary port of 8761 that Eureka is listening on.  
    ```
    cf ic group create -p 8180 -m 256 --min 1 --auto --name wfd-menu-group -e "--env "eureka.client.serviceUrl.defaultZone=http://eureka-host/eureka/" -n wfd-menu-$(cf ic namespace get) -d mybluemix.net registry.ng.bluemix.net/$(cf ic namespace get)/wfd-menu:latest
    ```

8. Validate via `http://localhost:8181` in your browser.  
