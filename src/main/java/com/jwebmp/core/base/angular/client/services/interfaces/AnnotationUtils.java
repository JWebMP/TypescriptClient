package com.jwebmp.core.base.angular.client.services.interfaces;

import com.google.common.base.Strings;
import com.jwebmp.core.base.angular.client.annotations.angular.NgApp;
import com.jwebmp.core.base.angular.client.annotations.angular.NgComponent;
import com.jwebmp.core.base.angular.client.annotations.components.NgComponentTagAttribute;
import com.jwebmp.core.base.angular.client.annotations.components.NgInput;
import com.jwebmp.core.base.angular.client.annotations.components.NgOutput;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.annotations.structures.NgGlobalField;
import com.jwebmp.core.base.angular.client.annotations.structures.NgInterface;
import com.jwebmp.core.base.angular.client.annotations.structures.NgMethod;
import com.jwebmp.core.base.angular.client.annotations.typescript.NgSourceDirectoryReference;
import com.jwebmp.core.base.angular.client.services.any;
import com.jwebmp.core.databind.IConfiguration;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface AnnotationUtils
{

    static boolean hasAnnotation(Class<?> object, Class<? extends Annotation> annotation)
    {
        return checkAnnotation(object, annotation, new HashSet<>());
    }

    private static boolean checkAnnotation(Class<?> clazz, Class<? extends Annotation> annotation, Set<Class<?>> visited)
    {
        if (clazz == null || Object.class.equals(clazz) || visited.contains(clazz))
        {
            return false;
        }

        // Mark this class as visited
        visited.add(clazz);

        // Check if the current class has the annotation
        if (clazz.isAnnotationPresent(annotation))
        {
            return true;
        }

        // Check all implemented interfaces recursively
        for (Class<?> iface : clazz.getInterfaces())
        {
            if (checkAnnotation(iface, annotation, visited))
            {
                return true;
            }
        }
        // Check the superclass recursively
        return checkAnnotation(clazz.getSuperclass(), annotation, visited);
    }

    static <T extends Annotation> List<T> getAnnotation(Class<?> clazz, Class<T> annotation)
    {
        List<T> annotations = new ArrayList<>();
        Set<Class<?>> visited = new HashSet<>();
        // Process class hierarchy and interfaces recursively
        collectAnnotations(clazz, annotation, annotations, visited);
        return annotations;
    }

    static <T extends Annotation> void collectAnnotations(
            Class<?> clazz,
            Class<T> annotation,
            List<T> annotations,
            Set<Class<?>> visited
    )
    {
        if (clazz == null || clazz.equals(Object.class) || visited.contains(clazz))
        {
            return;
        }

        // Mark this class as visited to avoid re-processing
        visited.add(clazz);

        // Check if the current class has the annotation
        if (clazz.isAnnotationPresent(annotation))
        {
            annotations.addAll(List.of(clazz.getAnnotationsByType(annotation)));
        }

        // Recursively process interfaces
        for (Class<?> iface : clazz.getInterfaces())
        {
            collectAnnotations(iface, annotation, annotations, visited);
        }

        // Recursively process the superclass
        collectAnnotations(clazz.getSuperclass(), annotation, annotations, visited);
    }


    static String getTsFilename(INgApp<?> clazz)
    {
        NgApp app;
        if (!clazz.getClass()
                .isAnnotationPresent(NgApp.class))
        {
            LogManager.getLogger("AnnotationUtils").error("Ng App Interface without NgApp Annotation? - " + clazz.getClass()
                    .getCanonicalName());
            throw new RuntimeException("Unable to build application without base metadata");
        }
        return clazz.name();
    }

    static String getTsFilename(Class<?> clazz)
    {
        try
        {
            if (clazz.isAnnotationPresent(NgSourceDirectoryReference.class))
            {
                NgSourceDirectoryReference ref = clazz.getAnnotation(NgSourceDirectoryReference.class);
                if (!Strings.isNullOrEmpty(ref.name()))
                {
                    return ref.name();
                }
            }
        } catch (Exception e)
        {
            LogManager.getLogger("AnnotationUtils")
                    .error("Unable to render a ts file name for " + clazz.getCanonicalName(), e);
        }
        return clazz.getSimpleName();
    }

    static String getTsVarName(Class<?> clazz)
    {
        String tsName = getTsFilename(clazz);
        tsName = tsName.substring(0, 1)
                .toLowerCase() + tsName.substring(1);
        return tsName;
    }

    static MyNgField getNgField(String value)
    {
        return new MyNgField(value);
    }

    static MyNgConstructorParameter getNgConstructorParameter(String value)
    {
        return new MyNgConstructorParameter(value);
    }

    static MyNgConstructorBody getNgConstructorBody(String value)
    {
        return new MyNgConstructorBody(value);
    }

    static MyNgMethod getNgMethod(String value)
    {
        return new MyNgMethod(value);
    }

    static MyNgGlobalField getNgGlobalField(String value)
    {
        return new MyNgGlobalField(value);
    }

    static MyNgImportReference getNgImportReference(String importName, String reference)
    {
        var ref = new MyNgImportReference(reference, importName);
        return ref;
    }

    static MyNgImportProvider getNgImportProvider(String importName)
    {
        var ref = new MyNgImportProvider(importName);
        return ref;
    }

    static MyNgImportModule getNgImportModule(String importName)
    {
        var ref = new MyNgImportModule(importName);
        return ref;
    }


    static MyNgAfterViewInit getNgAfterViewInit(String value)
    {
        var ref = new MyNgAfterViewInit(value);
        return ref;
    }

    static MyNgAfterViewChecked getNgAfterViewChecked(String value)
    {
        var ref = new MyNgAfterViewChecked(value);
        return ref;
    }


    static MyNgAfterContentChecked getNgAfterContentChecked(String value)
    {
        var ref = new MyNgAfterContentChecked(value);
        return ref;
    }

    static MyNgAfterContentInit getNgAfterContentInit(String value)
    {
        var ref = new MyNgAfterContentInit(value);
        return ref;
    }

    static MyNgInterface getNgInterface(String value)
    {
        var ref = new MyNgInterface(value);
        return ref;
    }

    static MyNgOnInit getNgOnInit(String value)
    {
        var ref = new MyNgOnInit(value);
        return ref;
    }


    static MyNgInput getNgInput(String value)
    {
        var ref = new MyNgInput(value);
        return ref;
    }

    static MyNgOutput getNgOutput(String value, String parentMethodName)
    {
        var ref = new MyNgOutput(value, parentMethodName);
        return ref;
    }

    static MyNgOnDestroy getNgOnDestroy(String value)
    {
        var ref = new MyNgOnDestroy(value);
        return ref;
    }

    static MyNgMethod getNgComponentMethod(String importName)
    {
        var ref = new MyNgMethod(importName);
        return ref;
    }

    static MyNgComponentReference getNgComponentReference(Class<? extends IComponent<?>> aClass)
    {
        var componentReference = new MyNgComponentReference(aClass);
        return componentReference;
    }

    static MyNgComponentTagAttribute getNgComponentTagAttribute(String key, String value)
    {
        var ref = new MyNgComponentTagAttribute().setKey(key)
                .setValue(value);
        return ref;
    }

    static MyNgDataTypeReference getNgDataTypeReference(Class<? extends INgDataType<?>> dataTypeClass)
    {
        var ref = new MyNgDataTypeReference().setDataTypeClass(dataTypeClass);
        return ref;
    }

    static MyNgComponent getNgComponent(String value)
    {
        var ref = new MyNgComponent().setValue(value);
        return ref;
    }

    @Getter
    @Setter
    @ToString
    class MyNgComponent implements NgComponent, IConfiguration
    {
        private String value;
        private String providedIn;
        private boolean standalone = true;

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public String providedIn()
        {
            return providedIn;
        }

        @Override
        public boolean standalone()
        {
            return standalone;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgComponent.class;
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgDataTypeReference implements NgDataTypeReference, IConfiguration
    {
        private Class<? extends INgDataType<?>> dataTypeClass;
        private boolean primary;
        private String signalName;

        @Override
        public Class<? extends INgDataType<?>> value()
        {
            return dataTypeClass;
        }

        @Override
        public boolean primary()
        {
            return primary;
        }

        @Override
        public String signalName()
        {
            return signalName;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgDataTypeReference.class;
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgComponentTagAttribute implements NgComponentTagAttribute, IConfiguration
    {
        private String key;
        private String value;

        @Override
        public String key()
        {
            return key;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public int sortOrder()
        {
            return 0;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgComponentTagAttribute.class;
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgConstructorParameter implements NgConstructorParameter, IConfiguration
    {
        private final String value;
        private boolean onParent = false;
        private boolean onSelf = true;
        private boolean isPublic = false;

        public MyNgConstructorParameter(String value)
        {
            this.value = value;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public boolean onParent()
        {
            return onParent;
        }

        @Override
        public boolean onSelf()
        {
            return onSelf;
        }

        @Override
        public boolean isPublic()
        {
            return isPublic;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgConstructorParameter.class;
        }

    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgField implements NgField, IConfiguration
    {
        private String value;
        private boolean onParent = false;
        private boolean onSelf = true;

        public MyNgField(String value)
        {
            this.value = value;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgField.class;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public boolean onParent()
        {
            return onParent;
        }

        @Override
        public boolean onSelf()
        {
            return onSelf;
        }

    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgConstructorBody implements NgConstructorBody, IConfiguration
    {
        private final String value;
        private boolean onParent = false;
        private boolean onSelf = true;

        public MyNgConstructorBody(String value)
        {
            this.value = value;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgConstructorBody.class;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public boolean onParent()
        {
            return onParent;
        }

        @Override
        public boolean onSelf()
        {
            return onSelf;
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgMethod implements NgMethod, IConfiguration
    {
        private final String value;
        private boolean onParent = false;
        private boolean onSelf = true;

        public MyNgMethod(String value)
        {
            this.value = value;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgMethod.class;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public boolean onParent()
        {
            return onParent;
        }

        @Override
        public boolean onSelf()
        {
            return onSelf;
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgGlobalField implements NgGlobalField, IConfiguration
    {
        private final String value;
        private boolean onParent = false;
        private boolean onSelf = true;

        public MyNgGlobalField(String value)
        {
            this.value = value;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgGlobalField.class;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public boolean onParent()
        {
            return onParent;
        }

        @Override
        public boolean onSelf()
        {
            return onSelf;
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgImportReference implements NgImportReference, IConfiguration
    {
        private final String reference;
        private final String importName;

        private boolean onParent = false;
        private boolean onSelf = true;
        private boolean direct = false;

        public MyNgImportReference(String reference, String importName)
        {
            this.reference = reference;
            this.importName = importName;
        }

        @Override
        public String reference()
        {
            return reference;
        }

        @Override
        public String value()
        {
            return importName;
        }

        @Override
        public boolean onParent()
        {
            return onParent;
        }

        @Override
        public boolean onSelf()
        {
            return onSelf;
        }

        @Override
        public boolean direct()
        {
            return direct;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgImportReference.class;
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgImportProvider implements NgImportProvider, IConfiguration
    {
        private final String importName;
        private boolean onParent = false;
        private boolean onSelf = true;

        public MyNgImportProvider(String importName)
        {
            this.importName = importName;
        }

        @Override
        public String value()
        {
            return importName;
        }

        @Override
        public boolean onParent()
        {
            return onParent;
        }

        @Override
        public boolean onSelf()
        {
            return onSelf;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgImportProvider.class;
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgImportModule implements NgImportModule, IConfiguration
    {
        private final String importName;
        private boolean onParent = false;
        private boolean onSelf = true;

        public MyNgImportModule(String importName)
        {
            this.importName = importName;
        }

        @Override
        public String value()
        {
            return importName;
        }

        @Override
        public boolean onParent()
        {
            return onParent;
        }

        @Override
        public boolean onSelf()
        {
            return onSelf;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgImportModule.class;
        }
    }

    @ToString
    @EqualsAndHashCode
    class MyNgAfterViewInit implements NgAfterViewInit, IConfiguration
    {
        private final String value;

        public MyNgAfterViewInit(String value)
        {
            this.value = value;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public int sortOrder()
        {
            return 0;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgAfterViewInit.class;
        }

    }

    @ToString
    @EqualsAndHashCode
    class MyNgAfterViewChecked implements NgAfterViewChecked, IConfiguration
    {
        private final String value;

        public MyNgAfterViewChecked(String value)
        {
            this.value = value;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public int sortOrder()
        {
            return 0;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgAfterViewChecked.class;
        }
    }

    @ToString
    @EqualsAndHashCode
    class MyNgAfterContentChecked implements NgAfterContentChecked, IConfiguration
    {
        private final String value;

        public MyNgAfterContentChecked(String value)
        {
            this.value = value;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public int sortOrder()
        {
            return 0;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgAfterContentChecked.class;
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgInterface implements NgInterface, IConfiguration
    {
        private final String value;
        private boolean onParent = false;
        private boolean onSelf = true;

        public MyNgInterface(String value)
        {
            this.value = value;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public boolean onParent()
        {
            return onParent;
        }

        @Override
        public boolean onSelf()
        {
            return onSelf;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgInterface.class;
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgInput implements NgInput, IConfiguration
    {
        private String value;
        private Class<? extends INgDataType<?>> type = any.class;
        private String attributeReference;
        private boolean renderAttributeReference = true;
        private boolean additionalData = true;
        private boolean mandatory = false;

        public MyNgInput(String value)
        {
            this.value = value;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public Class<? extends INgDataType<?>> type()
        {
            return type;
        }

        @Override
        public String attributeReference()
        {
            return attributeReference;
        }

        @Override
        public boolean renderAttributeReference()
        {
            return renderAttributeReference;
        }

        @Override
        public boolean additionalData()
        {
            return additionalData;
        }

        @Override
        public boolean mandatory()
        {
            return mandatory;
        }

        @Override
        public int sortOrder()
        {
            return 0;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgInput.class;
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgOutput implements NgOutput, IConfiguration
    {
        private String value;
        private String parentMethodName;
        private Class<? extends INgDataType<?>> type = any.class;

        public MyNgOutput(String value, String parentMethodName)
        {
            this.value = value;
            this.parentMethodName = parentMethodName;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public String parentMethodName()
        {
            return parentMethodName;
        }

        @Override
        public Class<? extends INgDataType<?>> type()
        {
            return type;
        }

        @Override
        public int sortOrder()
        {
            return 0;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgOutput.class;
        }
    }

    @ToString
    @EqualsAndHashCode
    class MyNgOnInit implements NgOnInit, IConfiguration
    {
        private final String value;

        public MyNgOnInit(String value)
        {
            this.value = value;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public int sortOrder()
        {
            return 0;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgOnInit.class;
        }
    }

    @ToString
    @EqualsAndHashCode
    class MyNgOnDestroy implements NgOnDestroy, IConfiguration
    {
        private final String value;

        public MyNgOnDestroy(String value)
        {
            this.value = value;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public int sortOrder()
        {
            return 0;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgOnDestroy.class;
        }
    }

    @ToString
    @EqualsAndHashCode
    class MyNgAfterContentInit implements NgAfterContentInit, IConfiguration
    {
        private final String value;

        public MyNgAfterContentInit(String value)
        {
            this.value = value;
        }

        @Override
        public String value()
        {
            return value;
        }

        @Override
        public int sortOrder()
        {
            return 0;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgAfterContentInit.class;
        }
    }

    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode
    class MyNgComponentReference implements NgComponentReference, IConfiguration
    {
        private final Class<? extends IComponent<?>> aClass;
        private boolean provides = false;
        private boolean onParent = false;
        private boolean onSelf = true;

        public MyNgComponentReference(Class<? extends IComponent<?>> aClass)
        {
            this.aClass = aClass;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgComponentReference.class;
        }

        @Override
        public Class<? extends IComponent<?>> value()
        {
            return aClass;
        }

        @Override
        public boolean provides()
        {
            return provides;
        }

        @Override
        public boolean onParent()
        {
            return onParent;
        }

        @Override
        public boolean onSelf()
        {
            return onSelf;
        }
    }
}
