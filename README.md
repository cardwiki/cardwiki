# CardWiki

![CardWiki logo](frontend/src/assets/logo.png)

A flashcard learning web app where decks can be edited collaboratively (like in a wiki).

![Backend Tests](https://github.com/cardwiki/cardwiki/workflows/Backend%20Tests/badge.svg)
![Frontend compiles](https://github.com/cardwiki/cardwiki/workflows/Frontend%20compiles/badge.svg)
![Backend code coverage](https://codecov.io/gh/cardwiki/cardwiki/branch/master/graph/badge.svg)

## Features

* **Learn with spaced-repetition** (our algorithm is inspired by Anki & SuperMemo2)
* **Markdown, LaTeX, images**
* **Share & collaborate**: Decks are public and can be edited by anyone.
* **History**: Revisions can be easily reviewed and reverted.
* **Organize** cards into categories.
* **Remix**: both decks and cards can be easily copied.
* **No password**: Login with GitHub, GitLab or Google

## Setup

The backend is written in Java with [Spring Boot](https://spring.io/projects/spring-boot) and the [Hibernate ORM](https://hibernate.org/orm/). The frontend is written in TypeScript with [Angular](https://angular.io/).

For setup instructions refer to the READMEs in [backend](backend/README.md) and [frontend](frontend/README.md).

## Contributing

Issues and pull requests are welcome!

For contributing code, check out our [contributions guide](CONTRIBUTING.md).
