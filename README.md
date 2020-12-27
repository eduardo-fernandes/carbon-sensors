# carbon-sensors

This project has a set of endpoints (service) which registers and create reports for carbon consumption.

As a reminder, below is the acceptance criteria provided in the code challenge description:

 - The service should be able to receive measurements from each sensor at
the rate of 1 per minute
 - If the CO2 level exceeds 2000 ppm the sensor status should be set to WARN
 - If the service receives 3 or more consecutive measurements higher than
2000 the sensor status should be set to ALERT
 - When the sensor reaches to status ALERT an alert should be stored
 - When the sensor reaches to status ALERT it stays in this state until it receives 3
consecutive measurements lower than 2000; then it moves to OK
 - The service should provide the following metrics about each sensor:
 - Average CO2 level for the last 30 days
 - Maximum CO2 Level in the last 30 days
 - It is possible to list all the alerts for a given sensor

Below are some minor issues found in the description, and provided API, and how they were addressed:

- No rule for the case when the sensor status is set to WARN, and the next reading is below 2000 ppm. Here will be assumed that for this case, the status is set back to OK.
- The `POST /api/v1/sensors/{uuid}/mesurements` has a typo in measurements. It should be: `POST /api/v1/sensors/{uuid}/measurements`. It will be corrected in this application.
- The response payload from the method `GET /api/v1/sensors/{uuid}/alerts` will return a list of measurements, instead of `measurement1`, `measurement2` and `measurement3`. By doing like this, we get an array of N measurements, having a more generic and meaningful way of showing such values.

## Requirements

In order to handle this project appropriately, please setup the following tools:

 - Git
 - Java (JDK) 11
 - Maven 3
 - IntelliJ was used for development, but Eclipse should work

## Cloning the project
In order to clone the project, please execute either one of the following commands:
 
for cloning with SSH
```
git clone git@github.com:eduardo-fernandes/carbon-sensors.git
```
 
or for cloning with HTTPS
 
```
git clone https://github.com/eduardo-fernandes/carbon-sensors.git
```

## How to test and start the application via command line
Please run all commands below from the root of the project.
 
In order to run unit and integration tests, execute the following command:
 
```
mvn clean verify package
```
 
Should you wish only to run the unit tests, execute the following command:
 
```
mvn verify -Dit.skip=true
```
 
For one to start the application, run the following command:
 
```
mvn spring-boot:run
```

Access the application in [http://localhost:8080/](http://localhost:8080/)

## Database
Here the out-of-the-box H2 database is used - for Spring-Boot, since no configuration is required, and in case we wish to change it, only more configuration will be required. This argument makes H2 the perfect choice for a demo application.

### Accessing local H2 database
After starting the application, one can access the embedded H2 database via the following URL: [http://localhost:8080/h2-console/](http://localhost:8080/h2-console/). Here is the configuration needed:
 - Setting Name: Generic H2 (Embedded)
 - Driver Cass: org.h2.Driver
 - JDBC URL: jdbc:h2:mem:testdb - pay attention because by default this is another URL
 - User Name: sa
 - Password: leave it empty
 
## Architectural overview
This application was developed using out-of-the-box Spring-Boot. This is a very good choice for a Java-based web application, because it gives you everything you need by default. You do not have to configure, databases, application servers, and so on. You simply set it up, develop and run. Later, when you wish setup your infrastructure, you only add configuration to your software, and _do not_ need to change your code.

As mentioned before H2 is used as an embedded database, because no extra effort is required apart from starting the application. As for everything in Spring-Boot, changing that is only a matter of configuration.

### Layers
Here are the layers of this app:

- Data Model - package: `com.carbonsensors.model`
- Repositories - package: `com.carbonsensors.repository`
- Services - package: `com.carbonsensors.service`
- Controller - package: `com.carbonsensors.controller`
- Data Transfer Objects (DTOs) - package: `com.carbonsensors.dto`
- Configuration files - package: `com.carbonsensors.config`

Note that DTOs were used to expose data in the REST endpoints. Some developers simply expose the model Entities. I do not like this approach because: 
- Usually the object you wish to expose is different from the database entity.
- It is nice to add Swagger documentation annotation in these DTOs. If we used the model entities, which already have annotations of their own, the code would be quite confusing.

### Endpoints
Here are the provided endpoints:
 - `POST - /api/v1/sensors` - create a sensor, returning its id
 - `GET - /api/v1/sensors/{sensorId}` - get a certain sensor status, based on its id
 - `POST - /api/v1/sensors/{sensorId}/measurements` - create a measurement associated with a certain sensor
 - `GET - /api/v1/sensors/{sensorId}/metrics` - get a certain sensor metrics
 - `GET - /api/v1/sensors/{sensorId}/alerts` - get alerts from a certain sensor
 
### Tests

Note the application was thoroughly unit tested, specially converters and services. Moreover, there is an integration test which goes through the whole API - `com.carbonsensors.e2e.ApiEndToEndITCase`

It is also interesting to observe that the repository classes were also tested as integration tests in the package - `com.carbonsensors.repository`. Since big part of the logic lives in the database queries themselves, it makes a lot of sense have automated tests for them.

### Test coverage
After running  `mvn clean verify package` one can access the code coverage report at the following file: `/carbon-sensors/target/site/jacoco/index.html`. Note the coverage is reasonably good, considering a coding challenge project.

### No security setup
It is important to say that no security mechanism whatsoever was setup here, since it was not in the goal of this assignment. Not that, Spring-Boot fully supports it while adding configuration.

## Accessing Swagger documentation
You can easily play around with the endpoints using swagger. In order to access it for this APi, please access [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).
 
 