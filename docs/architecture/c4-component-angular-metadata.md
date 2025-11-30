# C4 Level 3 — Angular Metadata Pipeline

Focus: how Ng* annotations are gathered and turned into TypeScript metadata inside the library.

```mermaid
flowchart LR
    GuiceModule([AngularClientModule / GuicedConfig]) --> PostStartup[AngularTypeScriptPostStartup]
    PostStartup -->|executeBlocking on Vert.x| AnnotationHelper
    AnnotationHelper --> AnnotationsMap
    AnnotationHelper --> ClassAnnotationMapping
    ClassAnnotationMapping --> References[AbstractReferences subclasses]
    References --> Configs[ComponentConfiguration / AbstractNgConfiguration]
    AnnotationUtils -.normalizes.-> References
    Configs --> Renderers[Ng render helpers (fields/injects/hooks/imports)]
    Renderers --> TSMetadata[TypeScript metadata consumed by host build]
```

Components referenced above are present in `com.jwebmp.core.base.angular.client` packages and align with the Guice lifecycle defined in `module-info.java`.
