package com.jwebmp.core.base.angular.client.annotations.boot;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgBootImportReferences.class)
@Inherited
public @interface NgBootImportReference
{
    String reference();

    String value();

    boolean overrides() default false;

    boolean onParent() default false;

    boolean onSelf() default true;

    /**
     * If the reference must be a direct import, using only the value field for rendering
     */
    boolean direct() default false;

    /**
     * If the value must be wrapped in braces or is a precise reference
     */
    boolean wrapValueInBraces() default true;
}
