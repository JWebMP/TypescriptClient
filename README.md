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

## How to use these rules
- Library rules index: `rules/generative/frontend/jwebmp/typescript/README.md` (annotations, scanning/runtime, configuration/rendering, testing, CI/release).
- Follow stage gates: Stage 1–3 are documentation; Stage 4 (implementation) is allowed because blanket approval is recorded in `PACT.md`.
- Keep host docs outside `rules/`; reference diagrams under `docs/architecture/` and topic glossary under `rules/generative/frontend/jwebmp/typescript/GLOSSARY.md`.
- Generated TypeScript is read-only; change Java annotations/configuration and rerun the generator instead of editing outputs.

## Prompt language alignment & glossary
- Canonical topic glossary: `rules/generative/frontend/jwebmp/typescript/GLOSSARY.md` (Ng* metadata, CRTP fluency, LLM guidance).
- Root glossary router: `GLOSSARY.md` links to enterprise glossaries (`rules/`) and the topic glossary above. Host projects should copy only enforced prompt-language mappings and link back for everything else.

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
