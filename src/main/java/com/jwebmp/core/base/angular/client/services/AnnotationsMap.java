package com.jwebmp.core.base.angular.client.services;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.angular.*;
import com.jwebmp.core.base.angular.client.annotations.angularconfig.*;
import com.jwebmp.core.base.angular.client.annotations.boot.*;
import com.jwebmp.core.base.angular.client.annotations.components.*;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBodys;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameters;
import com.jwebmp.core.base.angular.client.annotations.functions.*;
import com.jwebmp.core.base.angular.client.annotations.globals.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.routing.NgRoutable;
import com.jwebmp.core.base.angular.client.annotations.routing.NgRouteData;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import lombok.extern.java.Log;

import java.lang.annotation.Annotation;
import java.util.*;

@Log
public class AnnotationsMap
{
/*
    private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngConfigs
            = Map.of(
            NgAsset.class, NgAssets.class,
            NgScript.class, NgScripts.class,
            NgStyleSheet.class, NgStyleSheets.class,
            NgPolyfill.class, NgPolyfills.class
    );
*/

    private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngEvents
            = Map.of(
            NgOnDestroy.class, NgOnDestroys.class,
            NgOnInit.class, NgOnInits.class,
            NgAfterViewInit.class, NgAfterViewInits.class,
            NgAfterViewChecked.class, NgAfterViewCheckeds.class,
            NgAfterContentInit.class, NgAfterContentInits.class,
            NgAfterContentChecked.class, NgAfterContentCheckeds.class
    );

    private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngComponents
            = Map.of(
            NgInput.class, NgInputs.class,
            NgComponentTagAttribute.class, NgComponentTagAttributes.class,
            NgOutput.class, NgOutputs.class,
            NgGlobalComponentConstructorParameter.class, NgGlobalComponentConstructorParameters.class,
            NgGlobalConstructorParameter.class, NgGlobalConstructorParameters.class,
            NgGlobalComponentImportReference.class, NgGlobalComponentImportReferences.class
    );

    private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngGlobals
            = Map.of(
            NgGlobalComponentConstructorParameter.class, NgGlobalComponentConstructorParameters.class,
            NgGlobalConstructorParameter.class, NgGlobalConstructorParameters.class,
            NgGlobalComponentImportReference.class, NgGlobalComponentImportReferences.class,
            NgAsset.class, NgAssets.class,
            NgScript.class, NgScripts.class,
            NgStyleSheet.class, NgStyleSheets.class,
            NgPolyfill.class, NgPolyfills.class
    );

    private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngBootConfigs
            = Map.of(
            NgBootDeclaration.class, NgBootDeclarations.class,
            NgBootModuleImport.class, NgBootModuleImports.class,
            NgBootImportReference.class, NgBootImportReferences.class,
            NgBootImportProvider.class, NgBootImportProviders.class,
            NgBootProvider.class, NgBootProviders.class,
            NgBootConstructorBody.class, NgBootConstructorBodys.class,
            NgBootConstructorParameter.class, NgBootConstructorParameters.class,
            NgBootGlobalField.class, NgBootGlobalFields.class,
            NgBootModuleSchema.class, NgBootModuleSchemas.class
    );

    private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngReferences
            = Map.of(
            NgComponentReference.class, NgComponentReferences.class,
            NgImportProvider.class, NgImportProviders.class,
            NgImportReference.class, NgImportReferences.class,
            NgDataTypeReference.class, NgDataTypeReferences.class
    );
    private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngClassStructures
            = Map.of(
            NgConstructorBody.class, NgConstructorBodys.class,
            NgConstructorParameter.class, NgConstructorParameters.class,
            NgField.class, NgFields.class,
            NgInterface.class, NgInterfaces.class,
            NgMethod.class, NgMethods.class,
            NgValidator.class, NgValidators.class
    );

    public static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngAllMultiples = new HashMap<>();
    public static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngAllGlobals = new HashMap<>();

    static
    {
        //   ngAllMultiples.putAll(ngConfigs);
        ngAllMultiples.putAll(ngEvents);
        ngAllMultiples.putAll(ngReferences);
        ngAllMultiples.putAll(ngClassStructures);
        ngAllMultiples.putAll(ngComponents);

        ngAllGlobals.putAll(ngGlobals);
        ngAllGlobals.putAll(ngBootConfigs);
    }

    public static final List<Class<? extends Annotation>> annotations
            = List.of(
            NgAsset.class,
            NgScript.class,
            NgStyleSheet.class,
            NgPolyfill.class,
            NgOnDestroy.class,
            NgBootDeclaration.class,
            NgBootModuleImport.class,
            NgBootModuleSchema.class,
            NgBootImportReference.class,
            NgBootProvider.class,
            NgBootConstructorParameter.class,
            NgBootConstructorBody.class,
            NgBootGlobalField.class,
            NgComponentReference.class,
            NgImportProvider.class,
            NgImportReference.class,
            NgConstructorBody.class,
            NgConstructorParameter.class,
            NgField.class,
            NgInterface.class,
            NgMethod.class,
            NgApp.class,
            NgComponent.class,
            NgDataService.class,
            NgDataType.class,
            NgDataTypeReference.class,
            NgDirective.class,
            NgModule.class,
            NgProvider.class,
            NgServiceProvider.class,
            NgRoutable.class,
            NgRouteData.class,

            NgOnInit.class,
            NgOnDestroy.class,
            NgAfterViewInit.class,
            NgAfterViewChecked.class,
            NgAfterContentInit.class,
            NgAfterContentChecked.class,

            NgInput.class,
            NgComponentTagAttribute.class,
            NgOutput.class,

            NgGlobalConstructorParameter.class,
            NgGlobalComponentConstructorParameter.class,
            NgGlobalComponentImportReference.class,

            NgValidator.class
    );


    public static Set<Class<?>> loadAllClasses()
    {
        Set<Class<?>> allAffectedClasses = new HashSet<>();
        ScanResult scanResult = IGuiceContext.instance()
                                             .getScanResult();
        for (Class<? extends Annotation> annotation : annotations)
        {
            for (ClassInfo allClass : scanResult
                    .getClassesWithAnnotation(annotation))
            {
                allAffectedClasses.add(allClass.loadClass());
            }
        }
        for (Class<? extends Annotation> annotation : ngAllMultiples.values())
        {
            for (ClassInfo allClass : scanResult
                    .getClassesWithAnnotation(annotation))
            {
                allAffectedClasses.add(allClass.loadClass());
            }
        }
        return allAffectedClasses;
    }

}
