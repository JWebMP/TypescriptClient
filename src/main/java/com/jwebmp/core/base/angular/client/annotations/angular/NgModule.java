package com.jwebmp.core.base.angular.client.annotations.angular;

import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
@NgImportReference(value = "NgModule", reference = "@angular/core")
public @interface NgModule
{
    /**
     * The name of the .ts file to render
     *
     * @return
     */
    String value() default "";

    boolean renderInAngularBootModule() default true;
}
