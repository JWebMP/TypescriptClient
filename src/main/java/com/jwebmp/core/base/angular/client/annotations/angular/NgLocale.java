package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Configures Angular locale data and the application-wide LOCALE_ID provider.
 * Place on an {@link NgApp} class or its boot component; the app takes precedence.
 * This configures formatting, not translated messages. Additional locales can be
 * selected at runtime through LocaleService and explicit pipe locale arguments.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Inherited
public @interface NgLocale
{
    /** Unicode locale identifier, for example {@code en-ZA}. */
    String value();

    /** Angular locale file name when different from value, e.g. {@code en} for {@code en-US}. */
    String dataLocale() default "";

    /** Include Angular's extra locale data for extended day-period formatting. */
    boolean extraData() default false;

    /** Additional Angular locale IDs to register for runtime selection. */
    String[] supportedLocales() default {};
}
