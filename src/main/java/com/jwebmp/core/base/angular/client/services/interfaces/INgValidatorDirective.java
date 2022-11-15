package com.jwebmp.core.base.angular.client.services.interfaces;

import com.fasterxml.jackson.databind.introspect.*;
import com.jwebmp.core.base.angular.client.annotations.angular.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.structures.*;

import java.util.*;

/**
 * Annotate this class with a @NgValidator
 * @param <J>
 */
@NgImportReference(value = "AbstractControl, ValidationErrors, ValidatorFn,NG_VALIDATORS,Validator", reference = "@angular/forms")
@NgInterface("Validator")
//@NgValidator(INgFormControlValidatorFunction.class)
public interface INgValidatorDirective<J extends INgValidatorDirective<J>> extends INgDirective<J>
{
	@Override
	default List<String> providers()
	{
		List<String> out = INgDirective.super.providers();
		out.add("""
		            {
		                provide: NG_VALIDATORS,
		                useExisting:""" +AnnotationUtils.getTsFilename(getClass()) + """
,
		                multi: true
		            }""");
		return out;
	}
	
	
	
	@Override
	default List<String> methods()
	{
		List<String> out = INgDirective.super.methods();
		NgValidator annotation = getClass().getAnnotation(NgValidator.class);
		if (annotation == null)
		{
			System.out.println("Invalid validator directive, requires a @NgValidator annotation");
			return out;
		}
		out.add("""
		        validate(control: AbstractControl): ValidationErrors | null {
		                return  """ + " " + AnnotationUtils.getTsFilename(annotation.value()) + """
              (control);
		            }""");
		return out;
	}
}
