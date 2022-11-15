package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.angular.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.typescript.*;

import java.util.*;

@TsDependency(value = "@angular/common", version = "^13.3.0")
@TsDependency(value = "@angular/compiler", version = "^13.3.0")
@TsDependency(value = "@angular/core", version = "^13.3.0")
@TsDependency(value = "@angular/forms", version = "^13.3.0")
@TsDependency(value = "rxjs", version = "^7.5.0")
@TsDependency(value = "tslib", version = "^2.3.0")
@TsDependency(value = "zone.js", version = "^0.11.4")

@TsDevDependency(value = "@angular-devkit/build-angular", version = "^13.3.0")
@TsDevDependency(value = "@angular/cli", version = "^13.3.0")
@TsDevDependency(value = "@angular/compiler-cli", version = "^13.3.0")
@TsDevDependency(value = "@types/node", version = "^12.20.48")
@TsDevDependency(value = "typescript", version = "^4.6.2")

@NgImportReference(value = "NgModule", reference = "@angular/core")

@NgModule
public interface INgModule<J extends INgModule<J>>
		extends IComponent<J>
{
	String moduleString = "@NgModule({\n" +
	                      "\timports:[%s],\n" +
	                      "\tdeclarations:[%s],\n" +
	                      "\tproviders: [%s],\n" +
	                      "\texports:[%s],\n" +
	                      "\tbootstrap:%s,\n" +
	                      "\tschemas:[%s]\n" +
	                      "" +
	                      "})";
	
	
	default List<String> declarations()
	{
		return new ArrayList<>();
	}
	
	@Override
	default List<String> componentDecorators()
	{
		List<String> list = IComponent.super.componentDecorators();
		StringBuilder declarations = new StringBuilder();
		StringBuilder providers = new StringBuilder();
		StringBuilder exports = new StringBuilder();
		StringBuilder bootstrap = new StringBuilder();
		StringBuilder schemas = new StringBuilder();
		
		declarations()
				.forEach(a -> {
					declarations.append(a)
					            .append(",")
					            .append("\n");
				});
		
		if (declarations.length() > 1)
		{
			declarations.deleteCharAt(declarations.length() - 2);
		}
		
		providers()
				.forEach((key) -> {
					providers.append(key)
					         .append(",")
					         .append("\n");
				});
		
		if (providers.length() > 1)
		{
			providers.deleteCharAt(providers.length() - 2);
		}
		
		
		exports()
				.forEach((key) -> {
					exports.append(key)
					       .append(",")
					       .append("\n");
				});
		if (exports.length() > 1)
		{
			exports.deleteCharAt(exports.length() - 2);
		}
		
		bootstrap.append(bootstrap());
		
		schemas()
				.forEach((key) -> {
					schemas.append(key)
					       .append(",")
					       .append("\n");
				});
		if (schemas.length() > 1)
		{
			schemas.deleteCharAt(schemas.length() - 2);
		}
		
		
		
		StringBuilder importNames = new StringBuilder();
		
		Arrays.stream(moduleImports()
				      .toArray())
		      .forEach((key) -> {
			
			      importNames.append(key)
			                 .append(",")
			                 .append("\n");
		      });
		
		if (importNames.length() > 1)
		{
			importNames.deleteCharAt(importNames.length() - 2);
		}
		
		String componentString = String.format(moduleString, importNames, declarations, providers, exports, bootstrap, schemas);
		list.add(componentString);
		return list;
	}
	
	default List<String> providers()
	{
		return new ArrayList<>();
	}
	
	default List<String> bootstrap()
	{
		return new ArrayList<>();
	}
	
	default List<String> assets()
	{
		return new ArrayList<>();
	}
	
	default List<String> exports()
	{
		return new ArrayList<>();
	}
	
	@SuppressWarnings("unchecked")
	default J setApp(INgApp<?> app)
	{
		return (J) this;
	}
	
	default List<String> moduleImports()
	{
		return new ArrayList<>();
	}
	
	default List<String> schemas()
	{
		return new ArrayList<>();
	}
}
