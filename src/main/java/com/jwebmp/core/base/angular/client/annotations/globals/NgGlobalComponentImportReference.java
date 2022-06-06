package com.jwebmp.core.base.angular.client.annotations.globals;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Repeatable(NgGlobalComponentImportReferences.class)
@Inherited
public @interface NgGlobalComponentImportReference
{
	String reference();
	String value();
}
