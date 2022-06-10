package com.jwebmp.core.base.angular.client.annotations.components;


import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgInputs.class)
@Inherited
public @interface NgInput
{
	String value();
	
	boolean additionalData() default false;
	
	int sortOrder() default 100;
}
