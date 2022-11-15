package com.jwebmp.core.base.angular.client.services.interfaces;

import com.google.common.base.*;
import com.guicedee.guicedinjection.interfaces.*;
import com.jwebmp.core.base.angular.client.annotations.angular.*;
import com.jwebmp.core.base.angular.client.annotations.constructors.*;
import com.jwebmp.core.base.angular.client.annotations.globals.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import com.jwebmp.core.base.angular.client.services.*;
import com.jwebmp.core.base.angular.client.services.spi.*;

import java.io.*;
import java.util.*;

import static com.jwebmp.core.base.angular.client.services.AnnotationsMap.*;
import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.*;

public interface IComponent<J extends IComponent<J>> extends IDefaultService<J>, ImportsStatementsComponent<J>
{
	ThreadLocal<File> currentAppFile = ThreadLocal.withInitial(() -> null);
	
	static ThreadLocal<File> getCurrentAppFile()
	{
		return currentAppFile;
	}
	
	default String renderBeforeClass()
	{
		return "";
	}
	
	default String renderAfterClass()
	{
		return "";
	}
	
	default boolean exportsClass()
	{
		return true;
	}
	
	default boolean includeInBootModule()
	{
		return false;
	}
	
	default List<NgField> getAllFields()
	{
		List<NgField> out = new ArrayList<>();
		for (NgField annotation : getAnnotations(getClass(), NgField.class))
		{
			if (annotation.onSelf())
			{
				out.add(annotation);
			}
		}
		for (NgComponentReference annotation : getAnnotations(getClass(), NgComponentReference.class))
		{
			Class<?> reference = annotation.value();
			for (NgField ngField : getAnnotations(reference, NgField.class))
			{
				if (ngField.onParent())
				{
					out.add(ngField);
				}
			}
		}
		
		for (String componentField : componentFields())
		{
			out.add(getNgField(componentField));
		}
		for (String field : fields())
		{
			out.add(getNgField(field));
		}
		
		Set<OnGetAllFields> interceptors = IDefaultService.loaderToSet(ServiceLoader.load(OnGetAllFields.class));
		for (OnGetAllFields interceptor : interceptors)
		{
			interceptor.perform(out, this);
		}
		return out;
	}
	
	default List<NgConstructorParameter> getAllConstructorParameters()
	{
		List<NgConstructorParameter> out = new ArrayList<>();
		
		for (NgConstructorParameter annotation : getAnnotations(getClass(), NgConstructorParameter.class))
		{
			if (annotation.onSelf())
			{
				out.add(annotation);
			}
		}
		
		
		List<NgGlobalConstructorParameter> allGlobals = getAllAnnotations(NgGlobalConstructorParameter.class);
		for (NgGlobalConstructorParameter global : allGlobals)
		{
			NgConstructorParameter param = getNgConstructorParameter(global.value());
			out.add(param);
		}
		
		//check references for constructors needed
		for (NgComponentReference annotation : getAnnotations(getClass(), NgComponentReference.class))
		{
			if (annotation.provides() && !INgServiceProvider.class.isAssignableFrom(annotation.value()))
			{
				Class<?> referencedClass = annotation.value();
				NgConstructorParameter serviceProviderParameter = getNgConstructorParameter("public " + getTsVarName(referencedClass) + " : " + getTsFilename(referencedClass));
				out.add(serviceProviderParameter);
			}
			if (INgServiceProvider.class.isAssignableFrom(annotation.value()))
			{
				Class<?> referencedClass = annotation.value();
				if (referencedClass.isAnnotationPresent(NgServiceProvider.class))
				{
					NgServiceProvider provider = referencedClass.getAnnotation(NgServiceProvider.class);
					NgConstructorParameter serviceProviderParameter = getNgConstructorParameter("public " + provider.referenceName() + " : " + getTsFilename(referencedClass));
					out.add(serviceProviderParameter);
				}
			}
			//get component referenced constructor parameters that say on parem
			Class<?> referencedClass = annotation.value();
			for (NgConstructorParameter ngConstructorParameter : getAnnotations(referencedClass, NgConstructorParameter.class))
			{
				if (ngConstructorParameter.onParent())
				{
					out.add(ngConstructorParameter);
				}
			}
		}
		
		for (String componentConstructorParameter : componentConstructorParameters())
		{
			out.add(getNgConstructorParameter(componentConstructorParameter));
		}
		for (String constructorParameter : constructorParameters())
		{
			out.add(getNgConstructorParameter(constructorParameter));
		}
		
		
		Set<OnGetAllConstructorParameters> interceptors = IDefaultService.loaderToSet(ServiceLoader.load(OnGetAllConstructorParameters.class));
		for (OnGetAllConstructorParameters interceptor : interceptors)
		{
			interceptor.perform(out, this);
		}
		
		return out;
	}
	
	default List<NgConstructorBody> getAllConstructorBodies()
	{
		List<NgConstructorBody> out = new ArrayList<>();
		for (NgConstructorBody annotation : getAnnotations(getClass(), NgConstructorBody.class))
		{
			if (annotation.onSelf())
			{
				out.add(annotation);
			}
		}
		
		//check references for constructors needed
		for (NgComponentReference annotation : getAnnotations(getClass(), NgComponentReference.class))
		{
			Class<?> clazz = annotation.value();
			for (NgConstructorBody ngConstructorBody : getAnnotations(clazz, NgConstructorBody.class))
			{
				if (ngConstructorBody.onParent())
				{
					out.add(ngConstructorBody);
				}
			}
		}
		
		for (String body : componentConstructorBody())
		{
			out.add(getNgConstructorBody(body));
		}
		for (String body : constructorBody())
		{
			out.add(getNgConstructorBody(body));
		}
		
		Set<OnGetAllConstructorBodies> interceptors = IDefaultService.loaderToSet(ServiceLoader.load(OnGetAllConstructorBodies.class));
		for (OnGetAllConstructorBodies interceptor : interceptors)
		{
			interceptor.perform(out, this);
		}
		
		return out;
	}
	
	default List<NgMethod> getAllMethods()
	{
		List<NgMethod> out = new ArrayList<>();
		for (NgMethod annotation : getAnnotations(getClass(), NgMethod.class))
		{
			if (annotation.onSelf())
			{
				out.add(annotation);
			}
		}
		for (NgComponentReference annotation : getAnnotations(getClass(), NgComponentReference.class))
		{
			Class<?> reference = annotation.value();
			for (NgMethod ngMethod : getAnnotations(reference, NgMethod.class))
			{
				if (ngMethod.onParent())
				{
					out.add(ngMethod);
				}
			}
		}
		
		for (String componentMethod : componentMethods())
		{
			out.add(getNgMethod(componentMethod.trim()));
		}
		
		for (String componentMethod : methods())
		{
			out.add(getNgMethod(componentMethod.trim()));
		}
		
		return out;
	}
	
	
	//***************************************************************************************
	//***************************************************************************************
	// Renderers
	//***************************************************************************************
	
	default StringBuilder renderImports()
	{
		StringBuilder sb = new StringBuilder();
		List<NgImportReference> refs = getAllImportAnnotations();
		refs = clean(refs);
		refs.forEach((ref) -> {
			if (!ref.value()
			        .startsWith("!"))
			{
				sb.append(String.format(importString, ref.value(), ref.reference()));
			}
			else
			{
				sb.append(String.format(importPlainString, ref.value()
				                                              .substring(1), ref.reference()));
			}
		});
		return sb;
	}
	
	default StringBuilder renderClassTs() throws IOException
	{
		StringBuilder out = new StringBuilder();
		out.append(renderImports());
		@SuppressWarnings("unchecked")
		J component = (J) this;
		
		if (!Strings.isNullOrEmpty(component.renderBeforeClass()))
		{
			out.append(component.renderBeforeClass());
		}
		
		for (String globalField : globalFields())
		{
			out.append(globalField)
			   .append("\n");
		}
		
		for (String decorator : componentDecorators())
		{
			out.append(decorator)
			   .append("\n");
		}
		for (String decorator : decorators())
		{
			out.append(decorator)
			   .append("\n");
		}
		
		out.append(renderClassDefinition());
		
		if (!Strings.isNullOrEmpty(component.renderAfterClass()))
		{
			out.append(";")
			   .append(component.renderAfterClass());
		}
		
		return out;
	}
	
	default StringBuilder renderClassDefinition()
	{
		StringBuilder out = new StringBuilder();
		out.append(exportsClass() ? "export " : "");
		
		List<NgDataType> cType = getAnnotations(getClass(), NgDataType.class);
		if (!cType.isEmpty())
		{
			out.append(cType.get(0)
			                .value()
			                .description())
			   .append(" ");
			String functionName = cType.get(0)
			                           .name();
			if(!Strings.isNullOrEmpty(cType.get(0).returnType()))
			{
				if (!Strings.isNullOrEmpty(functionName))
				{
					out.append(" " + functionName + " ");
				}
				else {
					out.append(" " + getTsFilename(getClass()) + " ");
				}
				out.append( "() :  ")
				   .append(cType.get(0)
				                .returnType());
			}
		}
		else
		{
			out.append("class ");
		}
		out.append(getTsFilename(getClass()));
		
		if (!Strings.isNullOrEmpty(ofType()))
		{
			out.append(" ")
			   .append(ofType());
		}
		
		out.append(renderInterfaces());
		
		out.append("\n");
		out.append(renderClassBody());
		return out;
	}
	
	/**
	 * Renders after the class statement
	 * @return
	 */
	default StringBuilder renderAfterClassEntry()
	{
		StringBuilder out = new StringBuilder();
		return out;
	}
	default StringBuilder renderBeforeClassBodyEnd()
	{
		StringBuilder out = new StringBuilder();
		return out;
	}
	default StringBuilder renderClassBody()
	{
		StringBuilder out = new StringBuilder();
		out.append("{\n");
		out.append(renderAfterClassEntry());
		out.append(renderFields());
		out.append(renderConstructor());
		out.append(renderMethods());
		out.append(renderBeforeClassBodyEnd());
		out.append("}\n");
		
		return out;
	}
	
	default StringBuilder renderInterfaces()
	{
		StringBuilder out = new StringBuilder();
		Set<String> ints = new HashSet<>(interfaces());
		List<NgInterface> interfacs = getAnnotations(getClass(), NgInterface.class);
		for (NgInterface interfac : interfacs)
		{
			if (interfac.onSelf())
			{
				ints.add(interfac.value());
			}
		}
		ints.addAll(componentInterfaces());
		
		if (!ints.isEmpty())
		{
			StringBuilder sbInterfaces = new StringBuilder();
			sbInterfaces.append(" implements ");
			for (String interf : ints)
			{
				sbInterfaces.append(interf)
				            .append(",");
			}
			sbInterfaces.deleteCharAt(sbInterfaces.length() - 1);
			out.append(sbInterfaces);
		}
		return out;
	}
	
	default StringBuilder renderFields()
	{
		StringBuilder out = new StringBuilder();
		Set<String> fStrings = new LinkedHashSet<>();
		List<NgField> fAnno = getAllFields();
		for (NgField ngField : fAnno)
		{
			fStrings.add(ngField.value());
		}
		for (String field : fStrings)
		{
			out.append("\t")
			   .append(field)
			   .append("\n");
		}
		return out;
	}
	
	default StringBuilder renderConstructorParameters()
	{
		StringBuilder out = new StringBuilder();
		List<NgConstructorParameter> allParameters = getAllConstructorParameters();
		Set<String> constructorParameters = new LinkedHashSet<>();
		for (NgConstructorParameter allParameter : allParameters)
		{
			constructorParameters.add(allParameter.value());
		}
		
		if (!constructorParameters.isEmpty())
		{
			for (String constructorParameter : constructorParameters)
			{
				String param = constructorParameter.trim();
				if (!param.endsWith(","))
				{
					param += ",";
				}
				param += " ";
				out.append(param);
			}
			if (out.length() > 1)
			{
				out.deleteCharAt(out.lastIndexOf(", "));
			}
		}
		return out;
	}
	
	default StringBuilder renderConstructorBody()
	{
		StringBuilder out = new StringBuilder();
		List<NgConstructorBody> allConstructorBodies = getAllConstructorBodies();
		Set<String> constructorBodies = new LinkedHashSet<>();
		for (NgConstructorBody allConstructorBody : allConstructorBodies)
		{
			constructorBodies.add(allConstructorBody.value()
			                                        .trim());
		}
		
		for (String constructorBody : constructorBodies)
		{
			out.append("\t")
			   .append(constructorBody)
			   .append("\n");
		}
		
		
		return out;
	}
	
	default StringBuilder renderConstructor()
	{
		StringBuilder out = new StringBuilder();
		
		String constructorParametersString = renderConstructorParameters().toString();
		String constructorBodyString = renderConstructorBody().toString();
		
		if (!Strings.isNullOrEmpty(constructorParametersString.toString()) || !Strings.isNullOrEmpty(constructorBodyString))
		{
			out.append("\tconstructor( ");
			out.append(constructorParametersString);
			out.append(")\n");
			
			out.append("\t{\n");
			out.append(constructorBodyString);
			out.append("\t}\n");
		}
		
		return out;
	}
	
	default StringBuilder renderMethods()
	{
		StringBuilder out = new StringBuilder();
		List<NgMethod> allMethods = getAllMethods();
		Set<String> methodStrings = new LinkedHashSet<>();
		for (NgMethod allMethod : allMethods)
		{
			methodStrings.add(allMethod.value());
		}
		for (String methods : methodStrings)
		{
			out.append(methods)
			   .append("\n");
		}
		return out;
	}
	
	default List<String> componentConstructorParameters()
	{
		return new ArrayList<>();
	}
	
	default List<String> constructorParameters()
	{
		List<String> parms = new ArrayList<>();
		List<NgComponentReference> compRefs = getAnnotations(getClass(), NgComponentReference.class);
		for (NgComponentReference compRef : compRefs)
		{
			if (compRef.provides() && compRef.onSelf())
			{
				if (INgServiceProvider.class.isAssignableFrom(compRef.value()))
				{
					parms.add("public " + getTsVarName(compRef.value()) + " : " + getTsFilename(compRef.value()) + "");
				}
				else
				{
					parms.add("public " + getTsVarName(compRef.value()) + " : " + getTsFilename(compRef.value()) + "");
				}
			}
		}
		return parms;
	}
	
	default List<Class<? extends NgDataType>> types()
	{
		return new ArrayList<>();
	}
	
	default List<String> componentConstructorBody()
	{
		List<String> constructorBodies = new ArrayList<>();
		return constructorBodies;
	}
	
	default List<String> constructorBody()
	{
		return new ArrayList<>();
	}
	
	default List<String> componentMethods()
	{
		List<String> list = new ArrayList<>();
		list.add(renderOnInitMethod());
		list.add(renderOnDestroyMethod());
		list.add(renderAfterViewInit());
		list.add(renderAfterViewChecked());
		list.add(renderAfterContentInit());
		list.add(renderAfterContentChecked());
		return list;
	}
	
	default String renderOnInitMethod()
	{
		StringBuilder out = new StringBuilder();
		return out.toString();
	}
	
	default String renderOnDestroyMethod()
	{
		StringBuilder out = new StringBuilder();
		return out.toString();
	}
	
	default String renderAfterViewInit()
	{
		StringBuilder out = new StringBuilder();
		
		return out.toString();
	}
	
	default String renderAfterViewChecked()
	{
		StringBuilder out = new StringBuilder();
		
		return out.toString();
	}
	
	default String renderAfterContentInit()
	{
		StringBuilder out = new StringBuilder();
		return out.toString();
	}
	
	default String renderAfterContentChecked()
	{
		StringBuilder out = new StringBuilder();
		
		return out.toString();
	}
	
	//***********************************************************
	// The lifecycle of angular objects
	//***********************************************************
	
	default List<String> componentOnInit()
	{
		return new ArrayList<>();
	}
	
	default List<String> componentOnDestroy()
	{
		return new ArrayList<>();
	}
	
	default List<String> componentAfterViewInit()
	{
		return new ArrayList<>();
	}
	
	default List<String> componentAfterViewChecked()
	{
		return new ArrayList<>();
	}
	
	default List<String> componentAfterContentChecked()
	{
		return new ArrayList<>();
	}
	
	default List<String> componentAfterContentInit()
	{
		return new ArrayList<>();
	}
	
	default List<String> onInit()
	{
		return new ArrayList<>();
	}
	
	default List<String> onDestroy()
	{
		return new ArrayList<>();
	}
	
	default List<String> afterViewInit()
	{
		return new ArrayList<>();
	}
	
	default List<String> afterViewChecked()
	{
		return new ArrayList<>();
	}
	
	default List<String> afterContentChecked()
	{
		return new ArrayList<>();
	}
	
	default List<String> afterContentInit()
	{
		return new ArrayList<>();
	}
	
	
	//***********************************************************
	// The default stuff
	//***********************************************************
	
	
	default List<String> methods()
	{
		return new ArrayList<>();
	}
	
	default List<String> globalFields()
	{
		return new ArrayList<>();
	}
	
	default List<String> componentFields()
	{
		return new ArrayList<>();
	}
	
	default List<String> fields()
	{
		return new ArrayList<>();
	}
	
	default String ofType()
	{
		return "";
	}
	
	default List<String> componentInterfaces()
	{
		return new ArrayList<>();
	}
	
	default List<String> interfaces()
	{
		return new ArrayList<>();
	}
	
	default List<String> componentDecorators()
	{
		return new ArrayList<>();
	}
	
	default List<String> decorators()
	{
		return new ArrayList<>();
	}
	
	// Component Reference Location Assists
	public static String getClassDirectory(Class<?> clazz)
	{
		return clazz.getPackageName()
		            .replaceAll("\\.", "/");
	}
	
}
