# GUIDES — Application of Rules (Stage 2)

Purpose: how to apply the selected rules to the observed codebase.

## Angular TypeScript metadata workflow
1) Annotate JWebMP components/services with Ng* annotations under `com.jwebmp.core.base.angular.client.annotations.*` and implement the relevant `INg*` interfaces for scaffolding strings (`interfaces` package).
2) Ensure module registration stays intact (`AngularClientModule`, `AngularTypeScriptClientModuleInclusion` in `module-info.java`) so Guice/Vert.x trigger `AnnotationHelper.startup()` and ClassGraph scanning.
3) Build metadata via `AbstractReferences.processClass(...)` (or higher-level generator) to populate `ComponentConfiguration`/`AbstractNgConfiguration`; use `splitComponentReferences()` before rendering to expand imports/injects.
4) Render TypeScript snippets with the `render*()` helpers (fields, hooks, injects, interfaces, imports) and feed them into the host Angular build chain.
5) Keep glossary routing intact for Angular 20: base rules `rules/generative/language/angular/README.md` + override `rules/generative/language/angular/angular-20.rules.md`; TypeScript base `rules/generative/language/typescript/README.md`.

## API surface sketch (observed)
- SPI/Interfaces: `INgComponent`, `INgDirective`, `INgDataService`, `INgProvider`, `TypescriptIndexPageConfigurator`; support arrays of strings that are converted to Ng* annotations by `AnnotationUtils`.
- Lifecycle hooks: `AngularTypeScriptPostStartup` (IGuicePostStartup) runs `AnnotationHelper.startup()` on Vert.x; `GuicedConfig` sets scan flags (annotation/classpath/field/method info).
- Configuration builders: `ComponentConfiguration` aggregates Ng* signals, models, methods, fields, inputs/outputs, imports/providers; `AbstractNgConfiguration` handles routing for directives/services/providers.
- Rendering contract: `renderOnInit|renderOnDestroy|renderInjects|renderInterfaces|renderFields|renderGlobalFields|renderSignals|renderModels` return TypeScript code fragments; consumers must compose them into `.ts` files.

## Design validation & acceptance criteria
- Annotation scan completes at startup without blocking Guice (executed via Vert.x `executeBlocking`); failures are logged via Log4j2.
- For a sample annotated class, `getClassMappings` returns Ng* entries matching the annotations present and their inheritance rules (parent/self flags respected).
- `splitComponentReferences()` resolves relative import paths and inject tokens for referenced components/services/providers.
- Rendered TypeScript includes required imports/interfaces for hooks used (`OnInit`, `OnDestroy`, etc.) and maintains CRTP-friendly naming in generated code.

## Test & verification strategy
- Unit: `AnnotationHelperTest` seeds fixtures; extend with assertions for Ng* lists, import resolution, and render outputs (no file system writes required).
- Integration/lightweight: instantiate `AngularTypeScriptPostStartup` with a Vert.x test instance to ensure futures resolve and maps populate.
- Static checks: enforce module boundary (exports/opens) and Lombok/Log4j2 alignment; consider adding nullness checks using JSpecify defaults.
- CI: run Maven unit tests and formatter/lint steps via GitHub Actions shared workflow (see RULES.md links).

## Migration notes
- Existing README is a stub; host docs now live in PACT/RULES/GUIDES/IMPLEMENTATION and `docs/architecture/`.
- No legacy monolithic docs are retained; forward-only policy applies to future rewrites.
