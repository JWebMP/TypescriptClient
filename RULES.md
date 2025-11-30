# Project RULES — JWebMP Typescript Client Library

Scope: Java 25 LTS Maven library that exposes JWebMP annotations and helpers to generate Angular 20 TypeScript artifacts. Forward-only docs-first workflow with CRTP fluent API and Log4j2 logging.

## Policy Anchors
- Follow behavioral/technical/modularity/forward-only sections from `rules/RULES.md`.
- Documentation-first stage gates; blanket approval is recorded but STOPs remain traceable.
- No project docs in `rules/`; host docs live in repo root or `docs/`.
- Logging: Log4j2 default; Lombok logging uses `@Log4j2`.
- Nullness: JSpecify defaults per rules; avoid widening nullability without docs.

## Selected Stacks & Rule Links
- Java 25 LTS + Maven: `rules/generative/language/java/java-25.rules.md`, `rules/generative/language/java/build-tooling.md`.
- TypeScript + Angular 20: `rules/generative/language/typescript/README.md`, `rules/generative/language/angular/README.md`, `rules/generative/language/angular/angular-20.rules.md`.
- JWebMP Core/Client: `rules/generative/frontend/jwebmp/README.md`, `rules/generative/frontend/jwebmp/client/README.md`.
- GuicedEE Client + lifecycle: `rules/generative/backend/guicedee/README.md`, `rules/generative/backend/guicedee/client/README.md`.
- Fluent API (CRTP enforced): `rules/generative/backend/fluent-api/README.md`, `rules/generative/backend/fluent-api/crtp.rules.md`.
- DDD/TDD framing: `rules/generative/architecture/README.md`, `rules/generative/architecture/tdd/README.md`.
- CI/CD provider: GitHub Actions via `rules/generative/platform/ci-cd/README.md` and `rules/generative/platform/ci-cd/providers/github-actions.md`.
- Secrets/config: `rules/generative/platform/secrets-config/README.md` and `rules/generative/platform/secrets-config/env-variables.md`.

## Fluent API & Lombok Alignment
- Fluent setters use CRTP; do not introduce Lombok builders.
- Use Lombok `@Log4j2` when adding loggers; avoid other Lombok logging annotations.
- Preserve module boundaries in `module-info.java`; new exports/opens require docs and justification.

## Glossary & Traceability
- Glossary precedence: topic glossaries override root; see `GLOSSARY.md` for routing.
- Close loops: RULES ↔ GUIDES ↔ IMPLEMENTATION ↔ diagrams under `docs/architecture/`.

## Stage Gates (record only)
Blanket approval is active for this run; Stage 1–3 docs proceed without pausing, but each gate is documented in responses.
