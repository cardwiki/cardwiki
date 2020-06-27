# Backend Template for SEPM Group Phase

## How to run it

### Start the backed

	mvn spring-boot:run

### Start the backed with test data

If the database is not clean, the test data won't be inserted

	mvn spring-boot:run -Dspring-boot.run.profiles=generateData

## Deployment

Configure `src/main/resources/application-prod.yml` and run it with `-Dspring-boot.run.profiles=prod`.

To initially give your user admin rights run:

	mvn spring-boot:run -Dspring-boot.run.profiles=makeAdmin -Dspring-boot.run.arguments=--admin-username=<username>
