# ERD — Annotation to Configuration Flow

```mermaid
erDiagram
    AnnotationsMap ||--|{ NgAnnotation : enumerates
    NgAnnotation ||--o{ ClassAnnotationMapping : captured_as
    ClassAnnotationMapping ||--|{ ComponentConfiguration : populates
    ClassAnnotationMapping ||--|{ AbstractNgConfiguration : populates
    NgComponent ||--|| ComponentConfiguration : config_for
    NgDirective ||--|| AbstractNgConfiguration : config_for
    ComponentConfiguration ||--o{ RenderedSnippet : emits
    AbstractNgConfiguration ||--o{ RenderedSnippet : emits
```

Entities are conceptual: NgAnnotation represents any Ng* annotation type, and RenderedSnippet represents the TypeScript strings produced by `render*()` methods.
