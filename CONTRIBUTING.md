# Contributing to CardWiki

## Backend

For developing the backend we recommend [IntelliJ](https://www.jetbrains.com/idea/). Since we use [Lombok](https://projectlombok.org/) to generate getters and setters of DTOs, you will also want to install the [Lombok plugin for your IDE](https://projectlombok.org/setup/overview).

If you start the backend with the `dev` profile, the database is populated with test data and you can easily log in with a test user and a test admin:

	mvn spring-boot:run -Dspring-boot.run.profiles=dev

Note that if the H2 database is not clean, the test data won't be inserted

### Database migration

For database migration we use [Liquibase](https://www.liquibase.org/). Changes in the database scheme are stored in the migration folder.

To automatically generate the changelog you can follow these steps:

1. Make changes in the entities
2. Have your database on the previous version without your changes
3. Run `mvn compile` and `mvn liquibase:diff`. The file liquibase-diff-changeLog.xml will be generated
4. Verify the changes
5. Rename it and include it in db.changelog-master.xml
6. Optionally run `mvn liquibase:updateSQL` to inspect the SQL statements (somewhere in the target folder)
7. Run the application to see if the migration works (e.g. inspect the logs, or the tables via the h2-console)

In general, running the application will apply all new database migrations.

## Frontend

For developing the frontend we recommend either IntelliJ or [VS Code](https://code.visualstudio.com/).

Angular provides `ng lint` for linting the source and `ng lint --fix` for fixing linting erros.

### Cypress tests

For end-to-end (E2E) testing we use [Cypress](https://docs.cypress.io). When writing and running tests, keep in mind that:

- our E2E tests use our backend server
- our E2E tests all use the same database

To open the testing UI, first start the backend and serve the frontend, afterwards run `npm run cypress:open`. From there you can select which tests should run and interactively watch and debug them. Test runs likely require erasing the database and restarting the backend server.