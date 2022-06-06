package com.jwebmp.core.base.angular.client.services.interfaces;

import com.google.common.base.*;
import com.guicedee.logger.*;
import com.jwebmp.core.base.angular.client.annotations.angular.*;
import com.jwebmp.core.base.angular.client.annotations.constructors.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import com.jwebmp.core.base.angular.client.annotations.typescript.*;

import java.lang.annotation.*;
import java.util.logging.*;

public interface AnnotationUtils
{
	Logger log = LogFactory.getLog(AnnotationUtils.class);
	
	static String getTsFilename(INgApp<?> clazz)
	{
		NgApp app;
		if (!clazz.getClass()
		          .isAnnotationPresent(NgApp.class))
		{
			System.out.println("Ng App Interface without NgApp Annotation? - " + clazz.getClass()
			                                                                          .getCanonicalName());
			throw new RuntimeException("Unable to build application without base metadata");
		}
		return clazz.name();
	}
	
	static String getTsFilename(Class<?> clazz)
	{
		try
		{
			if (clazz.isAnnotationPresent(NgSourceDirectoryReference.class))
			{
				NgSourceDirectoryReference ref = clazz.getAnnotation(NgSourceDirectoryReference.class);
				if (!Strings.isNullOrEmpty(ref.name()))
				{
					return ref.name();
				}
			}
		}catch (Exception e)
		{
			log.log(Level.SEVERE, "Unable to render a ts file name for " + clazz.getCanonicalName(), e);
		}
		return clazz.getSimpleName();
	}
	
	
	public static String getTsVarName(Class<?> clazz)
	{
		String tsName = getTsFilename(clazz);
		tsName = tsName.substring(0, 1)
		               .toLowerCase() +
		         tsName.substring(1);
		return tsName;
	}
	
	static NgField getNgField(String value)
	{
		return new NgField(){
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return NgField.class;
			}
			
			@Override
			public String value()
			{
				return value;
			}
			
			@Override
			public boolean onParent()
			{
				return false;
			}
			
			@Override
			public boolean onSelf()
			{
				return true;
			}
		};
	}
	
	static NgConstructorParameter getNgConstructorParameter(String value)
	{
		return new NgConstructorParameter(){
			@Override
			public String value()
			{
				return value;
			}
			
			@Override
			public boolean onParent()
			{
				return false;
			}
			
			@Override
			public boolean onSelf()
			{
				return true;
			}
			
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return NgConstructorParameter.class;
			}
		};
	}
	
	static NgConstructorBody getNgConstructorBody(String value)
	{
		return new NgConstructorBody(){
			
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return NgConstructorBody.class;
			}
			
			@Override
			public String value()
			{
				return value;
			}
			
			@Override
			public boolean onParent()
			{
				return false;
			}
			
			@Override
			public boolean onSelf()
			{
				return true;
			}
		};
	}
	
	static NgMethod getNgMethod(String value)
	{
		return new NgMethod(){
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return NgMethod.class;
			}
			
			@Override
			public String value()
			{
				return value;
			}
			
			@Override
			public boolean onParent()
			{
				return false;
			}
			
			@Override
			public boolean onSelf()
			{
				return true;
			}
		};
	}
	
	static NgImportReference getNgImportReference(String importName, String reference)
	{
		NgImportReference ref = new NgImportReference(){
			@Override
			public String reference()
			{
				return reference;
			}
			
			@Override
			public String value()
			{
				return importName;
			}
			
			@Override
			public boolean onParent()
			{
				return false;
			}
			
			@Override
			public boolean onSelf()
			{
				return true;
			}
			
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return NgImportReference.class;
			}
		};
		return ref;
	}
	
	static NgComponentReference getNgComponentReference(Class<? extends IComponent<?>> aClass)
	{
		NgComponentReference componentReference = new NgComponentReference()
		{
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return NgComponentReference.class;
			}
			
			@Override
			public Class<? extends IComponent<?>> value()
			{
				return aClass;
			}
			
			@Override
			public boolean provides()
			{
				return false;
			}
			
			@Override
			public boolean onParent()
			{
				return false;
			}
			
			@Override
			public boolean onSelf()
			{
				return true;
			}
		};
		return componentReference;
	}
	
	static NgComponentReference getNgComponentReferenceOnParent(Class<? extends IComponent<?>> aClass)
	{
		NgComponentReference componentReference = new NgComponentReference()
		{
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return NgComponentReference.class;
			}
			
			@Override
			public Class<? extends IComponent<?>> value()
			{
				return aClass;
			}
			
			@Override
			public boolean provides()
			{
				return false;
			}
			
			@Override
			public boolean onParent()
			{
				return true;
			}
			
			@Override
			public boolean onSelf()
			{
				return false;
			}
		};
		return componentReference;
	}
}
