# Drones

This is an example of REST API service that allows clients to communicate with the drones.
It uses embedded PostgreSQL, so you don't need to install any DB.


**Prerequisites:** [Java 11]

* [Getting Started](#getting-started)
* [Usage](#usage)
* [Links](#links)

## Getting Started

To build this example application, run the following command:

```bash
./mvnw clean build
```

To run this example application, run the following command:

```bash
./mvnw spring-boot:run
```

To run unit test for this example application, run the following command:

```bash
./mvnw test
```

### Usage

Run the example application to use the following requests. 
Also, you can use test files from `./src/test/resources/data` as a requests content.

Registering a drone:

```bash
curl -X PUT -H "Content-Type: application/json" -d @drone.json localhost:8080/drone/register
```
Loading a drone with medication items:

```bash
curl -X POST -H "Content-Type: application/json" -d @medications_list.json localhost:8080/drone/12345/load
```
Checking loaded medication items for a given drone:

```bash
curl localhost:8080/drone/12345/medications
```
Checking available drones for loading:

```bash
curl localhost:8080/drone/available
```
Check drone battery level for a given drone:

```bash
curl localhost:8080/drone/12345/battery
```

## Links

This example uses the following open source libraries:

* [Spring Boot](https://spring.io/projects/spring-boot)
* [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc)
* [FlyWay](https://flywaydb.org/)
