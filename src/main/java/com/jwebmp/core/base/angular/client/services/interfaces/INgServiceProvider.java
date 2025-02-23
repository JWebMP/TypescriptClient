package com.jwebmp.core.base.angular.client.services.interfaces;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.angular.NgServiceProvider;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getNgComponentReference;

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
        if (!getAnnotation().dataArray())
        {
            out.add(0, "public " + getAnnotation().variableName() + " : " + getAnnotation().dataType()
                    .getSimpleName() + " = " + INgDataType.renderObjectStructure(getAnnotation().dataType()) + ";");


        } else
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
        String s = "this.subscription = this.service.data\n" +
                "" + (buffer() ? ".pipe(bufferTime(" + bufferTime() + "))" : "") +
                "" + (takeLast() ? ".pipe(takeLast(" + takeLastCount() + "))" : "") +
                "" +
                ".subscribe(message => {\n" +
                "" +
                "if (message) {\n" +
                "if (typeof message === 'string')\n" +
                "                        this." + getAnnotation().variableName() + " = JSON.parse(message as any);\n" +
                "                    else this." + getAnnotation().variableName() + " = message as any;" +
                "                       this._onUpdate.next(true);" +
                "" +
                //        "                    this." + getAnnotation().variableName() + " = JSON.parse(message as any);\n" +
                "                }" +
                "            " +
/*		     "" +
		     "" +
		     "            if (message && observer.out) {\n";
		s += "                this." + getAnnotation().variableName() + " = message.out[0];\n";
		s += "                this._onUpdate.next(true);\n" +
		     "            }\n" +*/
                "        });\n";
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
            resetString += "\t\tthis." + getAnnotation().variableName() + " = " + INgDataType.renderObjectStructure(getAnnotation().dataType()) + ";";
        } else
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

    @Override
    default List<String> interfaces()
    {
        List<String> out = IComponent.super.interfaces();
        out.add("OnDestroy");
        //out.add("OnInit");
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
