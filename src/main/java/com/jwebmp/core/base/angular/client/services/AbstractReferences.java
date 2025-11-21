package com.jwebmp.core.base.angular.client.services;

import com.google.common.base.Strings;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnInit;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils;
import com.jwebmp.core.base.angular.client.services.interfaces.IComponent;

public abstract class AbstractReferences<J extends AbstractNgConfiguration<?>>
{
    public abstract J getConfiguration();

    protected void processClass(Class<?> clazz, boolean checkForParent)
    {

        addComponentReferences(clazz, checkForParent);

        addFields(clazz, checkForParent);
        addMethods(clazz, checkForParent);
        addConstructorParameters(clazz, checkForParent);
        addConstructorBodies(clazz, checkForParent);
        addInjects(clazz, checkForParent);
        addInterfaces(clazz, checkForParent);
        addGlobalFields(clazz, checkForParent);
        addImportReferences(clazz, checkForParent);
        addOnInit(clazz, checkForParent);
        addOnDestroy(clazz, checkForParent);
    }

    protected void unwrapMethods(IComponent<?> comp, boolean checkForParent)
    {
        for (String componentMethod : comp.interfaces())
        {
            if (!Strings.isNullOrEmpty(componentMethod))
            {
                var ng = AnnotationUtils.getNgInterface(componentMethod);
                if ((ng.onSelf() && !checkForParent) || (ng.onParent() && checkForParent))
                {
                    getConfiguration().getInterfaces()
                                      .add(ng);
                }
            }
        }

        for (String componentConstructorParameter : comp.constructorParameters())
        {
            if (!Strings.isNullOrEmpty(componentConstructorParameter))
            {
                var ng = AnnotationUtils.getNgConstructorParameter(componentConstructorParameter,false,true,false);
                if ((ng.isOnSelf() && !checkForParent) || (ng.isOnParent() && checkForParent))
                {
                    getConfiguration().getConstructorParameters()
                                      .add(AnnotationUtils.getNgConstructorParameter(ng.value(),false,true,false));
                }
            }
        }
        for (String componentConstructorBody : comp.constructorBody())
        {
            if (!Strings.isNullOrEmpty(componentConstructorBody))
            {
                var ng = AnnotationUtils.getNgConstructorBody(componentConstructorBody,false,true);
                if ((ng.isOnSelf() && !checkForParent) || (ng.isOnParent() && checkForParent))
                {
                    getConfiguration().getConstructorBodies()
                                      .add(AnnotationUtils.getNgConstructorBody(ng.value(),false,true));
                }
            }
        }

        for (String componentMethod : comp.methods())
        {
            if (!Strings.isNullOrEmpty(componentMethod))
            {
                var ng = AnnotationUtils.getNgComponentMethod(componentMethod,false,true);
                if ((ng.isOnSelf() && !checkForParent) || (ng.isOnParent() && checkForParent))
                {
                    getConfiguration().getMethods()
                                      .add(ng);
                }
            }
        }

        for (String componentField : comp.globalFields())
        {
            if (!Strings.isNullOrEmpty(componentField))
            {
                var ng = AnnotationUtils.getNgGlobalField(componentField);
                if ((ng.isOnSelf() && !checkForParent) || (ng.isOnParent() && checkForParent))
                {
                    getConfiguration().getGlobalFields()
                                      .add(ng);
                }
            }
        }

        for (String componentField : comp.fields())
        {
            if (!Strings.isNullOrEmpty(componentField))
            {
                var ng = AnnotationUtils.getNgField(componentField,false,true);
                if ((ng.isOnSelf() && !checkForParent) || (ng.isOnParent() && checkForParent))
                {
                    getConfiguration().getFields()
                                      .add(ng);
                }
            }
        }

        for (String onInit : comp.onInit())
        {
            if (!Strings.isNullOrEmpty(onInit))
            {
                getConfiguration().getImportReferences()
                                  .add(AnnotationUtils.getNgImportReference("OnInit", "@angular/core"));
                getConfiguration().getInterfaces()
                                  .add(AnnotationUtils.getNgInterface("OnInit"));
                getConfiguration().getOnInit()
                                  .add(AnnotationUtils.getNgOnInit(onInit));
            }
        }
        for (String onDestroy : comp.onDestroy())
        {
            if (!Strings.isNullOrEmpty(onDestroy))
            {
                getConfiguration().getImportReferences()
                                  .add(AnnotationUtils.getNgImportReference("OnDestroy", "@angular/core"));
                getConfiguration().getInterfaces()
                                  .add(AnnotationUtils.getNgInterface("OnDestroy"));
                getConfiguration().getOnDestroy()
                                  .add(AnnotationUtils.getNgOnDestroy(onDestroy));
            }
        }
    }

    protected void addOnInit(Class<?> component, boolean checkForParent)
    {
        AnnotationUtils.getAnnotation(component, NgOnInit.class)
                       .forEach(ngMethod -> {
                           if ((ngMethod.onSelf() && !checkForParent) || (ngMethod.onParent() && checkForParent))
                           {
                               getConfiguration().getImportReferences()
                                                 .add(AnnotationUtils.getNgImportReference("OnInit", "@angular/core"));
                               getConfiguration().getInterfaces()
                                                 .add(AnnotationUtils.getNgInterface("OnInit"));
                               getConfiguration().getOnInit()
                                                 .add(AnnotationUtils.getNgOnInit(ngMethod.value()
                                                                                          .trim()));
                           }
                       });
    }

    protected void addOnDestroy(Class<?> component, boolean checkForParent)
    {
        AnnotationUtils.getAnnotation(component, NgOnDestroy.class)
                       .forEach(ngMethod -> {
                           if ((ngMethod.onSelf() && !checkForParent) || (ngMethod.onParent() && checkForParent))
                           {
                               getConfiguration().getImportReferences()
                                                 .add(AnnotationUtils.getNgImportReference("OnDestroy", "@angular/core"));
                               getConfiguration().getInterfaces()
                                                 .add(AnnotationUtils.getNgInterface("OnDestroy"));
                               getConfiguration().getOnDestroy()
                                                 .add(AnnotationUtils.getNgOnDestroy(ngMethod.value()));
                           }
                       });
    }

    protected void addConstructorParameters(Class<?> component, boolean checkForParent)
    {
        AnnotationUtils.getAnnotation(component, NgConstructorParameter.class)
                       .forEach(constructorParameter -> {
                           if ((constructorParameter.onSelf() && !checkForParent) || (constructorParameter.onParent() && checkForParent))
                           {
                               getConfiguration().getConstructorParameters()
                                                 .add(AnnotationUtils.getNgConstructorParameter(constructorParameter.value(),constructorParameter.onParent(),constructorParameter.onSelf(),constructorParameter.isPublic()));
                           }
                       });
    }

    protected void addConstructorBodies(Class<?> component, boolean checkForParent)
    {
        AnnotationUtils.getAnnotation(component, NgConstructorBody.class)
                       .forEach(ngConstructorBody -> {
                           if ((ngConstructorBody.onSelf() && !checkForParent) || (ngConstructorBody.onParent() && checkForParent))
                           {
                               getConfiguration().getConstructorBodies()
                                                 .add(AnnotationUtils.getNgConstructorBody(ngConstructorBody.value(),ngConstructorBody.onParent(),ngConstructorBody.onSelf()));
                           }
                       });
    }

    protected void addFields(Class<?> component, boolean checkForParent)
    {
        AnnotationUtils.getAnnotation(component, NgField.class)
                       .forEach(ngField -> {
                           if ((ngField.onSelf() && !checkForParent) || (ngField.onParent() && checkForParent))
                           {
                               getConfiguration().getFields()
                                                 .add(AnnotationUtils.getNgField(ngField.value(),ngField.onParent(),ngField.onSelf()));
                           }
                       });
    }

    protected void addComponentReferences(Class<?> component, boolean checkForParent)
    {
        AnnotationUtils.getAnnotation(component, NgComponentReference.class)
                       .forEach(ngField -> {
                           if ((ngField.onSelf() && !checkForParent) || (ngField.onParent() && checkForParent))
                           {
                               getConfiguration().getComponentReferences()
                                                 .add(AnnotationUtils.getNgComponentReference((Class<? extends IComponent<?>>) ngField.value()));
                           }
                       });
    }

    protected void addGlobalFields(Class<?> component, boolean checkForParent)
    {
        AnnotationUtils.getAnnotation(component, NgGlobalField.class)
                       .forEach(ngField -> {
                           if ((ngField.onSelf() && !checkForParent) || (ngField.onParent() && checkForParent))
                           {
                               getConfiguration().getGlobalFields()
                                                 .add(AnnotationUtils.getNgGlobalField(ngField.value()));
                           }
                       });
    }

    protected void addMethods(Class<?> component, boolean checkForParent)
    {
        AnnotationUtils.getAnnotation(component, NgMethod.class)
                       .forEach(ngMethod -> {
                           if ((ngMethod.onSelf() && !checkForParent) || (ngMethod.onParent() && checkForParent))
                           {
                               getConfiguration().getMethods()
                                                 .add(AnnotationUtils.getNgComponentMethod(ngMethod.value(),ngMethod.onParent(),ngMethod.onSelf()));
                           }
                       });
    }

    protected void addInterfaces(Class<?> component, boolean checkForParent)
    {
        AnnotationUtils.getAnnotation(component, NgInterface.class)
                       .forEach(ngMethod -> {
                           if ((ngMethod.onSelf() && !checkForParent) || (ngMethod.onParent() && checkForParent))
                           {
                               getConfiguration().getInterfaces()
                                                 .add(AnnotationUtils.getNgInterface(ngMethod.value()));
                           }
                       });
    }

    protected void addInjects(Class<?> component, boolean checkForParent)
    {
        AnnotationUtils.getAnnotation(component, NgInject.class)
                       .forEach(ngMethod -> {
                           if ((ngMethod.onSelf() && !checkForParent) || (ngMethod.onParent() && checkForParent))
                           {
                               getConfiguration().getInjects()
                                                 .add(AnnotationUtils.getNgInject(ngMethod.referenceName(), ngMethod.value()));
                           }
                       });
    }

    protected void addImportReferences(Class<?> component, boolean checkForParent)
    {
        AnnotationUtils.getAnnotation(component, NgImportReference.class)
                       .forEach(importReference -> {
                           if ((importReference.onSelf() && !checkForParent) || (importReference.onParent() && checkForParent))
                           {
                               getConfiguration().getImportReferences()
                                                 .add(
                                                         AnnotationUtils.getNgImportReference(importReference.value(), importReference.reference(),
                                                                 importReference.direct(), importReference.wrapValueInBraces()
                                                         )
                                                 );
                           }
                       });
    }
}
