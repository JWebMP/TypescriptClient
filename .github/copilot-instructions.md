# Copilot Workspace Instructions

- Load host `RULES.md` and enterprise `rules/RULES.md` (behavioral, technical, modularity, forward-only sections) before suggesting code.
- Respect docs-first workflow: Stage 1–3 are documentation; Stage 4 code/scaffolding only. Blanket approvals may skip waits but must be recorded.
- Apply Java 25 + Maven stack; Angular 20/TypeScript for outputs; JWebMP Core/Client and GuicedEE Client lifecycle.
- Fluent API strategy: CRTP only; do not generate Lombok builders. Logging uses Log4j2 via Lombok `@Log4j2` when needed.
- Keep project docs outside `rules/`; maintain links among PACT, GLOSSARY, RULES, GUIDES, IMPLEMENTATION, and `docs/architecture/` diagrams.
- Forward-only edits: replace outdated docs instead of keeping stubs; do not revert user changes unless requested.
