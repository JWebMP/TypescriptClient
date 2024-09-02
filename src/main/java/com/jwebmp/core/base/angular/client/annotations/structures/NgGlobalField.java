package com.jwebmp.core.base.angular.client.annotations.structures;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgGlobalFields.class)
@Inherited
public @interface NgGlobalField
{
    String value();

    boolean onParent() default false;

    boolean onSelf() default true;
}
