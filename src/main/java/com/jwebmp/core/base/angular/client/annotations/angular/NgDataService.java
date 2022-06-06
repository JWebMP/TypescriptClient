package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgDataService
{
	/**
	 * The signal name
	 *
	 * @return
	 */
	String value();
	
	boolean fetchOnCreate() default true;
}
