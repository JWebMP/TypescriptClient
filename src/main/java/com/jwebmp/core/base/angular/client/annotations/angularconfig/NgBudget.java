package com.jwebmp.core.base.angular.client.annotations.angularconfig;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * An Angular CLI size budget. Sizes use Angular syntax, for example "30kb",
 * "2mb", or a percentage relative to baseline. Empty values are omitted.
 */
@Target({})
@Retention(RUNTIME)
public @interface NgBudget
{
    Type type();

    /** Required for BUNDLE budgets. */
    String name() default "";

    String baseline() default "";

    String maximumWarning() default "";

    String maximumError() default "";

    String minimumWarning() default "";

    String minimumError() default "";

    String warning() default "";

    String error() default "";

    enum Type
    {
        ALL("all"),
        ALL_SCRIPT("allScript"),
        ANY("any"),
        ANY_SCRIPT("anyScript"),
        ANY_COMPONENT_STYLE("anyComponentStyle"),
        BUNDLE("bundle"),
        INITIAL("initial");

        private final String value;

        Type(String value)
        {
            this.value = value;
        }

        public String value()
        {
            return value;
        }
    }
}
