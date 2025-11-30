# PROMPT Reference — JWebMP Typescript Client Library

Load this file before future AI runs. It captures selected stacks, glossary routing, and diagram links.

## Selected Stacks
- Java 25 LTS + Maven
- Angular 20 (TypeScript), TypeScript generation from Ng* annotations
- JWebMP Core/Client + GuicedEE Client lifecycle
- Fluent API: CRTP (no Lombok builders); Lombok logging via `@Log4j2`
- Logging backend: Log4j2; Nullness: JSpecify
- CI/CD: GitHub Actions

## Key Artifacts
- Pact: `PACT.md`
- Rules: `RULES.md`
- Guides: `GUIDES.md`
- Implementation: `IMPLEMENTATION.md`
- Glossary: `GLOSSARY.md` (topic-first; see topic links inside)

## Architecture Diagrams
- Index: `docs/architecture/README.md`
- Context: `docs/architecture/c4-context.md`
- Container: `docs/architecture/c4-container.md`
- Component (Angular metadata pipeline): `docs/architecture/c4-component-angular-metadata.md`
- Sequences: `docs/architecture/sequence-startup-scan.md`, `docs/architecture/sequence-component-config.md`
- ERD: `docs/architecture/erd-angular-metadata.md`

## Policies
- Forward-only; documentation-first stage gates recorded (blanket approval active for this run).
- Host docs stay outside `rules/` submodule.
