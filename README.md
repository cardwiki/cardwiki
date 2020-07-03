# CardWiki

![CardWiki logo](frontend/src/assets/logo.png)

![Backend Tests](https://github.com/cardwiki/cardwiki/workflows/Backend%20Tests/badge.svg)
![Frontend compiles](https://github.com/cardwiki/cardwiki/workflows/Frontend%20compiles/badge.svg)
![Backend code coverage](https://codecov.io/gh/cardwiki/cardwiki/branch/master/graph/badge.svg)

## Setup

Running the backend in IntelliJ requires the [Lombok Plugin](https://projectlombok.org/setup/intellij).

## Howto user story

1. create a GitLab issue `US01: Login` and assign yourself to it
2. create a new branch in GitLab from `development` e.g. `us01-login`
3. `git fetch origin us01-login`
4. `git checkout us01-login`
7. `doWork();`
8. `git pull origin development`
8. `git push`
9. create a merge request on GitLab, source: your branch, target: development  
   add `closes #<issue id>` or `part of #<issue id>` in the description

Do not merge your own merge requests! Don't forget to log your time!

## DTO Naming Convention

Put the entity name first, e.g. `CategoryDetailedDto` instead of `DetailedCategoryDto`.
