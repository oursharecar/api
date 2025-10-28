# Repository Guidelines

## Project Structure & Module Organization
- `server/src/main/kotlin` hosts the Ktor entrypoints (`Application.kt`, `Routing.kt`, etc.) with configuration in `server/src/main/resources/application.yaml`.
- `server/src/test/kotlin` contains HTTP and serialization tests driven by `kotlin.test`.
- `core/src/commonMain/kotlin` defines shared telemetry contracts consumed by both the server and the client.
- `client/src/commonMain/kotlin` provides HTTP helpers for calling the server; keep generated build outputs under each module’s `build/` directory out of commits.

## Build, Test, and Development Commands
- `./gradlew build` compiles all modules and executes their test suites.
- `./gradlew :server:run` starts the CIO server locally (listens on http://localhost:8080 by default).
- `./gradlew :server:buildFatJar` produces a runnable shaded JAR in `server/build/libs/`.
- `./gradlew :server:publishImageToLocalRegistry` publishes the Docker image built from the fat JAR.
Ensure a JDK 17+ is installed—the wrapper targets Kotlin 2.2.20 and Ktor 3.3.0.

## Coding Style & Naming Conventions
- Use Kotlin’s standard 4-space indentation and enable trailing commas in multiline argument lists.
- Keep packages lowercase dot-separated; classes and objects UpperCamelCase; functions and properties lowerCamelCase.
- Keep routing logic in top-level functions (`Routing.kt`) and model contracts in `core` to simplify reuse.
- Run `./gradlew build` before committing to catch compiler and serialization configuration issues; introduce additional linters only once they are wired into `build.gradle.kts`.

## Testing Guidelines
- Mirror source packages in `server/src/test/kotlin`; reuse `ApplicationTest.kt` as the template for new route scenarios.
- Prefer `testApplication {}` for endpoint coverage and `kotlin.test` assertions for lightweight checks.
- Add integration tests when touching routing, authentication, or serialization; keep domain-only tests inside `core`.
- Execute `./gradlew :server:test` prior to every PR and note remaining manual verification in the PR description.

## Commit & Pull Request Guidelines
- Follow the existing history: concise imperative subjects (e.g., `Add health check`) under 72 characters, optional body for context.
- Reference issue IDs in the body when available and describe configuration or schema changes explicitly.
- PRs should summarize module impact, list executed commands, and attach curl examples or screenshots for user-facing changes.

## Configuration Tips
- Adjust environment-specific values in `server/src/main/resources/application.yaml`; never commit secrets—pass them via environment variables.
- Log levels live in `server/src/main/resources/logback.xml`; tune logging there rather than in code.
