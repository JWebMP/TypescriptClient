package com.jwebmp.core.base.angular.client.annotations.references;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgImportReferences.class)
@Inherited
public @interface NgImportReference
{
	String reference();
	String value();
	
	boolean onParent() default false;
	boolean onSelf() default true;
}
