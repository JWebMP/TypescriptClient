package com.jwebmp.core.base.angular.client.annotations.functions;

import com.jwebmp.core.base.angular.client.annotations.angular.NgRestClient;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgRestClients {
    /**
     * The string name of the dev dependency for the given ng app
     *
     * @return
     */
    NgRestClient[] value();
}
