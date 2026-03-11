package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Container annotation for repeatable {@link NgRestClientQueryParam}.
 */
@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgRestClientQueryParams
{
    NgRestClientQueryParam[] value();
}

