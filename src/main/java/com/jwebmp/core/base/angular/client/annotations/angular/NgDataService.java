package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgDataService
{
    /**
     * The signal name
     *
     * @return
     */
    String value();

    /**
     * The listener name to apply
     *
     * @return
     */
    String listenerName() default "";

    /**
     * If the service provides data, and is fetched on request through a service provider
     *
     * @return Sets the providedIn value to 'root' or 'any'
     */
    boolean singleton() default true;

    boolean fetchOnCreate() default true;
}
