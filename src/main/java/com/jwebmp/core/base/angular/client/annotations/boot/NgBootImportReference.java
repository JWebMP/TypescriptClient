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

    /**
     * If true, generates a bare side-effect import: {@code import 'reference'}.
     * When set, the {@code value} field is ignored — only the {@code reference} is used.
     */
    boolean sideEffect() default false;

    /**
     * If true, generates an additional line after the import that assigns the imported value
     * to the global scope: {@code (globalThis as any).VALUE = VALUE;}.
     * <p>
     * This is useful for libraries that rely on global variables (e.g. clipboard.js
     * expects {@code ClipboardJS} on the global scope) when the Angular esbuild application
     * builder wraps scripts in a context where UMD global assignment may not reach
     * {@code window}.
     * </p>
     */
    boolean assignToGlobal() default false;
}
