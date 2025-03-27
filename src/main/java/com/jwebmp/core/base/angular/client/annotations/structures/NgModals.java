package com.jwebmp.core.base.angular.client.annotations.structures;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgModals
{
    /**
     * The string name of the dev dependency for the given ng app
     *
     * @return
     */
    NgModal[] value();
}
