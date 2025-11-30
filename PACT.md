---
version: 2.0
date: 2025-12-01
title: The Human–AI Collaboration Pact
project: JWebMP / Typescript Client Library
authors: [Maintainers, Architects, AI Assistants]
---

# 🤝 Pact (Developer Edition)

## 1. Purpose
This pact aligns humans and AI on how we evolve the JWebMP Typescript Client Library. The goal is intentional collaboration — documenting first, then implementing — while keeping continuity across PACT ↔ RULES ↔ GUIDES ↔ IMPLEMENTATION.

## 2. Principles
- Continuity: carry context across runs; forward-only changes (no backports).
- Finesse: precise, traceable docs that mirror the code we see on disk.
- Non-Transactional Flow: collaborate iteratively, not Q&A.
- Closing Loops: every artifact links back to its source and forward to its dependents.

## 3. Structure of Work
| Layer | Description | Artifact |
| --- | --- | --- |
| Pact | Collaboration culture and constraints | `PACT.md` |
| Rules | Conventions and stack selections | `RULES.md` |
| Guides | How to apply rules in this repo | `GUIDES.md` |
| Implementation | Current state and deltas | `IMPLEMENTATION.md` |

## 4. Behavioral Agreements
- Language: clear, narrative-technical; no invented architecture.
- Reflection: surface unknowns; prefer questions over assumptions.
- Tone: friendly and direct; avoid boilerplate AI phrasing.
- Iteration: small, reviewable changes; document before code.
- Transparency: declare constraints (Java 25, CRTP, Log4j2) and stage gates.

## 5. Developer Culture
- Tool literacy: Maven + Java 25 + JWebMP/GuicedEE, Angular 20 TS generation.
- Traceability: diagram + doc + code alignment, stored in version control.
- Diagramming: Mermaid-first, linked from `docs/architecture/`.

## 6. Technical Commitments
- Honor RULES.md sections (behavioral, technical, modularity, forward-only) from `rules/RULES.md`.
- Respect selected stacks: Java 25 LTS, Maven, Angular 20 (TypeScript), GuicedEE Client, JWebMP Core/Client, CRTP fluent style, Lombok with `@Log4j2`, JSpecify.
- Logging defaults to Log4j2; Lombok logging uses `@Log4j2` only.
- No project docs live inside the `rules/` submodule; host docs stay at repo root or `docs/`.
- Stage gates: blanket approval granted for this run; STOPs are auto-approved but still recorded.

## 7. Shared Goals
- Document the existing annotation-driven Angular TS generation pipeline clearly.
- Keep glossary/topic precedence explicit for AI usage.
- Close loops across PACT ↔ GLOSSARY ↔ RULES ↔ GUIDES ↔ IMPLEMENTATION ↔ diagrams.

## 8. Links
- Rules Repository submodule: `rules/`
- RULES: `RULES.md`
- GUIDES: `GUIDES.md`
- IMPLEMENTATION: `IMPLEMENTATION.md`
- Architecture index: `docs/architecture/README.md`
