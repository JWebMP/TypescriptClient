package com.jwebmp.core.base.angular.client.services.interfaces;

import com.google.common.base.Strings;
import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.angular.NgApp;
import com.jwebmp.core.base.angular.client.annotations.angular.NgComponent;
import com.jwebmp.core.base.angular.client.annotations.components.NgComponentTagAttribute;
import com.jwebmp.core.base.angular.client.annotations.components.NgInput;
import com.jwebmp.core.base.angular.client.annotations.components.NgOutput;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import com.jwebmp.core.base.angular.client.annotations.typescript.NgSourceDirectoryReference;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import com.jwebmp.core.base.angular.client.services.tstypes.any;
import com.jwebmp.core.databind.IConfiguration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.logging.log4j.LogManager;

import java.lang.annotation.Annotation;
import java.util.*;

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

        //find both single and repeatables
        var annotations = IGuiceContext.get(AnnotationHelper.class)
                                       .getAnnotationFromClass(clazz, annotation);
        if (!annotations.isEmpty())
        {
            return true;
        }

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
        var annos = IGuiceContext.get(AnnotationHelper.class)
                                 .getAnnotationFromClass(clazz, annotation);
        for (T anno : annos)
        {
            annotations.add(anno);
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
            LogManager.getLogger("AnnotationUtils")
                      .error("Ng App Interface without NgApp Annotation? - " + clazz.getClass()
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
        }
        catch (Exception e)
        {
            LogManager.getLogger("AnnotationUtils")
                      .error("Unable to render a ts file name for " + clazz.getCanonicalName(), e);
        }
        // Prefer the standard simple name when available (covers top-level and member classes)
        String simple = clazz.getSimpleName();
        if (!Strings.isNullOrEmpty(simple))
        {
            return simple;
        }
        // Handle anonymous/local classes where getSimpleName() may be empty
        String binary = clazz.getName();
        // Strip package
        int lastDot = binary.lastIndexOf('.');
        String noPkg = lastDot >= 0 ? binary.substring(lastDot + 1) : binary;
        // If there is a '$', try to use the simple segment after the last '$'
        int lastDollar = noPkg.lastIndexOf('$');
        if (lastDollar >= 0 && lastDollar < noPkg.length() - 1)
        {
            String after = noPkg.substring(lastDollar + 1);
            // If it's an anonymous class (numeric suffix), fall back to enclosing class name or the part before '$'
            if (after.chars()
                     .allMatch(Character::isDigit))
            {
                Class<?> enclosing = clazz.getEnclosingClass();
                if (enclosing != null && !Strings.isNullOrEmpty(enclosing.getSimpleName()))
                {
                    return enclosing.getSimpleName();
                }
                // Fallback: return the part before the last '$'
                return noPkg.substring(0, lastDollar);
            }
            return after;
        }
        // Final fallback: return whatever remains without package
        return noPkg;
    }

    static String getTsVarName(Class<?> clazz)
    {
        String tsName = getTsFilename(clazz);
        tsName = tsName.substring(0, 1)
                       .toLowerCase() + tsName.substring(1);
        return tsName;
    }

    static MyNgField getNgField(String value, boolean onParent, boolean onSelf)
    {
        return new MyNgField(value, onParent,onSelf);
    }

    static MyNgConstructorParameter getNgConstructorParameter(String value, boolean onParent, boolean onSelf,boolean isPublic)
    {
        return new MyNgConstructorParameter(value, onParent, onSelf,isPublic);
    }

    static MyNgConstructorBody getNgConstructorBody(String value, boolean onParent, boolean onSelf)
    {
        return new MyNgConstructorBody(value,onParent,onSelf);
    }

    static MyNgMethod getNgMethod(String value, boolean onParent, boolean onSelf)
    {
        return new MyNgMethod(value,onParent,onSelf);
    }

    static MyNgGlobalField getNgGlobalField(String value)
    {
        return new MyNgGlobalField(value);
    }

    static MyNgImportReference getNgImportReference(String importName, String reference, boolean onSelf, boolean onParent, boolean direct, boolean wrapValueInBraces)
    {
        var ref = new MyNgImportReference(reference, importName, onParent, onSelf, direct, wrapValueInBraces);
        return ref;
    }

    static MyNgImportReference getNgImportReference(String importName, String reference, boolean direct, boolean wrapValueInBraces)
    {
        var ref = new MyNgImportReference(reference, importName, false, true, direct, wrapValueInBraces);
        return ref;
    }

    static MyNgImportReference getNgImportReference(String importName, String reference)
    {
        var ref = new MyNgImportReference(reference, importName, false, true, false, true);
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


    static MyNgInput getNgInput(String value, boolean mandatory)
    {
        var ref = new MyNgInput(value).setMandatory(mandatory);
        return ref;
    }

    static MyNgInput getNgInput(String value, boolean mandatory, Class<? extends INgDataType<?>> type)
    {
        var ref = new MyNgInput(value).setMandatory(mandatory)
                                      .setType(type);
        return ref;
    }

    static MyNgInput getNgInput(String value, boolean mandatory, Class<? extends INgDataType<?>> type, String attributeReference, boolean renderAttributeReference)
    {
        var ref = new MyNgInput(value).setMandatory(mandatory)
                                      .setType(type)
                                      .setAttributeReference(attributeReference)
                                      .setRenderAttributeReference(renderAttributeReference);
        return ref;
    }

    static MyNgInput getNgInput(String value, boolean mandatory, Class<? extends INgDataType<?>> type, String attributeReference, boolean renderAttributeReference, boolean additionalData)
    {
        var ref = new MyNgInput(value).setMandatory(mandatory)
                                      .setType(type)
                                      .setAttributeReference(attributeReference)
                                      .setRenderAttributeReference(renderAttributeReference)
                                      .setAdditionalData(additionalData);
        return ref;
    }

    static MyNgInput getNgInput(String value, boolean mandatory, Class<? extends INgDataType<?>> type, String attributeReference, boolean renderAttributeReference, boolean additionalData, boolean array)
    {
        var ref = new MyNgInput(value).setMandatory(mandatory)
                                      .setType(type)
                                      .setAttributeReference(attributeReference)
                                      .setRenderAttributeReference(renderAttributeReference)
                                      .setAdditionalData(additionalData)
                                      .setArray(array);
        return ref;
    }

    static MyNgOutput getNgOutput(String value, String parentMethodName,Class<? extends INgDataType<?>> type, boolean onSelf, boolean onParent)
    {
        var ref = new MyNgOutput(value, parentMethodName,type, onSelf, onParent);
        return ref;
    }

    static MyNgOnDestroy getNgOnDestroy(String value)
    {
        var ref = new MyNgOnDestroy(value);
        return ref;
    }

    static MyNgMethod getNgComponentMethod(String importName, boolean onParent, boolean onSelf)
    {
        var ref = new MyNgMethod(importName,onParent,onSelf);
        return ref;
    }

    static MyNgInject getNgInject(String referenceName, String type)
    {
        var ref = new MyNgInject().setReferenceName(referenceName)
                                  .setValue(type);
        return ref;
    }

    static MyNgModel getNgModel(String value, String referenceName, Class<? extends INgDataType<?>> dataType, boolean mandatory)
    {
        var ref = new MyNgModel().setValue(value)
                                 .setReferenceName(referenceName)
                                 .setDataType(dataType)
                                 .setMandatory(mandatory);
        return ref;
    }

    static MyNgSignal getNgSignal(String referenceName, String value, String type)
    {
        var ref = new MyNgSignal().setReferenceName(referenceName)
                                  .setValue(value)
                                  .setType(type);
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

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgComponent that = (MyNgComponent) o;
            return isStandalone() == that.isStandalone() && Objects.equals(getValue(), that.getValue()) && Objects.equals(getProvidedIn(), that.getProvidedIn());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getValue(), getProvidedIn(), isStandalone());
        }
    }

    @Getter
    @Setter
    @ToString
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

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgDataTypeReference that = (MyNgDataTypeReference) o;
            return isPrimary() == that.isPrimary() && Objects.equals(getDataTypeClass(), that.getDataTypeClass()) && Objects.equals(getSignalName(), that.getSignalName());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getDataTypeClass(), isPrimary(), getSignalName());
        }
    }

    @Getter
    @Setter
    @ToString
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

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgComponentTagAttribute that = (MyNgComponentTagAttribute) o;
            return Objects.equals(getKey(), that.getKey()) && Objects.equals(getValue(), that.getValue());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getKey(), getValue());
        }
    }

    @Getter
    @Setter
    @ToString
    class MyNgConstructorParameter implements NgConstructorParameter, IConfiguration
    {
        private final String value;
        private boolean onParent;
        private boolean onSelf;
        private boolean isPublic;

        public MyNgConstructorParameter(String value, boolean onParent, boolean onSelf, boolean isPublic)
        {
            this.value = value;
												this.onParent = onParent;
												this.onSelf = onSelf;
												this.isPublic = isPublic;
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

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgConstructorParameter that = (MyNgConstructorParameter) o;
            return isOnParent() == that.isOnParent() && isOnSelf() == that.isOnSelf() && isPublic() == that.isPublic() && Objects.equals(getValue(), that.getValue());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getValue(), isOnParent(), isOnSelf(), isPublic());
        }
    }

    @Getter
    @Setter
    @ToString
    class MyNgField implements NgField, IConfiguration
    {
        private String value;
        private boolean onParent = false;
        private boolean onSelf = true;

        public MyNgField(String value, boolean onParent, boolean onSelf)
        {
            this.value = value;
												this.onParent = onParent;
												this.onSelf = onSelf;
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

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgField myNgField = (MyNgField) o;
            return isOnParent() == myNgField.isOnParent() && isOnSelf() == myNgField.isOnSelf() && Objects.equals(getValue(), myNgField.getValue());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getValue(), isOnParent(), isOnSelf());
        }
    }

    @Getter
    @Setter
    @ToString
    class MyNgConstructorBody implements NgConstructorBody, IConfiguration
    {
        private final String value;
        private boolean onParent = false;
        private boolean onSelf = true;

        public MyNgConstructorBody(String value, boolean onParent, boolean onSelf)
        {
            this.value = value;
												this.onParent = onParent;
												this.onSelf = onSelf;
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

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgConstructorBody that = (MyNgConstructorBody) o;
            return isOnParent() == that.isOnParent() && isOnSelf() == that.isOnSelf() && Objects.equals(getValue(), that.getValue());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getValue(), isOnParent(), isOnSelf());
        }
    }

    @Getter
    @Setter
    @ToString
    class MyNgMethod implements NgMethod, IConfiguration
    {
        private final String value;
        private boolean onParent = false;
        private boolean onSelf = true;

        public MyNgMethod(String value, boolean onParent, boolean onSelf)
        {
            this.value = value;
												this.onParent = onParent;
												this.onSelf = onSelf;
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

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgMethod that = (MyNgMethod) o;
            return isOnParent() == that.isOnParent() && isOnSelf() == that.isOnSelf() && Objects.equals(getValue(), that.getValue());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getValue(), isOnParent(), isOnSelf());
        }
    }

    @Getter
    @Setter
    @ToString
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

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgGlobalField that = (MyNgGlobalField) o;
            return isOnParent() == that.isOnParent() && isOnSelf() == that.isOnSelf() && Objects.equals(getValue(), that.getValue());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getValue(), isOnParent(), isOnSelf());
        }
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    class MyNgImportReference implements NgImportReference, IConfiguration
    {
        private final String reference;
        private final String importName;

        private boolean onParent = false;
        private boolean onSelf = true;
        private boolean direct = false;
        private boolean wrapValueInBraces = true;

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
        public boolean wrapValueInBraces()
        {
            return wrapValueInBraces;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgImportReference.class;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgImportReference that = (MyNgImportReference) o;
            return isOnParent() == that.isOnParent() && isOnSelf() == that.isOnSelf() && isDirect() == that.isDirect() && Objects.equals(getReference(), that.getReference()) && Objects.equals(getImportName(), that.getImportName());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getReference(), getImportName(), isOnParent(), isOnSelf(), isDirect());
        }
    }

    @Getter
    @Setter
    @ToString
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

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgImportProvider that = (MyNgImportProvider) o;
            return isOnParent() == that.isOnParent() && isOnSelf() == that.isOnSelf() && Objects.equals(getImportName(), that.getImportName());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getImportName(), isOnParent(), isOnSelf());
        }
    }

    @Getter
    @Setter
    @ToString
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

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgImportModule that = (MyNgImportModule) o;
            return isOnParent() == that.isOnParent() && isOnSelf() == that.isOnSelf() && Objects.equals(getImportName(), that.getImportName());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getImportName(), isOnParent(), isOnSelf());
        }
    }

    @ToString
    class MyNgAfterViewInit implements NgAfterViewInit, IConfiguration
    {
        private final String value;
        private boolean onParent = false;
        private boolean onSelf = true;

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
            return NgAfterViewInit.class;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgAfterViewInit that = (MyNgAfterViewInit) o;
            return onParent == that.onParent && onSelf == that.onSelf && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(value, onParent, onSelf);
        }
    }

    @ToString
    class MyNgAfterViewChecked implements NgAfterViewChecked, IConfiguration
    {
        private final String value;
        private boolean onParent = false;
        private boolean onSelf = true;

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
            return NgAfterViewChecked.class;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgAfterViewChecked that = (MyNgAfterViewChecked) o;
            return onParent == that.onParent && onSelf == that.onSelf && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(value, onParent, onSelf);
        }
    }

    @ToString
    class MyNgAfterContentChecked implements NgAfterContentChecked, IConfiguration
    {
        private final String value;
        private boolean onParent = false;
        private boolean onSelf = true;

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
            return NgAfterContentChecked.class;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgAfterContentChecked that = (MyNgAfterContentChecked) o;
            return onParent == that.onParent && onSelf == that.onSelf && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(value, onParent, onSelf);
        }
    }

    @Getter
    @Setter
    @ToString
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

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgInterface that = (MyNgInterface) o;
            return isOnParent() == that.isOnParent() && isOnSelf() == that.isOnSelf() && Objects.equals(getValue(), that.getValue());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getValue(), isOnParent(), isOnSelf());
        }
    }

    @Getter
    @Setter
    @ToString
    class MyNgInput implements NgInput, IConfiguration
    {
        private String value;
        private Class<? extends INgDataType<?>> type = any.class;
        private String attributeReference;
        private boolean renderAttributeReference = true;
        private boolean additionalData = true;
        private boolean mandatory = false;
        private boolean array = false;

        private boolean onSelf = true;
        private boolean onParent = false;

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
        public boolean array()
        {
            return array;
        }

        @Override
        public boolean onSelf()
        {
            return false;
        }

        @Override
        public boolean onParent()
        {
            return false;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgInput.class;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgInput myNgInput = (MyNgInput) o;
            return isRenderAttributeReference() == myNgInput.isRenderAttributeReference() && isAdditionalData() == myNgInput.isAdditionalData() && isMandatory() == myNgInput.isMandatory() && isOnSelf() == myNgInput.isOnSelf() && isOnParent() == myNgInput.isOnParent() && Objects.equals(getValue(), myNgInput.getValue()) && Objects.equals(getType(), myNgInput.getType()) && Objects.equals(getAttributeReference(), myNgInput.getAttributeReference());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getValue(), getType(), getAttributeReference(), isRenderAttributeReference(), isAdditionalData(), isMandatory(), isOnSelf(), isOnParent());
        }
    }

    @Getter
    @Setter
    @ToString
    class MyNgOutput implements NgOutput, IConfiguration
    {
        private String value;
        private String parentMethodName;
        private Class<? extends INgDataType<?>> type;
        private boolean onSelf = true;
        private boolean onParent = false;

        public MyNgOutput(String value, String parentMethodName,Class<? extends INgDataType<?>> type, boolean onSelf, boolean onParent)
        {
										this.value = value;
										this.parentMethodName = parentMethodName;
										this.type = type;
										this.onSelf = onSelf;
										this.onParent = onParent;
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

        @Override
        public boolean onSelf()
        {
            return false;
        }

        @Override
        public boolean onParent()
        {
            return false;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgOutput that = (MyNgOutput) o;
            return isOnSelf() == that.isOnSelf() && isOnParent() == that.isOnParent() && Objects.equals(getValue(), that.getValue()) && Objects.equals(getParentMethodName(), that.getParentMethodName()) && Objects.equals(getType(), that.getType());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getValue(), getParentMethodName(), getType(), isOnSelf(), isOnParent());
        }
    }

    @ToString
    class MyNgOnInit implements NgOnInit, IConfiguration
    {
        private final String value;
        private boolean onParent = false;
        private boolean onSelf = true;

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
            return NgOnInit.class;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgOnInit that = (MyNgOnInit) o;
            return onParent == that.onParent && onSelf == that.onSelf && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(value, onParent, onSelf);
        }
    }

    @ToString
    class MyNgOnDestroy implements NgOnDestroy, IConfiguration
    {
        private final String value;
        private boolean onParent = false;
        private boolean onSelf = true;

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
            return NgOnDestroy.class;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgOnDestroy that = (MyNgOnDestroy) o;
            return onParent == that.onParent && onSelf == that.onSelf && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(value, onParent, onSelf);
        }
    }

    @ToString
    class MyNgAfterContentInit implements NgAfterContentInit, IConfiguration
    {
        private final String value;
        private boolean onParent = false;
        private boolean onSelf = true;

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
            return NgAfterContentInit.class;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgAfterContentInit that = (MyNgAfterContentInit) o;
            return onParent == that.onParent && onSelf == that.onSelf && Objects.equals(value, that.value);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(value, onParent, onSelf);
        }
    }

    @Getter
    @Setter
    @ToString
    class MyNgComponentReference implements NgComponentReference, IConfiguration
    {
        private final Class<? extends IComponent<?>> aClass;
        private boolean provides = false;
        private boolean onParent = false;
        private boolean onSelf = true;
        private boolean referenceOnly = false;

        public MyNgComponentReference(Class<? extends IComponent<?>> aClass)
        {
            this.aClass = aClass;
            if (aClass.isAnnotationPresent(NgComponentReference.class))
            {
                NgComponentReference ngComponentReference = aClass.getAnnotation(NgComponentReference.class);
                this.provides = ngComponentReference.provides();
                this.referenceOnly = ngComponentReference.referenceOnly();
                this.onParent = ngComponentReference.onParent();
                this.onSelf = ngComponentReference.onSelf();
            }
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

        @Override
        public boolean referenceOnly()
        {
            return referenceOnly;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgComponentReference that = (MyNgComponentReference) o;
            return isProvides() == that.isProvides() && isOnParent() == that.isOnParent() && isOnSelf() == that.isOnSelf() && this.referenceOnly == that.referenceOnly && Objects.equals(aClass, that.aClass);
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(aClass, isProvides(), isOnParent(), isOnSelf());
        }
    }

    @Getter
    @Setter
    @ToString
    class MyNgInject implements NgInject, IConfiguration
    {
        private String value;
        private String referenceName;

        private boolean onParent = false;
        private boolean onSelf = true;

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
            return NgInject.class;
        }

        @Override
        public String referenceName()
        {
            return referenceName;
        }

        @Override
        public boolean equals(Object o)
        {

            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgInject that = (MyNgInject) o;
            return isOnParent() == that.isOnParent() && isOnSelf() == that.isOnSelf() && Objects.equals(getValue(), that.getValue()) && Objects.equals(getReferenceName(), that.getReferenceName());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getValue(), getReferenceName(), isOnParent(), isOnSelf());
        }
    }

    @Getter
    @Setter
    @ToString
    class MyNgModel implements NgModel, IConfiguration
    {
        private String value;
        private String referenceName;
        private Class<? extends INgDataType<?>> dataType;
        private boolean mandatory = false;
        private boolean onParent = false;
        private boolean onSelf = true;

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
        public Class<? extends INgDataType<?>> dataType()
        {
            return dataType;
        }

        @Override
        public boolean mandatory()
        {
            return mandatory;
        }

        @Override
        public Class<? extends Annotation> annotationType()
        {
            return NgModel.class;
        }

        @Override
        public String referenceName()
        {
            return referenceName;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgModel myNgModal = (MyNgModel) o;
            return isOnParent() == myNgModal.isOnParent() && isOnSelf() == myNgModal.isOnSelf() && Objects.equals(getValue(), myNgModal.getValue()) && Objects.equals(getReferenceName(), myNgModal.getReferenceName());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getValue(), getReferenceName(), isOnParent(), isOnSelf());
        }
    }

    @Getter
    @Setter
    @ToString
    class MyNgSignal implements NgSignal, IConfiguration
    {
        private String value;
        private String type;

        private String referenceName;
        private boolean onParent = false;
        private boolean onSelf = true;

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
            return NgModel.class;
        }

        @Override
        public String referenceName()
        {
            return referenceName;
        }

        @Override
        public String type()
        {
            return type;
        }

        @Override
        public boolean equals(Object o)
        {
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }
            MyNgSignal that = (MyNgSignal) o;
            return isOnParent() == that.isOnParent() && isOnSelf() == that.isOnSelf() && Objects.equals(getValue(), that.getValue()) && Objects.equals(getType(), that.getType()) && Objects.equals(getReferenceName(), that.getReferenceName());
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(getValue(), getType(), getReferenceName(), isOnParent(), isOnSelf());
        }
    }
}
