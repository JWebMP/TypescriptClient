package com.jwebmp.core.base.angular.client.annotations.angular;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
public @interface NgDataType
{
	DataTypeClass value() default DataTypeClass.Class;
	
	boolean exports() default true;
	
	String name() default "";
	
	enum DataTypeClass
	{
		Class,
		Function,
		Enum,
		AbstractClass,
		Interface,
		Const,
		;
		
		public String description()
		{
			return name().toLowerCase();
		}
	}
	
	/**
	 * If a function what is the return type, after the :
	 * @return
	 */
	String returnType() default "";
}
