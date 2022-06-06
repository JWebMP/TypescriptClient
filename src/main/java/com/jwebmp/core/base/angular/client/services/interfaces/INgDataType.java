package com.jwebmp.core.base.angular.client.services.interfaces;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;
import com.guicedee.guicedinjection.*;
import com.guicedee.guicedinjection.representations.*;
import org.apache.commons.lang3.*;

import java.lang.reflect.*;
import java.math.*;
import java.util.*;

import static com.guicedee.guicedinjection.interfaces.ObjectBinderKeys.*;

public interface INgDataType<J extends INgDataType<J>>
		extends IComponent<J>, IJsonRepresentation<J>
{
	@Override
	default List<String> componentFields()
	{
		List<String> fields = IComponent.super.componentFields();
		if (fields == null)
		{
			fields = new ArrayList<>();
		}
		StringBuilder sb = new StringBuilder();
		Class<?> clazz = getClass();
		while (!clazz.equals(Object.class))
		{
			renderClassFields(sb, clazz);
			clazz = clazz.getSuperclass();
		}
		fields.add(sb.toString());
		return fields;
	}
	
	private void renderClassFields(StringBuilder sb, Class<?> clazz)
	{
		for (Field declaredField : clazz.getDeclaredFields())
		{
			renderFieldTS(sb, declaredField.getName(), declaredField.getType(), declaredField, false);
		}
	}
	
	default void renderFieldTS(StringBuilder out, String fieldName, Class fieldType, Field field, boolean array)
	{
		if (field.getAnnotation(JsonIgnore.class) != null)
		{
			return;
		}
		ObjectMapper mapper = GuiceContext.get(DefaultObjectMapper);
		if (Number.class.isAssignableFrom(fieldType))
		{
			out.append(" public " + fieldName + "? : number" + (array ? "[]" : "") + " = " + (array ? "[]" : "0") + ";\n");
		}
		else if (BigDecimal.class.isAssignableFrom(fieldType))
		{
			out.append(" public " + fieldName + "? : number" + (array ? "[]" : "") + " = " + (array ? "[]" : "0") + ";\n");
		}
		else if (BigInteger.class.isAssignableFrom(fieldType))
		{
			out.append(" public " + fieldName + "? : number" + (array ? "[]" : "") + " = " + (array ? "[]" : "0") + ";\n");
		}
		else if (String.class.isAssignableFrom(fieldType))
		{
			out.append(" public " + fieldName + "? : string" + (array ? "[]" : "") + " = " + (array ? "[]" : "''") + ";\n");
		}
		else if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType))
		{
			out.append(" public " + fieldName + "? : boolean" + (array ? "[]" : "") + " =" + (array ? "[]" : "false") + ";\n");
		}
		else if (IComponent.class.isAssignableFrom(fieldType))
		{
			//todo make this import the data type from the class
			//out.append(" public " + fieldName + "? : " + getTsFilename(fieldType) + "" + (array ? "[]" : "") + " = " + (array ? "[]" : "{}") + ";\n");
			out.append(" public " + fieldName + "? : any " + (array ? "[]" : "") + " = " + (array ? "[]" : "{}") + ";\n");
		}
		else if (Collection.class.isAssignableFrom(fieldType))
		{
			//get generic type
			String genericType = StringUtils.substringBetween(field.getGenericType()
			                                                       .getTypeName(), "<", ">");
			try
			{
				renderFieldTS(out, fieldName, Class.forName(genericType), field, true);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		else if (fieldType.isArray())
		{
			//get generic type
			String genericType = fieldType.arrayType()
			                              .getCanonicalName();
			try
			{
				renderFieldTS(out, fieldName, Class.forName(genericType), field, true);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		else if (Object.class.isAssignableFrom(fieldType))
		{
			out.append(" public " + fieldName + "? : any" + (array ? "[]" : "") + ";\n");
		}
	}
}
