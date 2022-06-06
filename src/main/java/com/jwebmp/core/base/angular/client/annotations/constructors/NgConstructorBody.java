package com.jwebmp.core.base.angular.client.annotations.constructors;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgConstructorBodys.class)
@Inherited
public @interface NgConstructorBody
{
	String value();
	
	boolean onParent() default false;
	boolean onSelf() default true;
}
