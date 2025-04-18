package com.jwebmp.core.base.angular.client.annotations.components;


import com.jwebmp.core.base.angular.client.services.tstypes.any;
import com.jwebmp.core.base.angular.client.services.interfaces.INgDataType;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgInputs.class)
@Inherited
public @interface NgInput
{
    String value();

    Class<? extends INgDataType<?>> type() default any.class;

    String attributeReference() default "";

    boolean renderAttributeReference() default true;

    boolean additionalData() default false;

    boolean mandatory() default false;

    boolean onParent() default false;

    boolean onSelf() default true;

    int sortOrder() default 100;
}
