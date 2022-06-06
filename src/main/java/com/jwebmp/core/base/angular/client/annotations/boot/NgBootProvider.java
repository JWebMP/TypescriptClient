package com.jwebmp.core.base.angular.client.annotations.boot;


import com.jwebmp.core.base.angular.client.annotations.angular.*;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgBootProviders.class)
@Inherited
@NgProvider
public @interface NgBootProvider
{
	String value();
	
	boolean onParent() default false;
	boolean onSelf() default true;
}
