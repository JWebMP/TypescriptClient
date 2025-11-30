# AngularTSClient
A Maven Java 25 library that exposes Ng* annotations and helpers so JWebMP components can generate TypeScript metadata.

## Docs-first workflow
- Pact: `PACT.md`
- Rules: `RULES.md`
- Guides: `GUIDES.md`
- Implementation: `IMPLEMENTATION.md`
- Glossary: `GLOSSARY.md`
- Architecture diagrams: `docs/architecture/README.md`
- Prompt reference for future runs: `docs/PROMPT_REFERENCE.md`

## Rules Repository
- The enterprise rules live in the `rules/` submodule (`.gitmodules` already configured). Keep host project docs outside that directory.
- Follow behavioral/technical/forward-only/modularity guidance from `rules/RULES.md` plus stack-specific rule links in `RULES.md`.

## Build & test
- Requirements: Java 25 LTS, Maven.
- Run tests: `mvn test`
- Module entry point: `module-info.java` exports Ng annotation packages and registers Guice modules; scanning is Vert.x-backed (`AngularTypeScriptPostStartup`).

## CI & environment
- GitHub Actions workflow: `.github/workflows/maven-package.yml` uses GuicedEE shared workflow; configure secrets `USERNAME`, `USER_TOKEN`, `SONA_USERNAME`, `SONA_PASSWORD`.
- Example environment: `.env.example` (derived from `rules/generative/platform/secrets-config/env-variables.md`).

## AI assistants
- Docs-first stage gates apply (Stage 1–3 docs before Stage 4 code; blanket approval noted in PACT).
- Assistant-specific rules live in `.aiassistant/rules/` and `.github/copilot-instructions.md`; keep links across PACT ↔ GLOSSARY ↔ RULES ↔ GUIDES ↔ IMPLEMENTATION ↔ `docs/architecture/`.
