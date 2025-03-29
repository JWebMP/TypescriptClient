package com.jwebmp.core.base.angular.client.annotations.references;

import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgImportReferences.class)
@Inherited
public @interface NgImportReference
{
    String reference();

    String value();

    boolean onParent() default false;

    boolean onSelf() default true;

    /**
     * @return if the reference must be a direct import, using only the value field for rendering
     */
    boolean direct() default false;

    /**
     * If the value must be wrapped in braces or is a precise reference
     *
     * @return
     */
    boolean wrapValueInBraces() default true;
}
