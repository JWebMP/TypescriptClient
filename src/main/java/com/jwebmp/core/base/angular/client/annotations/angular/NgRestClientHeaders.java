package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Container annotation for repeatable {@link NgRestClientHeader}.
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgRestClientHeaders
{
    NgRestClientHeader[] value();
}

