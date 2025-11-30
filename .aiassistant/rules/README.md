# AI Assistant Rules (Pinned)

Follow host `RULES.md` and enterprise `rules/RULES.md` sections 4/5 plus Document Modularity and Forward-Only policies.

- Docs-first: complete Stage 1–3 docs before code (Stage 4) unless blanket approval is recorded.
- Forward-only: replace outdated docs; do not resurrect legacy patterns.
- Modularity: keep project docs outside `rules/`; cross-link PACT ↔ GLOSSARY ↔ RULES ↔ GUIDES ↔ IMPLEMENTATION ↔ `docs/architecture/`.
- Logging: Log4j2 default; Lombok logging uses `@Log4j2`.
- Fluent API: CRTP enforced; avoid Lombok builders.
- Java 25 LTS + Maven; Angular 20/TypeScript; JWebMP Core/Client; GuicedEE Client lifecycle.
