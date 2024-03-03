package com.jwebmp.core.base.angular.client.services.interfaces;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.angular.NgDirective;
import com.jwebmp.core.base.angular.client.annotations.components.NgInput;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnInit;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;

import java.util.*;

@NgImportReference(value = "Directive", reference = "@angular/core")
@NgImportReference(value = "ElementRef", reference = "@angular/core")
@NgImportReference(value = "Input", reference = "@angular/core")
@NgConstructorParameter("private elementRef: ElementRef")

@NgImportReference(value = "OnInit", reference = "@angular/core")
@NgImportReference(value = "OnDestroy", reference = "@angular/core")

public interface INgDirective<J extends INgDirective<J>> extends IComponent<J>
{
    String directiveString = "@Directive({\n" +
            "\tselector:'%s',\n" +
            "\tproviders:[%s]\n" +
            "})";

    default List<String> declarations()
    {
        Set<String> out = new HashSet<>();
        out.add(getClass().getSimpleName());
        return new ArrayList<>(out);
    }

    @Override
    default List<String> componentDecorators()
    {
        List<String> list = IComponent.super.componentDecorators();
        StringBuilder selector = new StringBuilder();
        StringBuilder styles = new StringBuilder();
        StringBuilder template = new StringBuilder();
        StringBuilder styleUrls = new StringBuilder();
        StringBuilder providers = new StringBuilder();

        NgDirective ngComponent = IGuiceContext.get(AnnotationHelper.class)
                                               .getAnnotationFromClass(getClass(), NgDirective.class)
                                               .get(0);

        selector.append(ngComponent.value());

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

        String componentString = String.format(directiveString, selector, providers);
        list.add(componentString);
        return list;
    }

    @Override
    default List<String> componentFields()
    {
        List<String> list = IComponent.super.componentFields();
        if (list == null)
        {
            list = new ArrayList<>();
        }
        List<NgInput> ngComponent = IGuiceContext.get(AnnotationHelper.class)
                                                 .getAnnotationFromClass(getClass(), NgInput.class);
        ngComponent.sort(Comparator.comparingInt(NgInput::sortOrder));
        for (NgInput ngInput : ngComponent)
        {
            list.add("\t@Input(\"" + ngInput.value() + "\") " + ngInput.value() + "? :" + AnnotationUtils.getTsFilename(ngInput.type()) + ";");
        }

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
        for (String s : componentOnInit())
        {
            out.append("\t")
               .append(s)
               .append("\n");
        }
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


    default List<String> componentInterfaces()
    {
        List<String> out = IComponent.super.componentInterfaces();
        out.add("OnInit");
        out.add("OnDestroy");
        return out;
    }


}
