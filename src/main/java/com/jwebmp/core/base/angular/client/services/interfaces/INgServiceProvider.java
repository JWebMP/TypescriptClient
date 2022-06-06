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
		String s = "this.subscription = this.service.data.subscribe(observer => {\n" +
		           "            if (observer && observer.out && observer.out.length > 0) {\n";
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
		out.add("public sendData(datas : any){" +
		        "   this.service.sendData(datas);" +
		        "}");
		NgServiceProvider provider = getAnnotation();
		out.add("get onUpdate(): Observable<boolean> {\n" +
		        "        return this._onUpdate.asObservable();\n" +
		        "    }");
		if(provider.dataArray())
		out.add("checkData()\n" +
		        "    {\n" +
		        "        if(this." + getAnnotation().variableName() + ".length == 0) {\n" +
		        "            this.service.fetchData();\n" +
		        "        }\n" +
		        "    }");
		else {
			out.add("checkData()\n" +
			        "    {\n" +
			        "        if(!this." + getAnnotation().variableName() + ") {\n" +
			        "            this.service.fetchData();\n" +
			        "        }\n" +
			        "    }");
		}
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
}
