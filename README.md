# CardWiki

![](https://reset.inso.tuwien.ac.at/repo/2020ss-SEPM-group/ss20_sepm_qse_02/badges/development/pipeline.svg)
![](https://reset.inso.tuwien.ac.at/repo/2020ss-SEPM-group/ss20_sepm_qse_02/badges/development/coverage.svg)

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
