package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.angular.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.structures.*;

/**
 * Override and use the fields method to render the function body, and return statement
 * @param <J>
 */
public interface INgFormGroupValidatorFunction<J extends INgFormGroupValidatorFunction<J>> extends INgFormControlValidatorFunction<J>
{
	@Override
	default StringBuilder renderFields()
	{
		StringBuilder sb = new StringBuilder();
		return sb;
	}
	
	@Override
	default boolean exportsClass()
	{
		return true;
	}
}
