package com.jwebmp.core.base.angular.client.services.interfaces;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.ajax.AjaxCall;
import com.jwebmp.core.base.ajax.AjaxResponse;
import com.jwebmp.core.base.angular.client.DynamicData;
import com.jwebmp.core.base.angular.client.annotations.angular.NgDataService;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgDataTypeReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.annotations.structures.NgMethod;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import com.jwebmp.core.base.angular.client.services.EventBusService;
import com.jwebmp.core.base.angular.client.services.any;

import java.util.*;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getTsFilename;

@NgImportReference(value = "Injectable", reference = "@angular/core")
@NgImportReference(value = "inject", reference = "@angular/core")
@NgImportReference(value = "BehaviorSubject, Observable, Subject, Subscription", reference = "rxjs")
@NgImportReference(value = "bufferTime", reference = "rxjs")

@NgDataTypeReference(value = DynamicData.class, primary = false)
@NgComponentReference(EventBusService.class)

@NgOnDestroy("this.eventBusService.unregisterListener(this.listenerName);")

@NgMethod("""
        get data(): Observable<DynamicData | undefined> {
                return this.dataListener;
            }""")

@NgConstructorBody("""
        this.dataListener = this.eventBusService.listen(this.listenerName);
        """)
@NgConstructorBody("""
        this.subscription = this.dataListener.subscribe(message => {
                    if (message) {
                        if (Array.isArray(message)) {
                            for (let m of message) {
                                if (m && m.out && m.out[0]) {
                                  \s
                                }
                            }
                        } else {
                            if (message.out && message.out[0]) {
                              \s
                            }
                        }
                    }
                })""")

@NgField("private readonly dataListener :  Observable<DynamicData | undefined>;")

@NgOnDestroy("this.subscription?.unsubscribe();")

@NgImportReference(value = "OnDestroy", reference = "@angular/core")
public interface INgDataService<J extends INgDataService<J>> extends IComponent<J>
{
    DynamicData getData(AjaxCall<?> call, AjaxResponse<?> response);

    default void receiveData(AjaxCall<?> call, AjaxResponse<?> response)
    {
    }

    @Override
    default List<String> interfaces()
    {
        List<String> out = IComponent.super.interfaces();
        out.add("OnDestroy");
        //	out.add("OnInit");
        return out;
    }

    default boolean checkDataIsArray()
    {
        return false;
    }

    @Override
    default List<String> methods()
    {
        List<String> methods = IComponent.super.methods();
        if (methods == null)
        {
            methods = new ArrayList<>();
        }

        String dtRef = "";
        dtRef = getTsFilename(DynamicData.class);

        methods.add("""
                fetchData() {
                        this.eventBusService.send('data', {
                        dataService:this.listenerName,
                            ...this.additionalData,
                            className: '%s'
                        }, this.listenerName);
                    }""".formatted(getClass().getCanonicalName()));

        methods.add("""
                public sendData(datas: any) {
                        this.eventBusService.send('dataSend', {
                        dataService:this.listenerName,
                            ...this.additionalData, data: {...datas},
                            className: '%s'
                        }, this.listenerName);
                    }""".formatted(getClass().getCanonicalName()));


        return methods;
    }

    @Override
    default List<String> constructorBody()
    {
        List<String> strings = IComponent.super.constructorBody();
        if (getClass().isAnnotationPresent(NgDataService.class))
        {
            NgDataService dataService = getClass().getAnnotation(NgDataService.class);
            if (dataService.fetchOnCreate())
            {
                strings.add("this.fetchData();\n");
            }
        }
        return strings;
    }

    @Override
    default List<String> fields()
    {
        List<String> fields = IComponent.super.fields();
        NgDataService dService = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgDataService.class)
                .get(0);

        var dtReferences = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgDataTypeReference.class);
        if (dtReferences.isEmpty())
        {
            fields.add("private dataSubject : BehaviorSubject<any> = new BehaviorSubject<any>(undefined);");
        } else
        {
            var name = dtReferences.stream().filter(a -> a.primary()).findFirst().orElseThrow().value().getSimpleName();
            fields.add("private dataSubject : BehaviorSubject<" + name + " | undefined> = new BehaviorSubject<" + name + " | undefined>(undefined);");
        }

        fields.add(" private listenerName = '" + dService.value() + "';");
        fields.add(" private clazzName = '" + getClass().getCanonicalName() + "';");
        fields.add(" public additionalData : any = {};");
        fields.add("private subscription? : Subscription;");
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
    default List<String> decorators()
    {
        List<String> out = IComponent.super.decorators();
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
        for (String s : onDestroy())
        {
            out.append("\t")
                    .append(s)
                    .append("\n");
        }
        List<NgOnDestroy> fInit = IGuiceContext.get(AnnotationHelper.class)
                .getAnnotationFromClass(getClass(), NgOnDestroy.class);
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
