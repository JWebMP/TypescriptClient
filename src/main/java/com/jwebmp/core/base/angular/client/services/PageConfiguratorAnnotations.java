package com.jwebmp.core.base.angular.client.services;

import com.jwebmp.core.services.IPageConfigurator;
import java.lang.annotation.Annotation;
import java.util.*;

/** Annotation metadata on existing SPI providers does not require scanning their modules. */
public final class PageConfiguratorAnnotations {
    private PageConfiguratorAnnotations() {}

    private static final class Providers {
        private static final List<Class<?>> TYPES = ServiceLoader.load(IPageConfigurator.class).stream()
                .<Class<?>>map(ServiceLoader.Provider::type)
                .sorted(Comparator.comparing(Class::getName)).toList();
    }

    /** Supplements scan results without instantiating providers or duplicating scanned declarations. */
    public static <A extends Annotation> List<A> unscanned(Class<A> annotation, Set<String> scannedClasses) {
        List<A> annotations = new ArrayList<>();
        for (Class<?> type : Providers.TYPES) {
            if (!scannedClasses.contains(type.getName()))
                annotations.addAll(Arrays.asList(type.getAnnotationsByType(annotation)));
        }
        return annotations;
    }
}
