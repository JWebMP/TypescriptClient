package com.jwebmp.core.base.angular.client.annotations.routing;

import com.jwebmp.core.base.angular.client.annotations.references.*;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Target({TYPE})
@Retention(RUNTIME)
@Inherited
@NgImportReference(value = "RouterModule, ParamMap,Router", reference = "@angular/router")
public @interface NgRoutable
{
	String path();
	
	String redirectTo() default "";
	
	String pathMatch() default "";
	
	/**
	 * Only one parent allowed, set as an array to not enforce being set
	 *
	 * @return
	 */
	Class<? extends com.jwebmp.core.base.angular.client.services.interfaces.IComponent<?>>[] parent() default {};
	
	int sortOrder() default 100;
	
	boolean ignoreComponent() default false;
}
