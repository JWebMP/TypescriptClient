# Architecture Index

All diagrams are text-first (Mermaid) and map to observed code in this repository.

- C4 Level 1 (Context): `docs/architecture/c4-context.md`
- C4 Level 2 (Container): `docs/architecture/c4-container.md`
- C4 Level 3 (Angular metadata pipeline): `docs/architecture/c4-component-angular-metadata.md`
- Sequence — startup scan: `docs/architecture/sequence-startup-scan.md`
- Sequence — component config build: `docs/architecture/sequence-component-config.md`
- ERD — annotation to configuration: `docs/architecture/erd-angular-metadata.md`

Trust boundary (observed): Host app inputs (annotated components) cross into this library’s scan/render pipeline; Guice/Vert.x execution is trusted infrastructure; generated TypeScript is consumed by downstream builds and should not execute unvalidated runtime inputs.

These diagrams are referenced by PACT/GLOSSARY/RULES/GUIDES/IMPLEMENTATION for traceability.
