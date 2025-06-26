package com.jwebmp.core.base.angular.client.services.interfaces;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.angular.NgDirective;
import com.jwebmp.core.base.angular.client.annotations.angular.NgProvider;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnInit;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import com.jwebmp.core.base.angular.client.services.EventBusService;

import java.util.*;

@NgImportReference(value = "Directive", reference = "@angular/core")
@NgImportReference(value = "ElementRef", reference = "@angular/core")
@NgImportReference(value = "Input", reference = "@angular/core")
@NgConstructorParameter("private elementRef: ElementRef")

@NgImportReference(value = "OnInit", reference = "@angular/core")
@NgImportReference(value = "OnDestroy", reference = "@angular/core")
@NgComponentReference(EventBusService.class)
public interface INgDirective<J extends INgDirective<J>> extends IComponent<J>
{
    String directiveString = "@Directive({\n" +
            "\tselector:'%s',\n" +
            "\tstandalone:%b,\n" +
            "\tproviders:[%s]\n" +
            "})";

    default List<String> declarations()
    {
        Set<String> out = new HashSet<>();
        out.add(getClass().getSimpleName());
        return new ArrayList<>(out);
    }

    @Override
    default List<NgConstructorParameter> getAllConstructorParameters()
    {
        var s = IComponent.super.getAllConstructorParameters();
        AnnotationHelper ah = IGuiceContext.get(AnnotationHelper.class);
        var compRefs = ah.getAnnotationFromClass(getClass(), NgComponentReference.class);
        for (NgComponentReference compRef : compRefs)
        {
            var reference = compRef.value();
            if (reference.isAnnotationPresent(NgProvider.class))
            {
                s.add(AnnotationUtils.getNgConstructorParameter("public " + AnnotationUtils.getTsVarName(reference) + " : " + AnnotationUtils.getTsFilename(reference)));
            }
        }
        return s;
    }

    @Override
    default List<String> decorators()
    {
        AnnotationHelper ah = IGuiceContext.get(AnnotationHelper.class);

        List<String> list = IComponent.super.decorators();
        StringBuilder selector = new StringBuilder();
        StringBuilder styles = new StringBuilder();
        StringBuilder template = new StringBuilder();
        StringBuilder styleUrls = new StringBuilder();
        StringBuilder providers = new StringBuilder();

        NgDirective ngComponent = ah
                .getAnnotationFromClass(getClass(), NgDirective.class)
                .get(0);

        selector.append(ngComponent.value());

        var compRefs = ah.getAnnotationFromClass(getClass(), NgComponentReference.class);
        for (NgComponentReference compRef : compRefs)
        {
            var reference = compRef.value();
            if (reference.isAnnotationPresent(NgProvider.class))
            {
                var np = reference.getAnnotation(NgProvider.class);
                if (!np.singleton())
                {
                    providers.append(compRef.value()
                                            .getSimpleName() + ",\n");
                }
            }
        }

        providers()
                .forEach((key) -> {
                    providers.append(key)
                             .append(",")
                             .append("\n");
                });
        if (!providers()
                .isEmpty())
        {
            providers.deleteCharAt(providers.length() - 2);
        }


        String componentString = String.format(directiveString, selector, ngComponent.standalone(), providers);
        list.add(componentString);
        return list;
    }

    default List<String> styleUrls()
    {
        return new ArrayList<>();
    }

    default List<String> providers()
    {
        return new ArrayList<>();
    }

    default List<String> inputs()
    {
        return new ArrayList<>();
    }

    default List<String> outputs()
    {
        return new ArrayList<>();
    }

    default List<String> host()
    {
        return new ArrayList<>();
    }

    @Override
    default String renderOnInitMethod()
    {
        StringBuilder out = new StringBuilder(IComponent.super.renderOnInitMethod());
        out.append("ngOnInit() {\n");
        for (String s : onInit())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }

        List<NgOnInit> fInit = IGuiceContext.get(AnnotationHelper.class)
                                            .getAnnotationFromClass(getClass(), NgOnInit.class);
        fInit.sort(Comparator.comparingInt(NgOnInit::sortOrder));
        Set<String> outs = new LinkedHashSet<>();
        if (!fInit.isEmpty())
        {
            for (NgOnInit ngField : fInit)
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


    default List<String> interfaces()
    {
        List<String> out = IComponent.super.interfaces();
        out.add("OnInit");
        out.add("OnDestroy");
        return out;
    }

}
