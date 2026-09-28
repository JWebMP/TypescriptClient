package com.jwebmp.core.base.angular.client.annotations.angularconfig;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Per-environment Angular build overrides. Unspecified settings retain template defaults.
 */
@Target({})
@Retention(RUNTIME)
public @interface NgBuildConfiguration
{
    /**
     * Replaces matching budgets (type and name), appending new ones.
     */
    NgBudget[] budgets() default {};

    /**
     * Set false to discard template budgets, including when budgets is empty.
     */
    boolean inheritBudgets() default true;

    BooleanOption optimization() default BooleanOption.DEFAULT;

    BooleanOption sourceMap() default BooleanOption.DEFAULT;

    BooleanOption extractLicenses() default BooleanOption.DEFAULT;

    OutputHashing outputHashing() default OutputHashing.DEFAULT;

    /**
     * JSON object merged last, after typed settings. Objects merge recursively;
     * arrays/scalars replace existing values, and null removes a property.
     * Keys are Angular build options, not a complete angular.json document.
     */
    String optionsJson() default "{}";

    enum BooleanOption
    {
        DEFAULT, TRUE, FALSE
    }

    enum OutputHashing
    {
        DEFAULT, NONE, ALL, MEDIA, BUNDLES
    }
}
