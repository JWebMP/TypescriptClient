# Angular TypeScript Client

[![Build](https://github.com/JWebMP/Plugins/TypescriptClient/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/JWebMP/Plugins/actions/workflows/maven-package.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.jwebmp.plugins/typescript-client)](https://central.sonatype.com/artifact/com.jwebmp.plugins/typescript-client)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue)](https://www.apache.org/licenses/LICENSE-2.0)

![Java 25+](https://img.shields.io/badge/Java-25%2B-green)
![Maven 4](https://img.shields.io/badge/Maven-4%2B-green)

A Java annotation library that lets JWebMP components describe **Angular TypeScript** metadata entirely in Java. Annotate your classes with `@NgComponent`, `@NgDataService`, `@NgDirective`, and friends — the generator produces ready-to-compile `.ts` files, Angular modules, routing configs, and STOMP-backed event-bus services without you ever editing TypeScript by hand.

## ✨ Features

- **Ng\* annotation family** — `@NgApp`, `@NgComponent`, `@NgModule`, `@NgDirective`, `@NgDataService`, `@NgDataType`, `@NgServiceProvider`, `@NgProvider`, `@NgValidator`, `@NgSchema`
- **Component metadata** — `@NgInput`, `@NgOutput`, `@NgComponentTagAttribute` for Angular component I/O
- **Lifecycle hooks** — `@NgOnInit`, `@NgOnDestroy`, `@NgAfterViewInit`, `@NgAfterViewChecked`, `@NgAfterContentInit`, `@NgAfterContentChecked`
- **Structural code generation** — `@NgField`, `@NgMethod`, `@NgConstructorParameter`, `@NgConstructorBody`, `@NgInterface`, `@NgSignal`, `@NgSignalComputed`, `@NgSignalEffect`, `@NgModel`, `@NgInject`
- **Import management** — `@NgImportReference`, `@NgImportModule`, `@NgImportProvider`, `@NgComponentReference`, `@NgDataTypeReference`, `@NgIgnoreImportReference`, `@NgIgnoreRender`
- **Global annotations** — `@NgGlobalField`, `@NgGlobalConstructorParameter`, `@NgGlobalComponentConstructorParameter`, `@NgGlobalComponentImportReference` for cross-cutting concerns
- **Routing** — `@NgRoutable` (path, redirectTo, pathMatch, parent, sortOrder) and `@NgRouteData`
- **Boot-module annotations** — `@NgBootDeclaration`, `@NgBootModuleImport`, `@NgBootImportReference`, `@NgBootEntryComponent`, `@NgBootImportProvider`, `@NgBootProvider`, `@NgBootConstructorBody`, `@NgBootConstructorParameter`, `@NgBootGlobalField`, `@NgBootModuleSchema`
- **Angular CLI config** — `@NgAsset`, `@NgScript`, `@NgStyleSheet`, `@NgPolyfill` for `angular.json` integration
- **TypeScript dependencies** — `@TsDependency`, `@TsDevDependency`, `@NgSourceDirectoryReference`
- **CRTP fluent interfaces** — `INgApp`, `INgComponent`, `INgDirective`, `INgDataService`, `INgDataType`, `INgModule`, `INgProvider`, `INgServiceProvider`, `INgRoutable`, `INgValidatorDirective`, `INgFormControlValidatorFunction`, `INgFormGroupValidatorFunction`
- **SPI extension points** — `OnGetAllConstructorParameters`, `OnGetAllConstructorBodies`, `OnGetAllFields`, `OnGetAllImports`, `OnGetAllMethods`, `OnGetAllModuleImports`
- **Built-in event bus** — `EventBusService` (STOMP over WebSocket) with auto-reconnect, listener registration, and RxJS observables
- **TypeScript type helpers** — `any`, `bool`, `number`, `string` placeholder classes for type mapping
- **Java → TS field mapping** — `INgDataType` introspects Java fields (including generics, enums, dates, optionals) and emits TypeScript interface fields with `jakarta.validation` awareness
- **Classpath scanning** — `AnnotationHelper` scans the hierarchy of every Ng-annotated class at startup via `AngularTypeScriptPostStartup` (reactive `Uni`-based)
- **Output directory control** — `jwebmp.outputDirectory` env/system property overrides the default `~/.jwebmp/<appName>` output location

## 📦 Installation

```xml
<dependency>
  <groupId>com.jwebmp.plugins</groupId>
  <artifactId>typescript-client</artifactId>
</dependency>
```

> Version is managed by the `com.jwebmp:jwebmp-bom` imported in the parent POM.

## 🗺️ JPMS Module

```
module com.jwebmp.core.base.angular.client {
    requires transitive com.guicedee.client;
    requires transitive com.jwebmp.client;
    requires transitive com.guicedee.guicedinjection;
    requires transitive com.guicedee.jsonrepresentation;
    requires transitive org.apache.commons.lang3;
    requires transitive org.apache.commons.io;
    requires transitive jakarta.validation;

    exports com.jwebmp.core.base.angular.client.annotations.angular;
    exports com.jwebmp.core.base.angular.client.annotations.components;
    exports com.jwebmp.core.base.angular.client.annotations.functions;
    exports com.jwebmp.core.base.angular.client.annotations.globals;
    exports com.jwebmp.core.base.angular.client.annotations.references;
    exports com.jwebmp.core.base.angular.client.annotations.constructors;
    exports com.jwebmp.core.base.angular.client.annotations.angularconfig;
    exports com.jwebmp.core.base.angular.client.annotations.structures;
    exports com.jwebmp.core.base.angular.client.annotations.routing;
    exports com.jwebmp.core.base.angular.client.annotations.typescript;
    exports com.jwebmp.core.base.angular.client.annotations.boot;
    exports com.jwebmp.core.base.angular.client;
    exports com.jwebmp.core.base.angular.client.services;
    exports com.jwebmp.core.base.angular.client.services.interfaces;
    exports com.jwebmp.core.base.angular.client.services.spi;
    exports com.jwebmp.core.base.angular.client.services.tstypes;

    provides IGuiceScanModuleInclusions with AngularTypeScriptClientModuleInclusion;
    provides IGuiceModule             with AngularClientModule;
    provides IGuiceConfigurator        with GuicedConfig;
    provides IGuicePostStartup         with AngularTypeScriptPostStartup;

    uses OnGetAllConstructorParameters;
    uses OnGetAllConstructorBodies;
    uses OnGetAllFields;
    uses OnGetAllImports;
    uses OnGetAllMethods;
    uses OnGetAllModuleImports;
}
```

## 🚀 Quick Start

### 1. Define an Angular app

```java
@NgApp(value = "my-app", bootComponent = AppComponent.class)
public class MyApp extends Page<MyApp> implements INgApp<MyApp> {
    @Override
    public List<IComponentHierarchyBase<?, ?>> getRoutes() {
        return List.of();
    }
}
```

### 2. Create a component

```java
@NgComponent("app-header")
public class HeaderComponent extends DivSimple<HeaderComponent>
        implements INgComponent<HeaderComponent> { }
```

### 3. Declare a data type (generates a TypeScript interface)

```java
@NgDataType
public class UserDTO implements INgDataType<UserDTO> {
    private String name;
    private String email;
    private int age;
}
```

### 4. Wire a data service

```java
@NgDataService(value = "userData", listenerName = "user.data")
public class UserDataService implements INgDataService<UserDataService> { }
```

### 5. Add routing

```java
@NgRoutable(path = "dashboard", parent = {AppComponent.class})
@NgComponent("app-dashboard")
public class DashboardComponent extends DivSimple<DashboardComponent>
        implements INgComponent<DashboardComponent> { }
```

### 6. Run the build

```bash
mvn clean install
```

TypeScript output is written to `~/.jwebmp/<appName>/` (override with `jwebmp.outputDirectory`).

## 🏗️ Architecture

### Annotation Categories

| Category | Annotations | Purpose |
|---|---|---|
| **Angular types** | `@NgApp`, `@NgComponent`, `@NgModule`, `@NgDirective`, `@NgDataService`, `@NgDataType`, `@NgServiceProvider`, `@NgProvider`, `@NgValidator`, `@NgSchema` | Declare Angular constructs |
| **Component I/O** | `@NgInput`, `@NgOutput`, `@NgComponentTagAttribute` | Component inputs, outputs, and host attributes |
| **Lifecycle** | `@NgOnInit`, `@NgOnDestroy`, `@NgAfterViewInit`, `@NgAfterViewChecked`, `@NgAfterContentInit`, `@NgAfterContentChecked` | Angular lifecycle hook bodies |
| **Structure** | `@NgField`, `@NgMethod`, `@NgConstructorParameter`, `@NgConstructorBody`, `@NgInterface`, `@NgSignal`, `@NgSignalComputed`, `@NgSignalEffect`, `@NgModel`, `@NgInject` | TypeScript class members |
| **References** | `@NgImportReference`, `@NgImportModule`, `@NgImportProvider`, `@NgComponentReference`, `@NgDataTypeReference`, `@NgIgnoreImportReference`, `@NgIgnoreRender` | Import/export management |
| **Globals** | `@NgGlobalField`, `@NgGlobalConstructorParameter`, `@NgGlobalComponentConstructorParameter`, `@NgGlobalComponentImportReference` | Cross-cutting members applied to all components |
| **Boot module** | `@NgBootDeclaration`, `@NgBootModuleImport`, `@NgBootImportReference`, `@NgBootEntryComponent`, `@NgBootImportProvider`, `@NgBootProvider`, `@NgBootConstructorBody`, `@NgBootConstructorParameter`, `@NgBootGlobalField`, `@NgBootModuleSchema` | Root Angular module configuration |
| **Angular CLI** | `@NgAsset`, `@NgScript`, `@NgStyleSheet`, `@NgPolyfill` | `angular.json` assets/scripts/styles/polyfills |
| **TypeScript** | `@TsDependency`, `@TsDevDependency`, `@NgSourceDirectoryReference` | `package.json` and source directory references |
| **Routing** | `@NgRoutable`, `@NgRouteData` | Angular route declarations |

### CRTP Interface Hierarchy

```
IComponent<J>
 ├── INgApp<J>              → @NgApp
 ├── INgComponent<J>        → @NgComponent (selector, template, styles, standalone)
 ├── INgDirective<J>        → @NgDirective (selector, inputs, outputs, standalone)
 ├── INgDataService<J>      → @NgDataService (signal-based, event-bus listener)
 ├── INgDataType<J>         → @NgDataType (Java fields → TS interface fields)
 ├── INgModule<J>           → @NgModule
 ├── INgProvider<J>         → @NgProvider (injectable service)
 ├── INgServiceProvider<J>  → @NgServiceProvider (data service + data type bridge)
 └── INgRoutable<J>         → @NgRoutable (route path, parent, guards)
```

### Startup Flow

```
IGuiceContext.instance()
 └─ GuicedConfig                          → enables annotation/classpath/field/method scanning
     └─ AngularClientModule               → binds AnnotationHelper singleton
         └─ AngularTypeScriptPostStartup  → scans all Ng-annotated classes via AnnotationHelper.startup()
```

### Key Classes

| Class | Role |
|---|---|
| `AnnotationHelper` | Singleton that scans class hierarchies, collects all Ng annotations (including globals and repeatables), and caches `ClassAnnotationMapping` results |
| `AnnotationsMap` | Central registry mapping single → repeatable annotation pairs, global annotations, and the set of classes to scan |
| `AppUtils` | File-system utilities — resolves output directories, reads/writes generated TS files, locates `angular.json`, `package.json`, `tsconfig.json`, etc. |
| `EventBusService` | Built-in Angular provider that manages a STOMP WebSocket connection with auto-reconnect, listener registration, and RxJS subjects |
| `DynamicData` | Generic wrapper for sending heterogeneous data payloads through `INgDataService` |
| `ComponentConfiguration` | Runtime configuration holder for a single component's generated metadata |
| `TypescriptIndexPageConfigurator` | SPI for customising the generated `index.html` |

### SPI Extension Points

| SPI | Purpose |
|---|---|
| `OnGetAllConstructorParameters` | Add/modify constructor parameters for any Ng component |
| `OnGetAllConstructorBodies` | Add/modify constructor body statements |
| `OnGetAllFields` | Add/modify class-level fields |
| `OnGetAllImports` | Add/modify import references |
| `OnGetAllMethods` | Add/modify class methods |
| `OnGetAllModuleImports` | Add/modify NgModule import entries |

## ⚙️ Configuration

| Environment / System Property | Default | Purpose |
|---|---|---|
| `jwebmp.outputDirectory` | — | Override the generated TS output directory |
| `jwebmp` | `~` (user home) | Base directory when `jwebmp.outputDirectory` is not set (output goes to `<base>/.jwebmp/<appName>`) |

> **Important:** Generated TypeScript is read-only. Change Java annotations and rerun the build — never edit the output files directly.

## 🔗 Dependencies

```
com.jwebmp.core.base.angular.client
 ├── com.jwebmp.client              (JWebMP client library)
 ├── com.guicedee.client             (GuicedEE lifecycle & SPI)
 ├── com.guicedee.guicedinjection    (Guice DI + classpath scanning)
 ├── com.guicedee.jsonrepresentation (Jackson / JSON utilities)
 ├── org.apache.commons.lang3        (StringUtils, etc.)
 ├── org.apache.commons.io           (FileUtils, IOUtils)
 ├── jakarta.validation              (@NotNull awareness in TS field generation)
 └── org.apache.logging.log4j.core   (Log4j2 logging)
```

## 🛠️ Build

- **Java**: 25 LTS
- **Maven**: inherits `com.jwebmp:parent:2.0.0-RC6`
- **JPMS**: module descriptor at `src/main/java/module-info.java`

```bash
mvn clean install
```

Run tests:

```bash
mvn test
```

## 🔄 CI

GitHub Actions workflow at `.github/workflows/maven-package.yml` (GuicedEE shared workflow).

Required secrets: `USERNAME`, `USER_TOKEN`, `SONA_USERNAME`, `SONA_PASSWORD`.

Example environment file: `.env.example`.

## 📖 Documentation

| Document | Path |
|---|---|
| Architecture diagrams | `docs/architecture/README.md` |

## 🤝 Contributing

Issues and pull requests are welcome. Please add tests (using `jwebmp-testlib`) for new annotations, SPI hooks, or rendering logic.

## 📄 License

[Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0)
