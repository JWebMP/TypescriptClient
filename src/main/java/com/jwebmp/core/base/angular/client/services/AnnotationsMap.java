package com.jwebmp.core.base.angular.client.services;

import com.guicedee.guicedinjection.*;
import com.guicedee.logger.*;
import com.jwebmp.core.base.angular.client.annotations.angular.*;
import com.jwebmp.core.base.angular.client.annotations.angularconfig.*;
import com.jwebmp.core.base.angular.client.annotations.boot.*;
import com.jwebmp.core.base.angular.client.annotations.components.*;
import com.jwebmp.core.base.angular.client.annotations.constructors.*;
import com.jwebmp.core.base.angular.client.annotations.functions.*;
import com.jwebmp.core.base.angular.client.annotations.globals.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.routing.*;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import io.github.classgraph.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.*;

public class AnnotationsMap
{
	private static final Logger log = LogFactory.getLog(AnnotationsMap.class);
	private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngConfigs
			= Map.of(
			NgAsset.class, NgAssets.class,
			NgScript.class, NgScripts.class,
			NgStyleSheet.class, NgStyleSheets.class,
			NgPolyfill.class, NgPolyfills.class
	);
	
	private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngEvents
			= Map.of(
			NgOnDestroy.class, NgOnDestroys.class,
			NgOnInit.class, NgOnInits.class,
			NgAfterViewInit.class,NgAfterViewInits.class,
			NgAfterViewChecked.class,NgAfterViewCheckeds.class,
			NgAfterContentInit.class,NgAfterContentInits.class,
			NgAfterContentChecked.class,NgAfterContentCheckeds.class
	);
	
	private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngComponents
			= Map.of(
			NgInput.class, NgInputs.class,
			NgComponentTagAttribute.class, NgComponentTagAttributes.class,
			NgOutput.class, NgOutputs.class,
			NgGlobalComponentConstructorParameter.class,NgGlobalComponentConstructorParameters.class,
			NgGlobalConstructorParameter.class,NgGlobalConstructorParameters.class,
			NgGlobalComponentImportReference.class, NgGlobalComponentImportReferences.class
	);
	
	private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngBootConfigs
			= Map.of(
			NgBootDeclaration.class, NgBootDeclarations.class,
			NgBootModuleImport.class, NgBootModuleImports.class,
			NgBootImportReference.class, NgBootImportReferences.class,
			NgBootProvider.class, NgBootProviders.class,
			NgBootConstructorBody.class, NgBootConstructorBodys.class,
			NgBootConstructorParameter.class, NgBootConstructorParameters.class,
			NgBootGlobalField.class, NgBootGlobalFields.class,
			NgBootModuleSchema.class, NgBootModuleSchemas.class
	);
	private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngReferences
			= Map.of(
			NgComponentReference.class, NgComponentReferences.class,
			NgImportProvider.class, NgImportProviders.class,
			NgImportReference.class, NgImportReferences.class,
			NgDataTypeReference.class, NgDataTypeReferences.class
	);
	private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngClassStructures
			= Map.of(
			NgConstructorBody.class, NgConstructorBodys.class,
			NgConstructorParameter.class, NgConstructorParameters.class,
			NgField.class, NgFields.class,
			NgInterface.class, NgInterfaces.class,
			NgMethod.class, NgMethods.class,
			NgValidator.class,NgValidators.class
	);
	
	private static final Map<Class<? extends Annotation>, Class<? extends Annotation>> ngAllMultiples = new HashMap<>();
	
	static
	{
		ngAllMultiples.putAll(ngConfigs);
		ngAllMultiples.putAll(ngEvents);
		ngAllMultiples.putAll(ngBootConfigs);
		ngAllMultiples.putAll(ngReferences);
		ngAllMultiples.putAll(ngClassStructures);
		ngAllMultiples.putAll(ngComponents);
	}
	
	public static final List<Class<? extends Annotation>> annotations
			= List.of(
			NgAsset.class,
			NgScript.class,
			NgStyleSheet.class,
			NgPolyfill.class,
			NgOnDestroy.class,
			NgBootDeclaration.class,
			NgBootModuleImport.class,
			NgBootModuleSchema.class,
			NgBootImportReference.class,
			NgBootProvider.class,
			NgBootConstructorParameter.class,
			NgBootConstructorBody.class,
			NgBootGlobalField.class,
			NgComponentReference.class,
			NgImportProvider.class,
			NgImportReference.class,
			NgConstructorBody.class,
			NgConstructorParameter.class,
			NgField.class,
			NgInterface.class,
			NgMethod.class,
			NgApp.class,
			NgComponent.class,
			NgDataService.class,
			NgDataType.class,
			NgDataTypeReference.class,
			NgDirective.class,
			NgModule.class,
			NgProvider.class,
			NgServiceProvider.class,
			NgRoutable.class,
			NgRouteData.class,
			
			NgOnInit.class,
			NgOnDestroy.class,
			NgAfterViewInit.class,
			NgAfterViewChecked.class,
			NgAfterContentInit.class,
			NgAfterContentChecked.class,
			
			NgInput.class,
			NgComponentTagAttribute.class,
			NgOutput.class,
			
			NgGlobalConstructorParameter.class,
			NgGlobalComponentConstructorParameter.class,
			NgGlobalComponentImportReference.class,
			
			NgValidator.class
	);
	
	private Class<?> clazz;
	private Map<Class<? extends Annotation>, List<Annotation>> annotationsMapping = new HashMap<>();
	
	private static Map<Class<?>, AnnotationsMap> annoMap = new HashMap<>();
	
	public static AnnotationsMap getAnnotationMap(Class<?> clazz)
	{
		AnnotationsMap mappy = null;
		if (!getAnnoMap().containsKey(clazz))
		{
			mappy = new AnnotationsMap(clazz);
			getAnnoMap().put(clazz, mappy);
		}
		else
		{
			mappy = getAnnoMap().get(clazz);
		}
		return mappy;
	}
	
	public static <A extends Annotation> List<A> getAnnotations(Class<?> clazz, Class<A> annotation)
	{
		AnnotationsMap mappy = getAnnotationMap(clazz);
		var out = (List<A>) mappy.annotationsMapping.get(annotation);
		if (out == null)
		{
			return new ArrayList<>();
		}
		return out;
	}

	private AnnotationsMap(Class<?> clazz)
	{
		this.clazz = clazz;
		readClass();
	}
	
	private void readClass()
	{
		if(!annoMap.containsKey(clazz))
		{
			readAnnotations(clazz);
			readSubclassHierarchy(clazz);
			readInterfaceHierarchy(clazz);
		}
	}
	
	private void readSubclassHierarchy(Class<?> clazz)
	{
		Class<?> clazzes = clazz.getSuperclass();
		while (clazzes != Object.class)
		{
			readAnnotations(clazzes);
			readInterfaceHierarchy(clazzes);
			clazzes = clazzes.getSuperclass();
		}
	}
	
	private void readInterfaceHierarchy(Class<?> clazz)
	{
		Class<?>[] clazzes = clazz.getInterfaces();
		for (Class<?> aClass : clazzes)
		{
			readAnnotations(aClass);
			readInterfaceHierarchy(aClass);
		}
	}
	
	private void readAnnotations(Class<?> clazz)
	{
		Annotation[] annos = clazz.getAnnotations();
		for (Annotation anno : annos)
		{
			if (ngAllMultiples.containsValue(anno.annotationType()))
			{
				var key = getKey(ngAllMultiples, anno.annotationType());
				var allAnnos = getListOfAnnotations(clazz, key, ngAllMultiples.get(key));
				for (Annotation allAnno : allAnnos)
				{
					addAnnotation(allAnno);
					readAnnotations(allAnno.getClass());
				}
			}
			if (annotations.contains(anno.annotationType()))
			{
				addAnnotation(anno);
				readAnnotations(anno.annotationType());
			}
		}
	}
	
	public <K, V> K getKey(Map<K, V> map, V value)
	{
		for (Map.Entry<K, V> entry : map.entrySet())
		{
			if (entry.getValue()
			         .equals(value))
			{
				return entry.getKey();
			}
		}
		return null;
	}
	
	public void addAnnotation(Annotation source)
	{
		if (!annotationsMapping.containsKey(source.annotationType()))
		{
			annotationsMapping.put(source.annotationType(), new ArrayList<>());
		}
		annotationsMapping.get(source.annotationType())
		                  .add(source);
	}
	
	private <T extends Annotation> List<T> getListOfAnnotations(Class<?> clazz, Class<T> singularAnnotation, Class<? extends Annotation> multipleAnnotation)
	{
		List<T> out = new ArrayList<>();
		if (clazz.isAnnotationPresent(multipleAnnotation))
		{
			Annotation refAnnotation = clazz.getAnnotation(multipleAnnotation);
			try
			{
				Method valueMethod = refAnnotation.annotationType()
				                                  .getDeclaredMethod("value");
				Annotation[] result = (Annotation[]) valueMethod.invoke(refAnnotation);
				for (Annotation annotation : result)
				{
					out.add((T) annotation);
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE,"Cannot read multiple annotations - " + clazz + " - " + multipleAnnotation,e);
			}
		}
		if (clazz.isAnnotationPresent(singularAnnotation))
		{
			out.add(clazz.getAnnotation(singularAnnotation));
		}
		return out;
	}
	
	public static <T extends Annotation> List<T> getAllAnnotations(Class<T> annotation)
	{
		List<T> annos = new ArrayList<>();
		getAnnoMap().forEach((key, value) -> {
			value.annotationsMapping.forEach((key2, value2) -> {
				for (Annotation annotation1 : value2)
				{
					if (annotation1.annotationType()
					               .equals(annotation))
					{
						annos.add((T) annotation1);
					}
				}
			});
		});
		
		return annos;
	}
	
	public static Map<Class<?>, AnnotationsMap> getAnnoMap()
	{
		return annoMap;
	}
	
	public static void loadAllClasses()
	{
		for (Class<? extends Annotation> annotation : annotations)
		{
			for (ClassInfo allClass : GuiceContext.instance()
			                                      .getScanResult()
			                                      .getClassesWithAnnotation(annotation))
			{
				try
				{
					getAnnotationMap(allClass.loadClass());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		for (Class<? extends Annotation> annotation : ngAllMultiples.values())
		{
			for (ClassInfo allClass : GuiceContext.instance()
			                                      .getScanResult()
			                                      .getClassesWithAnnotation(annotation))
			{
				try
				{
					getAnnotationMap(allClass.loadClass());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
}
