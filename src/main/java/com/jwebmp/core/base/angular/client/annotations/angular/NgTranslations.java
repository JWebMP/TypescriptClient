package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.*;

/** Enables app-scoped Transloco generation. App declarations override boot-component declarations. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface NgTranslations
{
    String defaultLanguage() default "en";
    String[] supportedLanguages() default {};
    /** Selected library namespaces; empty includes all discovered namespaces. */
    String[] namespaces() default {};
    /** Enable ICU plural/select messages as well as ordinary interpolation. */
    boolean messageFormat() default true;
    /** Timeout for each translation HTTP request. */
    int timeoutMs() default 10000;
}
