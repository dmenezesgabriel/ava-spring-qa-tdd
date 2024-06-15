# Java Spring QA TDD

Project done in FIAP's Software Architecture Post Graduation course with the purpose of applying QA to software development.

- Fluent tests with assertJ
- Example Spring API
- Unit tests mocked with Mockito
- Integration tests using H2 Database

## Use Maven from a specific project fom intellij terminal

https://www.jetbrains.com/help/idea/terminal-emulator.html#jqufbj_147

## Maven Commands

- **Unit tests**:

```shell
mvn test -P unit-test
```

-**Integration tests**:

```shell
mvn test -P integration-test
```

-**System tests**:

```shell
mvn test -P system-test
```

- **Smoke tests**:

```shell
mvn test -P system-test -Dcucumber.filter.tags=@smoke
```

## Alure Report

- **Install**:

```shell
npm install -g allure-commandline
```

- **Run**:
```shell
npx allure serve target/allure-results
```

PATH=$(npm get prefix)