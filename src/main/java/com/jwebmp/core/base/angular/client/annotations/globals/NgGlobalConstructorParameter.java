package com.jwebmp.core.base.angular.client.annotations.globals;

import com.jwebmp.core.base.angular.client.annotations.boot.*;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgGlobalConstructorParameters.class)
@Inherited
public @interface NgGlobalConstructorParameter
{
	String value();
}
