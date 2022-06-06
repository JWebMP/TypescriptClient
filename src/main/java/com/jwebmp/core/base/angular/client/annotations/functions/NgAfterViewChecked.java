package com.jwebmp.core.base.angular.client.annotations.functions;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgAfterViewCheckeds.class)
@Inherited
public @interface NgAfterViewChecked
{
	String value();
	
	int sortOrder() default 100;
}
