package com.jwebmp.core.base.angular.client.annotations.components;


import com.jwebmp.core.base.angular.client.services.*;
import com.jwebmp.core.base.angular.client.services.interfaces.*;

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
	
	Class<? extends INgDataType> type() default any.class;
	String attributeReference() default "";
	boolean renderAttributeReference() default true;
	
	boolean additionalData() default false;
	
	
	
	int sortOrder() default 100;
}
