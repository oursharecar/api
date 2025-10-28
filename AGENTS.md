# Repository Guidelines

## Project Structure & Module Organization
- `server/src/main/kotlin` hosts Ktor entrypoints (`Application.kt`, `Routing.kt`) and service wiring.
- `server/src/test/kotlin` mirrors the server package layout for HTTP and serialization tests.
- Shared telemetry contracts live in `core/src/commonMain/kotlin` for reuse across modules.
- HTTP client helpers for the server API are in `client/src/commonMain/kotlin`.
- Generated outputs are kept under each module’s `build/` directory; never commit these artifacts.

## Build, Test, and Development Commands
- `./gradlew build` compiles all modules and executes their test suites; run before every PR.
- `./gradlew :server:run` launches the CIO server locally on `http://localhost:8080`.
- `./gradlew :server:buildFatJar` produces a shaded JAR in `server/build/libs/`.
- `./gradlew :server:publishImageToLocalRegistry` publishes the Docker image built from the fat JAR.

## Coding Style & Naming Conventions
- Kotlin 4-space indentation everywhere; enable trailing commas in multiline argument lists.
- Packages remain lowercase dot-separated; classes/objects use UpperCamelCase; functions and properties use lowerCamelCase.
- Keep routing logic in top-level functions under `server/src/main/kotlin`, and place reusable contracts in `core`.

## Testing Guidelines
- Use `kotlin.test` with `testApplication {}` for endpoint coverage; follow the layout in `server/src/test/kotlin/ApplicationTest.kt`.
- Add integration tests when modifying routing, authentication, or serialization; keep domain-only checks in `core`.
- Run `./gradlew :server:test` for server-only verification; prefer `./gradlew build` before pushing.

## Commit & Pull Request Guidelines
- Write imperative commit subjects under 72 characters (e.g., `Add health check`); include bodies for context or issue links.
- PRs should summarize module impact, list executed commands, and attach curl examples or screenshots for user-visible changes.
- Call out configuration or schema updates explicitly and verify that tests pass locally before requesting review.

## Configuration & Security Tips
- Adjust environment-specific settings in `server/src/main/resources/application.yaml`; never commit secrets—pass them via environment variables.
- Tune log levels in `server/src/main/resources/logback.xml` instead of in code.
- Confirm JDK 17+ is installed; Kotlin 2.2.20 and Ktor 3.3.0 are the baseline versions targeted by the Gradle wrapper.

## Future Plans
- Introduce contract tests for the Mongo repositories to exercise pagination and soft-delete flows.
- Add structured logging contexts around routing to surface tenant identifiers and request IDs.
- Wire JWT configuration to a secrets manager stub so local runs mimic production setup.
