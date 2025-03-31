package com.jwebmp.core.base.angular.client.services.interfaces;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.angular.NgServiceProvider;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import com.jwebmp.core.base.angular.client.services.DataServiceConfiguration;
import com.jwebmp.core.base.angular.client.services.DataServiceReferences;
import com.jwebmp.core.base.angular.client.services.ServiceProviderReferences;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getNgComponentReference;

@NgImportReference(value = "Subscription", reference = "rxjs")
@NgImportReference(value = "BehaviorSubject", reference = "rxjs")
@NgImportReference(value = "Observable", reference = "rxjs")
@NgImportReference(value = "Injectable", reference = "@angular/core")
@NgImportReference(value = "Injectable", reference = "@angular/core")

@NgField("private subscription?: Subscription;")
@NgField("public additionalData: any = {};")
@NgOnDestroy("this.subscription?.unsubscribe();")

//@NgImportReference(value = "OnDestroy", reference = "@angular/core")
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

        var reference3 = AnnotationUtils.getNgImportReference("inject", "@angular/core");
        out.add(reference3);

        return out;
    }

    @Override
    default StringBuilder renderFields()
    {
        var config = ServiceProviderReferences.getServiceProviderConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderInjects());
        sb.append(config.renderFields());
        return sb;
    }

    @Override
    default StringBuilder renderConstructorBody()
    {
        var config = ServiceProviderReferences.getServiceProviderConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderConstructorBodies());
        return sb;
    }

    @Override
    default StringBuilder renderConstructorParameters()
    {
        var config = ServiceProviderReferences.getServiceProviderConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderConstructorParameters());
        return sb;
    }

    @Override
    default StringBuilder renderMethods()
    {
        var config = ServiceProviderReferences.getServiceProviderConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderMethods());
        sb.append(config.renderOnInit());
        sb.append(config.renderOnDestroy());
        return sb;
    }

    @Override
    default StringBuilder renderInterfaces()
    {
        var config = ServiceProviderReferences.getServiceProviderConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderInterfaces());
        return sb;
    }

    @Override
    default StringBuilder renderImports()
    {
        var config = ServiceProviderReferences.getServiceProviderConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderImportStatements());
        return sb;
    }

    @Override
    default List<String> decorators()
    {
        List<String> out = IComponent.super.decorators();
        out.add("@Injectable({\n" +
                "  providedIn: '" + providedIn() + "'\n" +
                "})");
        return out;
    }

    @Override
    default List<String> fields()
    {
        List<String> out = IComponent.super.fields();
        out.add("private _onUpdate = new BehaviorSubject<boolean>(false);");
        out.add("private readonly service = inject(" + getAnnotation().value().getSimpleName() + ");");
        INgDataType<?> obj = IGuiceContext.get(getAnnotation().dataType());
        if (!getAnnotation().dataArray())
        {
            out.add(0, "public " + getAnnotation().variableName() + " : " + getAnnotation().dataType()
                    .getSimpleName() + " = " + obj.renderObjectStructure(getAnnotation().dataType()) + ";");


        }
        else
        {
            out.add(0, "public " + getAnnotation().variableName() + " : " + getAnnotation().dataType()
                    .getSimpleName() + "[] = [];");

        }
        return out;
    }

    @Override
    default List<String> constructorBody()
    {
        List<String> out = IComponent.super.constructorBody();
        String s = """
                \tthis.subscription = this.service.data
                        .subscribe(message => {
                            if (message) {
                                if (typeof message === 'string')
                                        this.%s = JSON.parse(message as any);
                                    else this.%s = message as any;
                                this._onUpdate.next(true);
                            }
                        });
                """.formatted(getAnnotation().variableName(), getAnnotation().variableName());
        out.add(s);
        //out.add("this.checkData();");
        return out;
    }

    @Override
    default List<String> methods()
    {
        List<String> out = IComponent.super.methods();
        String sendDataString = "\tpublic sendData(datas : any){\n";
        sendDataString += "\t\tthis.service.additionalData = this.additionalData;\n" +
                "\t\tthis.service.sendData(datas);\n" +
                "\t}";

        out.add(sendDataString);

        out.add("""
                \tget onUpdate(): Observable<boolean> {
                \t\treturn this._onUpdate.asObservable();
                \t}""");
        out.add("""
                \tcheckData()
                \t{
                \t\tthis.service.fetchData();
                \t}""");

        String resetString = """
                \treset() {
                \t\tthis._onUpdate.next(false);
                \t\tthis.service.additionalData = {};
                \t\tthis.service.additionalData = this.additionalData;
                """;
        if (!getAnnotation().dataArray())
        {
            INgDataType<?> obj = IGuiceContext.get(getAnnotation().dataType());
            resetString += "\t\tthis." + getAnnotation().variableName() + " = " + obj.renderObjectStructure(getAnnotation().dataType()) + ";";
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
        if (getClass().isAnnotationPresent(NgServiceProvider.class))
        {
            var ng = getClass().getAnnotation(NgServiceProvider.class);
            if (ng.singleton())
            {
                return "root";
            }
        }
        return "any";
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
