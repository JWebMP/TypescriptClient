package com.jwebmp.core.base.angular.client.annotations.globals;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgGlobalComponentConstructorParameters.class)
@Inherited
public @interface NgGlobalComponentConstructorParameter
{
	String value();
}
