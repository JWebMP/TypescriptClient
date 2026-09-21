package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface NgTranslationSources
{
    NgTranslationSource[] value();
}
