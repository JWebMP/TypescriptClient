package com.jwebmp.core.base.angular.client.annotations.functions;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgAfterViewInits.class)
@Inherited
public @interface NgAfterViewInit
{
	String value();
	
	int sortOrder() default 100;
}
