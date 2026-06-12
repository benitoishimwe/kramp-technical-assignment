# Contributing

## Getting Started

1. Fork the repository and create a feature branch from `main`:
   ```bash
   git checkout -b feat/your-feature-name
   ```
2. Make your changes, add tests, and ensure the full test suite passes:
   ```bash
   ./mvnw verify
   ```
3. Open a pull request against `main`. CI will run build, test, and static analysis automatically.

## Branch Naming

| Prefix | Use for |
|---|---|
| `feat/` | New features |
| `fix/` | Bug fixes |
| `refactor/` | Non-functional changes |
| `docs/` | Documentation only |
| `chore/` | Build, CI, dependency updates |

## Code Standards

- **Java 17** — use records, sealed classes, and text blocks where they simplify the code.
- **Lombok** is already on the classpath; prefer `@Value`/`@Builder`/`@Slf4j` over boilerplate.
- **No class-level comments** that just restate the class name. Javadoc on public classes must explain the *why*, not the *what*.
- **Port/Adapter boundary** — `AggregationService` must not import any `upstream.*` concrete class directly. Add a new port interface first.
- **Partial failure** — any new upstream call must be classified as *required* or *optional* and handled accordingly (throw `CatalogUnavailableException` or degrade via `.handle()`).

## Adding a New Upstream Service

1. Define a record in `model/upstream/` for the raw data.
2. Define an interface in `service/port/`.
3. Add a `Mock*Service` in `service/upstream/` extending `AbstractMockService`.
4. Wire the new port into `AggregationService` and add it to the `CompletableFuture` fan-out.
5. Extend `ProductInfoResponse` and the proto schema.
6. Add at minimum two `AggregationServiceTest` cases: success and degraded.

## Running Tests

```bash
./mvnw test                  # unit tests only
./mvnw verify                # unit + integration (full build)
```

Test reports are written to `target/surefire-reports/`.

## Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
feat(grpc): add server-side streaming for batch product lookups
fix(catalog): handle null specs map returned by upstream
docs(readme): add curl examples for degraded pricing scenario
```

## Pull Request Checklist

- [ ] Tests added or updated for the changed behaviour
- [ ] `./mvnw verify` passes locally
- [ ] Javadoc updated on any new public class
- [ ] Proto schema changes are backwards-compatible (new fields use new field numbers)
- [ ] `application.yml` updated if new configuration keys are introduced
