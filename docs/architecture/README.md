# Architecture Index

All diagrams are text-first (Mermaid) and map to observed code in this repository.

- C4 Level 1 (Context): `docs/architecture/c4-context.md`
- C4 Level 2 (Container): `docs/architecture/c4-container.md`
- C4 Level 3 (Angular metadata pipeline): `docs/architecture/c4-component-angular-metadata.md`
- Sequence — startup scan: `docs/architecture/sequence-startup-scan.md`
- Sequence — component config build: `docs/architecture/sequence-component-config.md`
- ERD — annotation to configuration: `docs/architecture/erd-angular-metadata.md`
- Dependency map (trusted boundaries and external stacks): `rules/generative/frontend/jwebmp/typescript/dependency-map.md`

Trust boundary (observed)
- Inputs: annotated host classes. Processing happens inside trusted library/Guice/Vert.x workers using ClassGraph.
- Outputs: generated TypeScript is a build artifact for the Angular toolchain; do not treat it as runtime input.
- Logging: Log4j2 inside the library; no external network calls are present.

These diagrams are referenced by PACT/GLOSSARY/RULES/GUIDES/IMPLEMENTATION for traceability.
