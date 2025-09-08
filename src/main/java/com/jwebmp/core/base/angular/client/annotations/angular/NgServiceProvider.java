package com.jwebmp.core.base.angular.client.annotations.angular;

import com.jwebmp.core.base.angular.client.services.interfaces.*;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgServiceProvider
{
    Class<? extends INgDataService<?>> value();

    Class<? extends INgDataType<?>> dataType();

    String variableName();

    String referenceName();

    boolean dataArray() default false;

    boolean singleton() default false;

    boolean deepMerge() default false;
}
