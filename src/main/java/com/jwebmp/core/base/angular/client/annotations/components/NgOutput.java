package com.jwebmp.core.base.angular.client.annotations.components;


import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgOutputs.class)
@Inherited
public @interface NgOutput
{
	String value();
	String parentMethodName() default "";
	
	int sortOrder() default 100;
}
