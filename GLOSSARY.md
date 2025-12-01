# GLOSSARY — Topic-First Index

Use this file as the host index; topic glossaries override root definitions. Follow Glossary Precedence Policy: topic glossaries > root terms; duplicate only enforced prompt-language mappings.

## Topic Glossaries
- JWebMP Typescript Client (canonical): `rules/generative/frontend/jwebmp/typescript/GLOSSARY.md`
- Enterprise base: `rules/GLOSSARY.md`
- Java 25/Maven: `rules/generative/language/java/java-25.rules.md`
- TypeScript/Angular: `rules/generative/language/typescript/GLOSSARY.md`, `rules/generative/language/angular/GLOSSARY.md`, override: `rules/generative/language/angular/angular-20.rules.md`
- Fluent API (CRTP): `rules/generative/backend/fluent-api/GLOSSARY.md`
- JWebMP Client: `rules/generative/frontend/jwebmp/client/GLOSSARY.md`
- GuicedEE Client: `rules/generative/backend/guicedee/client/GLOSSARY.md`
- Logging: align with Log4j2 guidance in `rules/RULES.md`

## Host Terms (kept minimal)
- CRTP: Fluent API pattern returning `(J) this` for chained setters; mandated over builders in this project.
- Log4j2: Default logging backend; when Lombok is used, prefer `@Log4j2` annotation.
- Annotation Scan: ClassGraph-driven scan triggered by Guice post-startup (`AngularTypeScriptPostStartup`) to gather Ng* annotations for TypeScript generation.
- Angular Metadata: Ng* annotations (components, directives, data services, models, signals) that feed configuration renderers in `com.jwebmp.core.base.angular.client.services`.

## Prompt Language Alignment
- WebAwesome terms are not selected for this project. Use Angular/JWebMP terminology from the topic glossaries.
