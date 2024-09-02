package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgDirective
{
    String value();

    String[] inputs() default {};

    String[] outputs() default {};

    String exportAs() default "";

    String[] queries() default {};

    String host() default "";

    boolean includeADeclaration() default true;

    boolean standalone() default false;
}
