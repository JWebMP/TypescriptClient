package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.*;

/** An application/boot-component translation override. Specify exactly one of resource or url. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Repeatable(NgTranslationSources.class)
public @interface NgTranslationSource
{
    String namespace();
    /** Classpath directory containing language.json files, merged during generation. */
    String resource() default "";
    /** Runtime HTTP URL containing {language}; ordinary Angular HTTP interceptors apply. */
    String url() default "";
    /** Higher priorities override lower priorities; conflicting equal-priority keys are errors. */
    int priority() default 100;
    /** A runtime source may fail without preventing use of bundled translations. */
    boolean optional() default true;
}
