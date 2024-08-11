package com.jwebmp.core.base.angular.client.annotations.references;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgImportModules.class)
@Inherited
public @interface NgImportModule
{
    String value();

    boolean onParent() default false;

    boolean onSelf() default true;
}
