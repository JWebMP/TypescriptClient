package com.jwebmp.core.base.angular.client.annotations.routing;

import com.jwebmp.core.base.angular.client.services.interfaces.*;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgRouteData
{
	String dataVariableName();
	
	Class<? extends INgDataService<?>> dataVariableType();
}
