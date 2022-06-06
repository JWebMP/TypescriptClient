package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.angular.*;
import com.jwebmp.core.base.angular.client.annotations.functions.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.structures.*;

import java.util.*;

import static com.jwebmp.core.base.angular.client.services.AnnotationsMap.*;
import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.*;

@NgImportReference(value = "Subscription", reference = "rxjs")
@NgImportReference(value = "BehaviorSubject", reference = "rxjs")
@NgImportReference(value = "Observable", reference = "rxjs")
@NgImportReference(value = "Injectable", reference = "@angular/core")

@NgField("private subscription?: Subscription;")
@NgField("public additionalData: any = {};")
@NgOnDestroy("this.subscription?.unsubscribe();")

@NgImportReference(value = "OnDestroy", reference = "@angular/core")
//@NgImportReference(value = "Output", reference = "@angular/core")
//@NgImportReference(value = "EventEmitter", reference = "@angular/core")
public interface INgServiceProvider<J extends INgServiceProvider<J>> extends IComponent<J>
{
	default NgServiceProvider getAnnotation()
	{
		return getClass().getAnnotation(NgServiceProvider.class);
	}
	
	@Override
	default List<NgImportReference> getAllImportAnnotations()
	{
		List<NgImportReference> out = IComponent.super.getAllImportAnnotations();
		NgComponentReference reference = getNgComponentReference(getAnnotation().value());
		out.addAll(putRelativeLinkInMap(getClass(), reference));
		NgComponentReference reference2 = getNgComponentReference(getAnnotation().dataType());
		out.addAll(putRelativeLinkInMap(getClass(), reference2));
		return out;
	}
	
	@Override
	default List<String> constructorParameters()
	{
		List<String> out = IComponent.super.constructorParameters();
		out.add("private service : " + getAnnotation().value()
		                                              .getSimpleName());
		return out;
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
	default List<String> componentFields()
	{
		List<String> out = IComponent.super.componentFields();
		out.add("private _onUpdate = new BehaviorSubject<boolean>(false);");
		if (!getAnnotation().dataArray())
		{
			out.add(0, "public " + getAnnotation().variableName() + " : " + getAnnotation().dataType()
			                                                                               .getSimpleName() + " = {};");
			
			
		}
		else
		{
			out.add(0, "public " + getAnnotation().variableName() + " : " + getAnnotation().dataType()
			                                                                               .getSimpleName() + "[] = [];");
			
		}
		return out;
	}
	
	@Override
	default List<String> componentConstructorBody()
	{
		List<String> out = IComponent.super.componentConstructorBody();
		String s = "this.subscription = this.service.data" +
		           "" + (buffer() ? ".pipe(bufferTime(" + bufferTime() + "))" : "") +
		           "" + (takeLast() ? ".pipe(takeLast(" + takeLastCount() + "))" : "") +
		           "" +
		           ".subscribe(observer => {\n" +
		           "            if (observer && observer.out) {\n";
		s += "                this." + getAnnotation().variableName() + " = observer.out[0];\n";
		s += "                this._onUpdate.next(true);" +
		     "            }\n" +
		     "        });";
		out.add(s);
		return out;
	}
	
	@Override
	default List<String> componentMethods()
	{
		List<String> out = IComponent.super.componentMethods();
		out.add("\tpublic sendData(datas : any){\n" +
		        "\t\tthis.service.additionalData = this.additionalData;\n" +
		        "\t\tthis.service.sendData(datas);\n" +
		        "\t}");
	
		out.add("\tget onUpdate(): Observable<boolean> {\n" +
		        "\t\treturn this._onUpdate.asObservable();\n" +
		        "\t}");
		out.add("\tcheckData()\n" +
		        "\t{\n" +
		        "\t\tthis.service.fetchData();\n" +
		        "\t}");
		
		String resetString = "\treset() {\n" +
		                     "\t\tthis._onUpdate.next(false);\n" +
		                     "\t\tthis.service.additionalData = {};\n" +
		                     "\t\tthis.service.additionalData = this.additionalData;\n";
		if (!getAnnotation().dataArray())
		{
			resetString += "\t\tthis." + getAnnotation().variableName() + " = {};";
		}
		else
		{
			resetString += "\t\tthis." + getAnnotation().variableName() + " = [];";
			
		}
		resetString += "\t}\n";
		out.add(resetString);
		return out;
	}
	
	default String providedIn()
	{
		return "any";
	}
	
	@Override
	default List<String> componentInterfaces()
	{
		List<String> out = IComponent.super.componentInterfaces();
		out.add("OnDestroy");
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
	
	default boolean buffer()
	{
		return false;
	}
	
	default int bufferTime()
	{
		return 500;
	}
	
	default boolean takeLast()
	{
		return false;
	}
	
	default int takeLastCount()
	{
		return 100;
	}
}
