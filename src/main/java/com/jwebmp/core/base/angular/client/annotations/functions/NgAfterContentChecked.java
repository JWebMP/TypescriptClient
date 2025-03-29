package com.jwebmp.core.base.angular.client.annotations.functions;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgAfterContentCheckeds.class)
@Inherited
public @interface NgAfterContentChecked
{
    String value();

    int sortOrder() default 100;

    boolean onParent() default false;

    boolean onSelf() default true;
}
