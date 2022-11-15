package com.jwebmp.core.base.angular.client.annotations.angular;

import com.jwebmp.core.base.angular.client.services.interfaces.*;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
@Repeatable(NgValidators.class)
public @interface NgValidator
{
	Class<? extends INgFormControlValidatorFunction<?>> value();
}
