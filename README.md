# ScalableCICD - Test Suite

This project contains TestNG/Selenium UI tests with env-driven configuration.

Running locally
----------------
- Compile only:

```bash
mvn -DskipTests=true test-compile
```

- Run smoke tests against staging in headless mode:

```bash
mvn -Denv=staging -Dheadless=true -Dgroups=smoke test
```

- Run all tests:

```bash
mvn test
```

GitHub Actions
---------------
A GitHub Actions workflow is configured at `.github/workflows/ci.yml`. It runs on push and supports manual dispatch with inputs:
- `env` (local/staging/prod) — which `env/{env}.properties` to use
- `groups` (TestNG groups to run, default `smoke`)
- `tag` optional label for the run

To trigger manually from the Actions UI, open the workflow and click `Run workflow`.

Examples for CI runs
-------------------
- Run smoke tests on staging (via workflow_dispatch): set `env=staging`, `groups=smoke`, `tag=nightly-staging`

- Run full regression on prod (be careful): set `env=prod`, `groups=regression`

Notes
-----
- `TestBase` reads config in this order: System properties -> environment variables -> `src/test/resources/env/{env}.properties` -> default.
- Local runs require Chrome. Grid runs require a reachable Selenium Grid URL configured in the env properties.
