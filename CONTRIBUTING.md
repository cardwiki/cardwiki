# Contributing to CardWiki

## Backend

For developing the backend we recommend [IntelliJ](https://www.jetbrains.com/idea/). Since we use [Lombok](https://projectlombok.org/) to generate getters and setters of DTOs, you will also want to install the [Lombok plugin for your IDE](https://projectlombok.org/setup/overview).

If you start the backend with the `dev` profile, the database is populated with test data and you can easily log in with a test user and a test admin:

	mvn spring-boot:run -Dspring-boot.run.profiles=dev

Note that if the H2 database is not clean, the test data won't be inserted

## Frontend

For developing the frontend we recommend either IntelliJ or [VS Code](https://code.visualstudio.com/).

Angular provides `ng lint` for linting the source and `ng lint --fix` for fixing linting erros.
