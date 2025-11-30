# IMPLEMENTATION — Current State (Stage 1)

This repository is a Maven Java 25 library providing annotations and helpers to generate Angular 20 TypeScript artifacts for JWebMP components.

## Modules & Layout
- `module-info.java`: declares module `com.jwebmp.core.base.angular.client`, exports Ng annotation packages, provides Guice bindings (`AngularClientModule`, `AngularTypeScriptPostStartup`, `AngularTypeScriptClientModuleInclusion`, `GuicedConfig`), and opens packages for Guice/Jackson.
- `com.jwebmp.core.base.angular.client.annotations.*`: Ng* annotations for Angular components, directives, routing, structures, constructors, bootstrapping, and TypeScript dependencies.
- `com.jwebmp.core.base.angular.client.services`: runtime helpers for scanning annotations and building renderable TypeScript configuration (`AnnotationHelper`, `AnnotationsMap`, `AbstractReferences`, `ComponentConfiguration`, `AbstractNgConfiguration`, data/directive/service provider configs, TS type helpers under `tstypes`).
- `com.jwebmp.core.base.angular.client.services.interfaces`: SPI definitions for Ng components/services plus `AnnotationUtils` helpers.
- `implementations` package: Guice module registration and post-startup hook that triggers annotation scanning on Vert.x worker thread.
- Tests: `AnnotationHelperTest` exercises annotation scanning using sample annotation fixture classes.

## Runtime Behavior (observed)
- Startup: Guice config enables annotation/classpath scanning (`GuicedConfig`), module inclusion identifies this module for scan, and `AngularTypeScriptPostStartup` fires `AnnotationHelper.startup()` via Vert.x to populate mappings.
- Annotation processing: `AnnotationHelper` and `AbstractReferences` aggregate Ng* annotations across class hierarchies, normalizing imports/injects/interfaces and splitting component references for TypeScript rendering.

Diagrams and flow details live in `docs/architecture/`.

## Planned Changes (Stage 3 plan)
- Scaffolding: keep `rules/` as submodule; add README updates linking PACT/RULES/GUIDES/IMPLEMENTATION/GLOSSARY; ensure docs remain outside `rules/`.
- Env/config: add `.env.example` derived from `rules/generative/platform/secrets-config/env-variables.md` with only keys relevant to this library (logging level, environment, tracing toggle).
- CI: add `.github/workflows/maven-package.yml` referencing GuicedEE shared workflow; document required secrets.
- AI workspace: add `.aiassistant/rules/` summary and `.github/copilot-instructions.md` aligned to RULES and stage-gate policy.
- Observability/logging: document Log4j2 default and Lombok `@Log4j2` usage; no code changes until Stage 4 approval (blanket approval already recorded).
- Traceability: wire PACT ↔ GLOSSARY ↔ RULES ↔ GUIDES ↔ IMPLEMENTATION ↔ `docs/architecture/` and update README links.

## Rollout & Validation
- Validate docs-only changes via link checks and consistency across artifacts.
- After CI workflow is added, run Maven tests in CI and optionally locally (`mvn test`); ensure module boundaries remain intact.
- Risks: forward-only removal of legacy docs may confuse downstream consumers—mitigated by README links; shared workflow requires secrets (`USERNAME`, `USER_TOKEN`, `SONA_USERNAME`, `SONA_PASSWORD`) to be present in GitHub repo settings.
