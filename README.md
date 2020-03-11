# mg-smartrent

Configure your maven git repository since gradle is looking at the token 
from password section of the server

# SmartRent Application Backend
 This is a backend of an application designed to report and review Renters, manage renting process, property and related issues, requests or offers. 

## Built With

* 	[Gradle](https://gradle.com/) - Dependency Management
* 	[JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) - Java™ Platform, Standard Edition Development Kit 
* 	[Spring Boot](https://spring.io/projects/spring-boot) - Framework to ease the bootstrapping and development of new Spring Applications
* 	[Eureka](https://spring.io/guides/gs/service-registration-and-discovery/) - Netflix Eureka service registry.
* 	[Sleuth](https://www.baeldung.com/spring-cloud-sleuth-single-application) - Distributed logging capable to trace operations between REST requests and not only.
* 	[Zipkin](https://zipkin.io/) - Zipkin is a distributed tracing system. It helps gather timing data needed to troubleshoot latency problems in service architectures. Features include both the collection and lookup of this data.
* 	[Lombok](https://projectlombok.org/) - Never write another getter or equals method again, with one annotation your class has a fully featured builder, Automate your logging variables, and much more.
* 	[Valid4j](http://www.valid4j.org/) - valid4j is a simple assertion and validation library for Java which makes it possible to use your favorite hamcrest-matchers to express pre- and post-conditions in your code in a design by contract style.
* 	[Docker](https://www.docker.com/) - Docker is a set of platform as a service products that uses OS-level virtualization to deliver software in packages called containers..
* 	[Git](https://git-scm.com/) - Free and Open-Source distributed version control system 

## To-Do
- [ ] Secure REST endpoints
- [ ] Persist logs reported to the Zipkin Server
- [ ] Externalize application configuration (use Spring Config Server)
- [ ] Integrate application with Jenkins CI/CD. Create jobs that can be bootstraped from Git

## Running the application
#####Prerequisites:
- Java 1.8
- Docker

##### Run the app:
The fastest way to start the application is to build it locally and generate docker containers for each of the Microservices - then run docker images 
1. Run gradle command ``gradlew build`` - this will generate distribution packages for all the MicroServices and local Docker images.
2. Run gradle command ``gradlew runDockerContainer`` - this will start all the Docker images locally.

#####App monitoring URLs:
|Endpoint|UI Name|Description|
|--------|------------|-----------|
|http://localhost:8081/ |Eureka Dashboard |Provides and overview of Ecosystem Microservices. Default UI user is `admin` pass `12341234`|
|http://localhost:9411/zipkin/ |Zipkin Dashboard |Provides access to application logs that are aggregated from all the services. |

##### App REST endpoints:
Routing is realized trough Zuul framework integrated in the Gateway API service.

|Endpoint|Service Name|Description|
|--------|------------|-----------|
|http://localhost:8081/user-service/rest/<endpoint> |User service |Provides access to User domain|
|``http://localhost:8081/user-service/rest/users?exists={trackingID}``|
|http://localhost:8081/property-service/rest/<endpoint> |Property service |Provides access to Property domain|
|http://localhost:8081/renter-service/rest/<endpoint> |Renter service |Provides access to Renter domain|

##### Other Gradle commands
- ``gradlew removeAllDockerImages`` - remove forcefully ALL local images.  

## Release management
To release the application run bellow gradle command: 
``gradlew release`` this will release all the Microservices and:
 * Remove version SNAPSHOT word from the services and docker images.
 * Create a release on the GitHub (Ex: version 0.0.1)
 * Create a new SNAPSHOT incremented version (Ex: version 0.0.2-SNAPSHOT)
 

## Running the tests
Run command 
```
gradlew test
``` 
 


## Documentation

* [TBD](https://documenter.getpostman.com/view/2449187/RWTiwzb2) - TBD


## Reporting Issues

This Project uses GitHub's integrated issue tracking system to record bugs and feature requests. If you want to raise an issue, please follow the recommendations below:

* Before you log a bug, please https://github.com/maimas/mg-smartrent/issues [search the issue tracker]
  to see if someone has already reported the problem.
* If the issue doesn't already exist, https://github.com/maimas/mg-smartrent/issues/new[create a new issue]. 
* Please provide as much information as possible with the issue report.
* If you need to paste code, or include a stack trace use Markdown +++```+++ escapes before and after your text. 
  
## Resources

* [My API Lifecycle Checklist and Scorecard](https://dzone.com/articles/my-api-lifecycle-checklist-and-scorecard)
* [HTTP Status Codes](https://www.restapitutorial.com/httpstatuscodes.html)
* [Common application properties](https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html)


## License
TBD
<!--[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FSpring-Boot-Framework%2FSpring-Boot-Application-Template.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FSpring-Boot-Framework%2FSpring-Boot-Application-Template?ref=badge_large)-->
