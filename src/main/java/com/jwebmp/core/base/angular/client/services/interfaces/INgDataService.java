package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.ajax.*;
import com.jwebmp.core.base.angular.client.*;
import com.jwebmp.core.base.angular.client.annotations.angular.*;
import com.jwebmp.core.base.angular.client.annotations.functions.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.services.*;

import java.util.*;

import static com.jwebmp.core.base.angular.client.services.AnnotationsMap.*;
import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.*;

@NgImportReference(value = "Injectable", reference = "@angular/core")
@NgImportReference(value = "BehaviorSubject, Observable, Subject, Subscription", reference = "rxjs")
@NgImportReference(value = "bufferTime", reference = "rxjs")

@NgDataTypeReference(value = DynamicData.class, primary = false)
@NgComponentReference(SocketClientService.class)

@NgOnDestroy("this.subscription?.unsubscribe();")
@NgOnDestroy("this.socketClientService.deregisterListener(this.listenerName);")
@NgOnDestroy("this._data.unsubscribe();")

@NgImportReference(value = "OnDestroy", reference = "@angular/core")
public interface INgDataService<J extends INgDataService<J>> extends IComponent<J>
{
	DynamicData getData(AjaxCall<?> call, AjaxResponse<?> response);
	
	default void receiveData(AjaxCall<?> call, AjaxResponse<?> response)
	{
	}
	
	@Override
	default List<String> componentInterfaces()
	{
		List<String> out = IComponent.super.componentInterfaces();
		out.add("OnDestroy");
		return out;
	}
	
	default boolean checkDataIsArray()
	{
		return false;
	}
	
	@Override
	default List<String> componentConstructorBody()
	{
		List<String> bodies = IComponent.super.componentConstructorBody();
		bodies.add("this.subscription = this.socketClientService.registerListener(this.listenerName)" +
		           "" + (buffer() ? ".pipe(bufferTime(1500))" : "") +
		           ".subscribe((message : " + getTsFilename(DynamicData.class) + ") => {\n" +
		           "" +
		           "" + (checkDataIsArray() ?
				"if (Array.isArray(message)) {\n" +
				"                this.dataStore.datas = message[0];\n" +
				"            } else\n" +
				"                this.dataStore.datas = message;\n" : "" +
				                                                      "this.dataStore.datas = message;\n")
		           +
		           "this._data.next(Object.assign({}, this.dataStore).datas);" +
		           "" +
		           "" +
		           "" +
		           "});\n");
		
		if (getClass().isAnnotationPresent(NgDataService.class))
		{
			NgDataService dataService = getClass().getAnnotation(NgDataService.class);
			if (dataService.fetchOnCreate())
			{
				bodies.add("this.fetchData();\n");
			}
		}
		
		return bodies;
	}
	
	
	@Override
	default List<String> componentMethods()
	{
		List<String> methods = IComponent.super.componentMethods();
		if (methods == null)
		{
			methods = new ArrayList<>();
		}
		
		String dtRef = "";
		dtRef = getTsFilename(DynamicData.class);
		methods.add("\tfetchData(){\n" +
		            "\t\tthis.socketClientService.send('data',{...this.additionalData,className :  '" + getClass().getCanonicalName() + "'},this.listenerName);\n" +
		            "\t}\n" +
		            "" +
		            "\tget data() : Observable<" + dtRef + "> {\n" +
		            "\t\treturn this._data.asObservable();\n" +
		            "\t}" +
		            "" +
		            "");
		methods.add("\tpublic sendData(datas : any) {\n" +
		            "\t\tthis.socketClientService.send('dataSend', {" +
		            "\t\t\t...this.additionalData," +
		            "\t\t\tdata :{...datas},\n" +
		            "\t\tclassName: '" + getClass().getCanonicalName() + "'}, this.listenerName);\n" +
		            "\t}");
		
		methods.add("\tpublic reset(){\n" +
		            "\t\tthis._data.next({});" +
		            "\t}\n");
		return methods;
	}
	
	
	@Override
	default List<String> componentFields()
	{
		List<String> fields = IComponent.super.componentFields();
		
		fields.add(" private _data = new BehaviorSubject<" + getTsFilename(DynamicData.class) + ">({});");
		fields.add(" private dataStore: { datas: " + getTsFilename(DynamicData.class) + " } = { datas: {} }; ");
		//	fields.add(" public data : " + getTsFilename(dReference.value()) + " = {};\n");
		
		
		NgDataService dService = getAnnotations(getClass(), NgDataService.class).get(0);
		fields.add(" private listenerName = '" + dService.value() + "';");
		fields.add(" private subscription? : Subscription;\n");
		fields.add(" public additionalData : any = {};\n");
		return fields;
	}
	
	default boolean buffer()
	{
		return false;
	}
	
	default int bufferTime()
	{
		return 100;
	}
	
	default boolean takeLast()
	{
		return false;
	}
	
	default int takeLastCount()
	{
		return 100;
	}
	
	
	default String providedIn()
	{
		return "any";
	}
	
	@Override
	default List<String> componentDecorators()
	{
		List<String> out = IComponent.super.componentDecorators();
		out.add("@Injectable({\n" +
		        "  providedIn: '" + providedIn() + "'\n" +
		        "})");
		return out;
	}
	
	@Override
	default String renderOnDestroyMethod()
	{
		StringBuilder out = new StringBuilder(IComponent.super.renderOnDestroyMethod());
		out.append("ngOnDestroy() {\n");
		for (String s : componentOnDestroy())
		{
			out.append("\t")
			   .append(s)
			   .append("\n");
		}
		for (String s : onDestroy())
		{
			out.append("\t")
			   .append(s)
			   .append("\n");
		}
		List<NgOnDestroy> fInit = getAnnotations(getClass(), NgOnDestroy.class);
		fInit.sort(Comparator.comparingInt(NgOnDestroy::sortOrder));
		Set<String> outs = new LinkedHashSet<>();
		if (!fInit.isEmpty())
		{
			for (NgOnDestroy ngField : fInit)
			{
				outs.add(ngField.value()
				                .trim());
			}
		}
		StringBuilder fInitOut = new StringBuilder();
		for (String s : outs)
		{
			fInitOut.append(s)
			        .append("\n");
		}
		out.append("\t")
		   .append(fInitOut)
		   .append("\n");
		out.append("}\n");
		return out.toString();
	}
}
