package com.jwebmp.core.base.angular.client.annotations.structures;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgSignalEffects.class)
@Inherited
public @interface NgSignalEffect
{
    String value();

    String referenceName();
}
