package com.jwebmp.core.base.angular.client.services.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.guicedee.guicedinjection.GuiceContext;
import com.guicedee.services.jsonrepresentation.IJsonRepresentation;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.InvalidPathException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.guicedee.guicedinjection.interfaces.ObjectBinderKeys.DefaultObjectMapper;
import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getNgComponentReference;
import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getTsFilename;


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
			renderFieldTS(sb, getFieldName(declaredField), declaredField.getType(), declaredField, false);
		}
	}
	
	static String getFieldName(Field field)
	{
		return field.getName();
	}
	
	default void renderFieldTS(StringBuilder out, String fieldName, Class fieldType, Field field, boolean array)
	{
		if (field.getAnnotation(JsonIgnore.class) != null || Modifier.isStatic(field.getModifiers()))
		{
			return;
		}
		ObjectMapper mapper = GuiceContext.get(DefaultObjectMapper);
		
		String optionalString = "";
		if (!isFieldRequired(field))
		{
			optionalString = "?";
		}
		
		if (Number.class.isAssignableFrom(fieldType))
		{
			out.append(" public " + fieldName + optionalString + " : number" + (array ? "[]" : "") + " = " + (array ? "[]" : "0") + ";\n");
		}
		else if (BigDecimal.class.isAssignableFrom(fieldType))
		{
			out.append(" public " + fieldName + optionalString + " : number" + (array ? "[]" : "") + " = " + (array ? "[]" : "0") + ";\n");
		}
		else if (BigInteger.class.isAssignableFrom(fieldType))
		{
			out.append(" public " + fieldName + optionalString + " : number" + (array ? "[]" : "") + " = " + (array ? "[]" : "0") + ";\n");
		}
		else if (String.class.isAssignableFrom(fieldType))
		{
			out.append(" public " + fieldName + optionalString + " : string" + (array ? "[]" : "") + " = " + (array ? "[]" : "''") + ";\n");
		}
		else if (Boolean.class.isAssignableFrom(fieldType) || boolean.class.isAssignableFrom(fieldType))
		{
			out.append(" public " + fieldName + optionalString + " : boolean" + (array ? "[]" : "") + " =" + (array ? "[]" : "false") + ";\n");
		}
		else if (OffsetDateTime.class.isAssignableFrom(fieldType) ||
				LocalDateTime.class.isAssignableFrom(fieldType) ||
				ZonedDateTime.class.isAssignableFrom(fieldType) ||
				LocalDate.class.isAssignableFrom(fieldType) ||
				Date.class.isAssignableFrom(fieldType)
		)
		{
			out.append(" public " + fieldName + optionalString + " : Date" + (array ? "[]" : "") + " =" + (array ? "[]" : "new Date()") + ";\n");
		}
		else if (INgDataType.class.isAssignableFrom(fieldType))
		{
			//todo make this import the data type from the class
			//out.append(" public " + fieldName + "? : " + getTsFilename(fieldType) + "" + (array ? "[]" : "") + " = " + (array ? "[]" : "{}") + ";\n");
			String typeName = getTsFilename(fieldType);
			out.append(" public " + fieldName + optionalString + " : " + typeName + " " + (array ? "[]" : "") + " = " + (array ? "[]" : renderObjectStructure(fieldType)) + ";\n");
		}
		else if (IComponent.class.isAssignableFrom(fieldType))
		{
			//todo make this import the data type from the class
			//out.append(" public " + fieldName + "? : " + getTsFilename(fieldType) + "" + (array ? "[]" : "") + " = " + (array ? "[]" : "{}") + ";\n");
			out.append(" public " + fieldName + optionalString + " : any " + (array ? "[]" : "") + " = " + (array ? "[]" : renderObjectStructure(fieldType)) + ";\n");
		}
		else if (Collection.class.isAssignableFrom(fieldType))
		{
			//get generic type
			String genericType = StringUtils.substringBetween(field.getGenericType()
			                                                       .getTypeName(), "<", ">");
			
			if (field.getGenericType() instanceof ParameterizedType)
			{
				var arguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
				if (arguments != null)
				{
					Object arg0 = arguments[0];
					if (arg0 instanceof ParameterizedType)
					{
						ParameterizedType pType = (ParameterizedType) arguments[0];
						Class<?> arg0Clazz = pType.getRawType()
						                          .getClass();
						try
						{
							renderFieldTS(out, fieldName, arg0Clazz, field, true);
						}
						catch (InvalidPathException ipe)
						{
							Logger.getLogger("INgDataType").log(Level.SEVERE, " Unable to generate generic based class - ", ipe);
						}
					}
					else if (arg0 instanceof Class<?>)
					{
						try
						{
							renderFieldTS(out, fieldName, (Class) arg0, field, true);
						}
						catch (InvalidPathException ipe)
						{
							Logger.getLogger("INgDataType").log(Level.SEVERE, " Unable to generate generic based class - ", ipe);
						}
					}
				}
			}
			else
			{
				try
				{
					renderFieldTS(out, fieldName, Class.forName(genericType), field, true);
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
				catch (InvalidPathException ipe)
				{
					Logger.getLogger("INgDataType").log(Level.SEVERE, " Unable to generate generic based class - ", ipe);
				}
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
			out.append(" public " + fieldName + optionalString + " : any" + (array ? "[]" : "") + " = " + (array ? "[]" : renderObjectStructure(fieldType)) + ";\n");
		}
	}
	
	static boolean isFieldRequired(Field field)
	{
		return field.getAnnotation(NotNull.class) != null;
	}
	
	static StringBuilder renderObjectStructure(Class<?> o)
	{
		StringBuilder out = new StringBuilder();
		out.append("{");
		for (Field declaredField : o.getDeclaredFields())
		{
			if (out.length() != 1)
			{
				out.append(",");
			}
			if (isFieldRequired(declaredField))
			{
				out.append(getFieldName(declaredField));
				out.append(" ");
				out.append(":");
				switch (declaredField.getType()
				                     .getSimpleName())
				{
					case "String":
					case "UUID":
						out.append("''");
						break;
					case "Boolean":
						out.append("false");
						break;
					case "Integer":
					case "Double":
						out.append("0");
						break;
					case "OffsetDateTime":
					case "Date":
					case "LocalDateTime":
					case "ZonedDateTime":
					case "LocalDate":
						out.append("new Date()");
						break;
					case "List":
					case "Set":
					case "HashMap":
						out.append("[]");
						break;
					default:
						out.append(renderObjectStructure(declaredField.getType()));
				}
			}
		}
		out.append("}");
		return out;
	}
	
	@Override
	default List<NgComponentReference> getComponentReferences()
	{
		List<NgComponentReference> out = IComponent.super.getComponentReferences();
		out.addAll(renderFieldReferences(getClass()));
		
		return out;
	}
	
	private List<NgComponentReference> renderFieldReferences(Class<?> clazz)
	{
		List<NgComponentReference> out = IComponent.super.getComponentReferences();
		for (Field declaredField : clazz.getDeclaredFields())
		{
			out.addAll(renderFieldReference(declaredField.getName(), declaredField.getType(), declaredField, false));
		}
		return out;
	}
	
	default List<NgComponentReference> renderFieldReference(String fieldName, Class fieldType, Field field, boolean array)
	{
		List<NgComponentReference> refs = new ArrayList<>();
		
		if (INgDataType.class.isAssignableFrom(fieldType))
		{
			refs.add(getNgComponentReference(fieldType));
		}
		else if (Collection.class.isAssignableFrom(fieldType))
		{
			if (field.getGenericType() instanceof ParameterizedType)
			{
				var arguments = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
				if (arguments != null)
				{
					Object arg0 = arguments[0];
					if (arg0 instanceof ParameterizedType)
					{
						ParameterizedType pType = (ParameterizedType) arguments[0];
						Class<?> arg0Clazz = pType.getRawType()
						                          .getClass();
						try
						{
							refs.add(getNgComponentReference((Class<? extends IComponent<?>>) arg0Clazz));
						}
						catch (InvalidPathException ipe)
						{
							Logger.getLogger("INgDataType").log(Level.SEVERE, " Unable to generate generic based class - ", ipe);
						}
					}
					else if (arg0 instanceof Class<?> && !arg0.equals(Object.class) && !arg0.equals(String.class) && !arg0.equals(Boolean.class) && !arg0.equals(Integer.class) && !arg0.equals(Double.class))
					{
						refs.add(getNgComponentReference((Class<? extends IComponent<?>>) arg0));
					}
				}
			}
		}
		else if (fieldType.isArray())
		{
			//get generic type
			String genericType = fieldType.arrayType()
			                              .getCanonicalName();
			try
			{
				renderFieldReference(fieldName, Class.forName(genericType), field, true);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
		return refs;
	}

	@Override
	default Map<String, String> imports() {
		Map<String, String> imports = IComponent.super.imports();

		return imports;
	}
}
