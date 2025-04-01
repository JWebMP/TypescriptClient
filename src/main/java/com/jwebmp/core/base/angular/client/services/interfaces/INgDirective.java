package com.jwebmp.core.base.angular.client.services.interfaces;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.angular.NgDirective;
import com.jwebmp.core.base.angular.client.annotations.angular.NgProvider;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnInit;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.services.*;

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

    @Override
    default StringBuilder renderFields()
    {
        DirectiveConfiguration config = DirectiveReferences.getDataServiceConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderInjects());
        sb.append(config.renderFields());
        return sb;
    }

    @Override
    default StringBuilder renderConstructorBody()
    {
        DirectiveConfiguration config = DirectiveReferences.getDataServiceConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderConstructorBodies());
        return sb;
    }

    @Override
    default StringBuilder renderConstructorParameters()
    {
        DirectiveConfiguration config = DirectiveReferences.getDataServiceConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderConstructorParameters());
        return sb;
    }

    @Override
    default StringBuilder renderMethods()
    {
        DirectiveConfiguration config = DirectiveReferences.getDataServiceConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderMethods());
        sb.append(config.renderOnInit());
        sb.append(config.renderOnDestroy());
        return sb;
    }

    @Override
    default StringBuilder renderInterfaces()
    {
        DirectiveConfiguration config = DirectiveReferences.getDataServiceConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderInterfaces());
        return sb;
    }

    @Override
    default StringBuilder renderImports()
    {
        DirectiveConfiguration config = DirectiveReferences.getDataServiceConfigurations(this);
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderImportStatements());
        return sb;
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

}
