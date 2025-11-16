package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.angular.NgModule;
import com.jwebmp.core.base.angular.client.annotations.angularconfig.NgPolyfill;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.typescript.TsDependency;
import com.jwebmp.core.base.angular.client.annotations.typescript.TsDevDependency;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@TsDependency(value = "@angular/common", version = "^19.2.0")
@TsDependency(value = "@angular/compiler", version = "^19.2.0")
@TsDependency(value = "@angular/core", version = "^19.2.0")
@TsDependency(value = "@angular/animations", version = "^19.2.0")
@TsDependency(value = "@angular/forms", version = "^19.2.0")
@TsDependency(value = "rxjs", version = "~7.8.0")
@TsDependency(value = "tslib", version = "^2.3.0")
@TsDependency(value = "zone.js", version = "~0.15.0")


@TsDependency(value = "zone.js", version = "~0.15.0")
@TsDependency(value = "uuid", version = "^11.1.0")


@TsDependency(value = "@angular/platform-browser", version = "^19.2.0")
@TsDependency(value = "@angular/platform-browser-dynamic", version = "^19.2.0")


@TsDevDependency(value = "@angular-devkit/build-angular", version = "^19.2.5")
@TsDevDependency(value = "@angular/cli", version = "^19.2.5")
@TsDevDependency(value = "@angular/compiler-cli", version = "^19.2.0")
//@TsDevDependency(value = "@types/node", version = "^18.18.0")
//@TsDevDependency(value = "@types/express", version = "~4.17.17")
//@TsDevDependency(value = "@types/jasmine", version = "~5.6.0")
@TsDevDependency(value = "typescript", version = "~5.7.2")
@TsDevDependency(value = "jasmine-core", version = "~5.6.0")

@TsDevDependency(value = "karma", version = "~6.4.0")
@TsDevDependency(value = "karma-chrome-launcher", version = "~3.2.0")
@TsDevDependency(value = "karma-coverage", version = "~2.2.0")
@TsDevDependency(value = "karma-jasmine", version = "~5.1.0")
@TsDevDependency(value = "karma-jasmine-html-reporter", version = "~2.1.0")


@TsDependency(value = "@stomp/stompjs", version = "^7.0.0")


@NgImportReference(value = "NgModule", reference = "@angular/core")

@NgPolyfill("zone.js")

@NgModule
public interface INgModule<J extends INgModule<J>>
        extends IComponent<J>
{
    String moduleString = "@NgModule({\n" +
            "\timports:[%s],\n" +
            "\tdeclarations:[%s],\n" +
            "\tproviders: [%s],\n" +
            "\texports:[%s],\n" +
            "\tbootstrap:%s,\n" +
            "\tschemas:[%s]\n" +
            "})";


    default List<String> declarations()
    {
        return new ArrayList<>();
    }

    @Override
    default List<String> decorators()
    {
        List<String> list = IComponent.super.decorators();
        StringBuilder declarations = new StringBuilder();
        StringBuilder providers = new StringBuilder();
        StringBuilder exports = new StringBuilder();
        StringBuilder bootstrap = new StringBuilder();
        StringBuilder schemas = new StringBuilder();
        StringBuilder entryComponents = new StringBuilder();

        declarations()
                .forEach(a -> {
                    declarations.append(a)
                            .append(",")
                            .append("\n");
                });

        if (declarations.length() > 1)
        {
            declarations.deleteCharAt(declarations.length() - 2);
        }

        providers()
                .forEach((key) -> {
                    providers.append(key)
                            .append(",")
                            .append("\n");
                });

        if (providers.length() > 1)
        {
            providers.deleteCharAt(providers.length() - 2);
        }


        exports()
                .forEach((key) -> {
                    exports.append(key)
                            .append(",")
                            .append("\n");
                });
        if (exports.length() > 1)
        {
            exports.deleteCharAt(exports.length() - 2);
        }

        bootstrap.append(bootstrap());

        schemas()
                .forEach((key) -> {
                    schemas.append(key)
                            .append(",")
                            .append("\n");
                });
        if (schemas.length() > 1)
        {
            schemas.deleteCharAt(schemas.length() - 2);
        }


        StringBuilder importNames = new StringBuilder();

        Arrays.stream(moduleImports()
                        .toArray())
                .forEach((key) -> {

                    importNames.append(key)
                            .append(",")
                            .append("\n");
                });

        if (importNames.length() > 1)
        {
            importNames.deleteCharAt(importNames.length() - 2);
        }

        Arrays.stream(entryComponents()
                        .toArray())
                .forEach((key) -> {

                    entryComponents.append(key)
                            .append(",")
                            .append("\n");
                });

        if (entryComponents.length() > 1)
        {
            entryComponents.deleteCharAt(entryComponents.length() - 2);
        }

        String componentString = String.format(moduleString, importNames, declarations, providers, exports, bootstrap, schemas);
        list.add(componentString);
        return list;
    }

    default List<String> providers()
    {
        return new ArrayList<>();
    }

    default List<String> bootstrap()
    {
        return new ArrayList<>();
    }

    default List<String> assets()
    {
        return new ArrayList<>();
    }

    default List<String> exports()
    {
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    default J setApp(INgApp<?> app)
    {
        return (J) this;
    }

    default List<String> schemas()
    {
        return new ArrayList<>();
    }

    default List<String> entryComponents()
    {
        return new ArrayList<>();
    }
}
