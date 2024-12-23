package com.jwebmp.core.base.angular.client.services;

import com.guicedee.client.IGuiceContext;
import com.guicedee.guicedinjection.GuiceContext;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import jakarta.inject.Singleton;
import lombok.extern.java.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;

import static com.jwebmp.core.base.angular.client.services.AnnotationsMap.ngAllGlobals;
import static com.jwebmp.core.base.angular.client.services.AnnotationsMap.ngAllMultiples;

@Singleton
@Log
public class AnnotationHelper
{
    private final Map<Class<?>, ClassAnnotationMapping> mappings;
    private final Set<Class<? extends Annotation>> annotationsListing;

    private Map<Class<? extends Annotation>, List<Annotation>> globalAnnotations;

    public static void startup()
    {
        AnnotationHelper annotationHelper = IGuiceContext.get(AnnotationHelper.class);
        for (Class<?> loadAllClass : AnnotationsMap.loadAllClasses())
        {
            annotationHelper.scanClass(loadAllClass);
        }
    }

    AnnotationHelper()
    {
        mappings = new HashMap<>();
        annotationsListing = new LinkedHashSet<>();
        for (Class<? extends Annotation> annotation : AnnotationsMap.annotations)
        {
            annotationsListing.add(annotation);
        }
        globalAnnotations = new HashMap<>();
        ngAllGlobals.forEach((key, value) -> {
            ScanResult scanResult = GuiceContext.instance()
                    .getScanResult();
            List<Annotation> globals = new ArrayList<>();

            ClassInfoList classesWithAnnotation = scanResult.getClassesWithAnnotation(key);

            ClassInfoList classesWithAnnotationMultiple = scanResult.getClassesWithAnnotation(value);
            for (ClassInfo classInfo : classesWithAnnotationMultiple)
            {
                for (Annotation listOfAnnotation : getListOfAnnotations(classInfo.loadClass(), key, value))
                {
                    if (listOfAnnotation != null)
                    {
                        globals.add(listOfAnnotation);
                    }
                }
            }
            for (ClassInfo classInfo : classesWithAnnotation)
            {
                Annotation annotation = classInfo.loadClass()
                        .getAnnotation(key);
                if (annotation != null)
                {
                    globals.add(annotation);
                }
            }

            globalAnnotations.put(key, globals);
        });
    }

    public <T> List<T> getGlobalAnnotations(Class<T> annotationClass)
    {
        return (List<T>) globalAnnotations.get(annotationClass);
    }

    public void scanClass(Class<?> clazz)
    {
        ClassAnnotationMapping cam = new ClassAnnotationMapping();
        cam.setClassKey(clazz);
        cam.setAnnotations(new ArrayList<>());
        scanClassHierarchy(cam, clazz);

        mappings.put(clazz, cam);
    }

    private static final Map<Class<?>, ClassAnnotationMapping> superClassMappings = new HashMap<>();

    private void scanClassHierarchy(ClassAnnotationMapping parentMapping, Class<?> clazz)
    {
        ClassAnnotationMapping maps = null;
        if (superClassMappings.containsKey(clazz))
        {
            maps = superClassMappings.get(clazz);
        } else
        {
            maps = extractAnnotations(clazz);
            superClassMappings.put(clazz, maps);
        }
        maps.mergeInto(parentMapping);

        Class<?>[] clazzInterfaces = clazz.getInterfaces();
        for (Class<?> clazzInterface : clazzInterfaces)
        {
            scanClassHierarchy(parentMapping, clazzInterface);
        }
        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null && !superclass.equals(Object.class))
        {
            ClassAnnotationMapping map = null;
            if (!superClassMappings.containsKey(superclass))
            {
                map = extractAnnotations(superclass);
                superClassMappings.put(superclass, map);
            } else
            {
                map = superClassMappings.get(superclass);
            }
            map.mergeInto(parentMapping);

            scanClassHierarchy(parentMapping, superclass);
            //scanClassHierarchy(parentMapping, superclass);
            //superClassMappings.put(superclass, mappings.get(superclass));
            //  }
            //superClassMappings.get(superclass).mergeInto(parentMapping);
            superclass = superclass.getSuperclass();
        }

    }

    private ClassAnnotationMapping extractAnnotations(Class<?> clazz)
    {
        ClassAnnotationMapping mapping = new ClassAnnotationMapping();
        mapping.setClassKey(clazz);
        for (Class<? extends Annotation> aClass : annotationsListing)
        {
            if (clazz.isAnnotationPresent(aClass))
            {
                Annotation a = clazz.getAnnotation(aClass);
                mapping.addLookup(aClass, a);
            }
            if (ngAllMultiples.containsKey(aClass))
            {
                Class<? extends Annotation> value = ngAllMultiples.get(aClass);
                if (clazz.isAnnotationPresent(value))
                {
                    List<? extends Annotation> listOfAnnotations = getListOfAnnotations(clazz, aClass, value);
                    for (Annotation a : listOfAnnotations)
                    {
                        mapping.addLookup(aClass, a);
                    }
                }
            }

        }
        return mapping;
    }

    public ClassAnnotationMapping getClassMappings(Class<?> clazz)
    {
        if (!mappings.containsKey(clazz))
        {
            scanClass(clazz);
        }
        return mappings.get(clazz);
    }

    public <T> List<T> getAnnotationFromClass(Class<?> clazz, Class<T> annotation)
    {
        if (!mappings.containsKey(clazz))
        {
            scanClass(clazz);
        }
        List<T> ts = (List<T>) mappings.get(clazz)
                .getLookup()
                .get(annotation);
        if (ts == null)
        {
            return new ArrayList<>();
        }
        return ts;
    }

    private <T extends Annotation> List<T> getListOfAnnotations(Class<?> clazz, Class<T> singularAnnotation, Class<? extends Annotation> multipleAnnotation)
    {
        List<T> out = new ArrayList<>();
        if (clazz.isAnnotationPresent(multipleAnnotation))
        {
            Annotation refAnnotation = clazz.getAnnotation(multipleAnnotation);
            try
            {
                Method valueMethod = refAnnotation.annotationType()
                        .getDeclaredMethod("value");
                Annotation[] result = (Annotation[]) valueMethod.invoke(refAnnotation);
                for (Annotation annotation : result)
                {
                    out.add((T) annotation);
                }
            } catch (Exception e)
            {
                log.log(Level.SEVERE, "Cannot read multiple annotations - " + clazz + " - " + multipleAnnotation, e);
            }
        }
        if (clazz.isAnnotationPresent(singularAnnotation))
        {
            out.add(clazz.getAnnotation(singularAnnotation));
        }
        return out;
    }
}
