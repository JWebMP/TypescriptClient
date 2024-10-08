package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgComponent
{
    /**
     * The selector going to be used
     *
     * @return
     */
    String value();

    String providedIn() default "";

    /**
     * @return If the component must render as a standalone component
     */
    boolean standalone() default true;
}
