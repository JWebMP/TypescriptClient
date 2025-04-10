package com.jwebmp.core.base.angular.client.services.interfaces;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.ajax.AjaxCall;
import com.jwebmp.core.base.ajax.AjaxResponse;
import com.jwebmp.core.base.angular.client.DynamicData;
import com.jwebmp.core.base.angular.client.annotations.angular.NgDataService;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgDataTypeReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.annotations.structures.NgMethod;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import com.jwebmp.core.base.angular.client.services.DataServiceConfiguration;
import com.jwebmp.core.base.angular.client.services.DataServiceReferences;
import com.jwebmp.core.base.angular.client.services.EventBusService;

import java.util.*;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getTsFilename;

@NgImportReference(value = "Injectable", reference = "@angular/core")
@NgImportReference(value = "inject", reference = "@angular/core")
@NgImportReference(value = "BehaviorSubject, ", reference = "rxjs")
@NgImportReference(value = "Observable", reference = "rxjs")
@NgImportReference(value = "Subscription", reference = "rxjs")
@NgImportReference(value = "v4 as uuidv4", reference = "uuid")
@NgDataTypeReference(value = DynamicData.class, primary = false)
@NgComponentReference(EventBusService.class)

@NgMethod("""
        get data(): Observable<DynamicData | undefined> {
            return this.dataListener;
        }""")

@NgMethod("""
        private generateHandlerId(): string {
            return `${this.listenerName}-${uuidv4()}`;
        }
        """)
@NgConstructorBody("""
        this.handlerId = this.generateHandlerId();
        """)

@NgConstructorBody("""
        this.dataListener = this.eventBusService.listen(this.listenerName, this.handlerId);
        """)

@NgField("private readonly dataListener :  Observable<DynamicData | undefined>;")
@NgField("private readonly handlerId: string;")

@NgConstructorBody("""
        this.subscription = this.dataListener.subscribe(
            (data) => this.handleIncomingData(data),
            (error) => console.error(` Error listening on ${this.listenerName}:`, error)
        );
        """)

@NgMethod("""
        private handleIncomingData(data: DynamicData | undefined): void {
            if (data) {
                this.dataSubject.next(data as any);
             //   console.log(`Received data for ${this.listenerName}:`, data);
                // Perform processing or state updates with the incoming data
            } else {
                console.warn(`Received empty data for ${this.listenerName}`);
            }
        }""")

@NgMethod("""
        fetchData() {
            this.eventBusService.send(
                'data',
                {
                    dataService: this.listenerName,
                    ...this.additionalData,
                    className: this.clazzName,
                },
                this.listenerName
            );
        }""")
@NgMethod("""
        public sendData(datas: any) {
            this.eventBusService.send(
                'dataSend',
                {
                    dataService: this.listenerName,
                    ...this.additionalData,
                    data: { ...datas },
                    className: this.clazzName,
                },
                this.listenerName
            );
        }""")
@NgOnDestroy("""
        this.eventBusService.unregisterListener(this.listenerName, this.handlerId);
        if (this.subscription) {
            this.subscription.unsubscribe();
        }""")
public interface INgDataService<J extends INgDataService<J>> extends IComponent<J>
{
    DynamicData getData(AjaxCall<?> call, AjaxResponse<?> response);

    default void receiveData(AjaxCall<?> call, AjaxResponse<?> response)
    {
    }

    @Override
    default StringBuilder renderFields()
    {
        DataServiceConfiguration config = DataServiceReferences.getDataServiceConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderInjects());
        sb.append(config.renderFields());
        return sb;
    }

    @Override
    default StringBuilder renderConstructorBody()
    {
        DataServiceConfiguration config = DataServiceReferences.getDataServiceConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderConstructorBodies());
        return sb;
    }

    @Override
    default StringBuilder renderConstructorParameters()
    {
        DataServiceConfiguration config = DataServiceReferences.getDataServiceConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderConstructorParameters());
        return sb;
    }

    @Override
    default StringBuilder renderMethods()
    {
        DataServiceConfiguration config = DataServiceReferences.getDataServiceConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderMethods());
        sb.append(config.renderOnInit());
        sb.append(config.renderOnDestroy());
        return sb;
    }

    @Override
    default StringBuilder renderInterfaces()
    {
        DataServiceConfiguration config = DataServiceReferences.getDataServiceConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderInterfaces());
        return sb;
    }

    @Override
    default StringBuilder renderImports()
    {
        DataServiceConfiguration config = DataServiceReferences.getDataServiceConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderImportStatements());
        return sb;
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
            fields.add("readonly dataSubject : BehaviorSubject<any> = new BehaviorSubject<any>(undefined);");
        }
        else
        {
            var firstReference = dtReferences.stream()
                    .filter(a -> a.primary())
                    .findFirst()
                    .orElse(null);

            if (firstReference == null || firstReference.value() == null || firstReference.value().getSimpleName() == null)
            {
                fields.add("readonly dataSubject : BehaviorSubject<any> = new BehaviorSubject<any>(undefined);");
            }
            else
            {
                var name = firstReference.value().getSimpleName();

                fields.add("readonly dataSubject : BehaviorSubject<" + name + " | undefined> = new BehaviorSubject<" + name + " | undefined>(undefined);");
            }
        }

        fields.add("readonly listenerName = '" + dService.value() + "';");
        fields.add("readonly clazzName = '" + getClass().getCanonicalName() + "';");
        fields.add("public additionalData : any = {};");
        fields.add("readonly subscription? : Subscription;");
        return fields;
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

    default boolean checkDataIsArray()
    {
        return false;
    }

}
