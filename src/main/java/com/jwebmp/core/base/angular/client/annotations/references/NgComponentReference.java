package com.jwebmp.core.base.angular.client.annotations.references;

import com.jwebmp.core.base.angular.client.services.interfaces.*;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgComponentReferences.class)
@Inherited
public @interface NgComponentReference
{
    Class<? extends IComponent<?>> value();

    boolean provides() default false;

    boolean onParent() default false;

    boolean onSelf() default true;
}
