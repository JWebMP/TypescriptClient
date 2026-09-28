package com.jwebmp.core.base.angular.client.annotations.angular;

import com.jwebmp.core.base.angular.client.services.interfaces.INgComponent;
import com.jwebmp.core.base.angular.client.annotations.angularconfig.NgBuildConfiguration;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgApp
{
    /**
     * The boot module to call from the angular app
     *
     * @return
     */
    Class<? extends INgComponent<?>> bootComponent();

    String value();

    /**
     * Overrides for angular.json's build.configurations.production.
     */
    NgBuildConfiguration production() default @NgBuildConfiguration;

    /**
     * Overrides for angular.json's build.configurations.development.
     */
    NgBuildConfiguration development() default @NgBuildConfiguration;

}
