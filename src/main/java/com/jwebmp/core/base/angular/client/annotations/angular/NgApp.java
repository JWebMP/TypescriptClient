package com.jwebmp.core.base.angular.client.annotations.angular;

import com.jwebmp.core.base.angular.client.services.interfaces.*;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgApp
{
	/**
	 * The boot module to call from the angular app
	 *
	 * @return
	 */
	Class<? extends INgComponent<?>> bootComponent();
	
	String name();
	
}
