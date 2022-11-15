package com.jwebmp.core.base.angular.client.annotations.components;


import com.jwebmp.core.base.angular.client.services.*;
import com.jwebmp.core.base.angular.client.services.interfaces.*;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgComponentTagAttributes.class)
@Inherited
public @interface NgComponentTagAttribute
{
	String key();
	String value();
	int sortOrder() default 100;
}
