package com.jwebmp.core.base.angular.client.annotations.functions;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgAfterContentInits.class)
@Inherited
public @interface NgAfterContentInit
{
	String value();
	
	int sortOrder() default 100;
}
