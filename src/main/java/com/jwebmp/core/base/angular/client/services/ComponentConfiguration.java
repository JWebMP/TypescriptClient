package com.jwebmp.core.base.angular.client.services;

import com.google.common.base.Strings;
import com.jwebmp.core.base.angular.client.annotations.angular.NgComponent;
import com.jwebmp.core.base.angular.client.annotations.angular.NgDataService;
import com.jwebmp.core.base.angular.client.annotations.angular.NgDirective;
import com.jwebmp.core.base.angular.client.annotations.components.NgInput;
import com.jwebmp.core.base.angular.client.annotations.components.NgOutput;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.NgAfterContentChecked;
import com.jwebmp.core.base.angular.client.annotations.functions.NgAfterContentInit;
import com.jwebmp.core.base.angular.client.annotations.functions.NgAfterViewChecked;
import com.jwebmp.core.base.angular.client.annotations.functions.NgAfterViewInit;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportModule;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils;
import com.jwebmp.core.base.angular.client.services.interfaces.INgComponent;
import com.jwebmp.core.base.angular.client.services.interfaces.INgDirective;
import com.jwebmp.core.base.html.interfaces.GlobalChildren;
import com.jwebmp.core.base.interfaces.IComponentHierarchyBase;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ComponentConfiguration<T extends IComponentHierarchyBase<?, T> & INgComponent<T>> extends AbstractNgConfiguration<T>
{
    private T rootComponent;

    private final Set<NgAfterViewInit> afterViewInit = new LinkedHashSet<>();
    private final Set<NgAfterViewChecked> afterViewChecked = new LinkedHashSet<>();
    private final Set<NgAfterContentInit> afterContentInit = new LinkedHashSet<>();
    private final Set<NgAfterContentChecked> afterContentChecked = new LinkedHashSet<>();

    protected final Set<NgImportModule> importModules = new LinkedHashSet<>();

    private final Set<NgInput> inputs = new LinkedHashSet<>();
    private final Set<NgOutput> outputs = new LinkedHashSet<>();

    private final Set<NgModal> modals = new LinkedHashSet<>();
    private final Set<NgSignal> signals = new LinkedHashSet<>();


    public ComponentConfiguration<T> setRootComponent(IComponentHierarchyBase<GlobalChildren, ?> rootComponent)
    {
        this.rootComponent = (T) rootComponent;
        return this;
    }

    @Override
    public void splitComponentReferences()
    {
        for (NgComponentReference componentReference : componentReferences)
        {
            List<NgImportReference> importReferences = retrieveRelativePathForReference(componentReference);
            var importReference = importReferences.get(0);
            var classReference = componentReference.value();
            if (INgDirective.class.isAssignableFrom(classReference))
            {
                for (NgDirective ngDirective : AnnotationUtils.getAnnotation(classReference, NgDirective.class))
                {
                    getImportModules().add(AnnotationUtils.getNgImportModule(AnnotationUtils.getTsFilename(classReference)));
                    getImportReferences().add(importReference);
                    break;
                }
            }
            if (INgComponent.class.isAssignableFrom(classReference))
            {
                for (NgComponent ng : AnnotationUtils.getAnnotation(classReference, NgComponent.class))
                {
                    getImportModules().add(AnnotationUtils.getNgImportModule(AnnotationUtils.getTsFilename(classReference)));
                    getImportReferences().add(importReference);
                    break;
                }
            }
        }

        super.splitComponentReferences();
    }

    public StringBuilder renderImportModules()
    {
        if (importModules.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder().append("\n");
        for (var importProvider : importModules)
        {
            sb.append("\t\t" + importProvider.value().trim()).append(",\n");
        }
        return sb;
    }

    public StringBuilder renderAfterViewInit()
    {
        if (afterViewInit.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\tngAfterViewInit()\n");
        sb.append("\t{\n");
        for (var ngAfterViewInit : afterViewInit)
        {
            sb.append("\t\t").append(ngAfterViewInit.value().trim()).append("\n");
        }
        sb.append("\t}\n");
        return sb;
    }

    public StringBuilder renderAfterViewChecked()
    {
        if (afterViewChecked.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\tngAfterViewChecked()\n");
        sb.append("\t{\n");
        for (var ngAfterViewChecked : afterViewChecked)
        {
            sb.append("\t\t").append(ngAfterViewChecked.value().trim()).append("\n");
        }
        sb.append("\t}\n");
        return sb;
    }

    public StringBuilder renderAfterContentInit()
    {
        if (afterContentInit.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\tngAfterContentInit()\n");
        sb.append("\t{\n");
        for (var ngAfterContentInit : afterContentInit)
        {
            sb.append("\t\t").append(ngAfterContentInit.value().trim()).append("\n");
        }
        sb.append("\t}\n");
        return sb;
    }

    public StringBuilder renderAfterContentChecked()
    {
        if (afterContentChecked.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\tngAfterContentChecked()\n");
        sb.append("\t{\n");
        for (var ngAfterContentChecked : afterContentChecked)
        {
            sb.append("\t\t").append(ngAfterContentChecked.value().trim()).append("\n");
        }
        sb.append("\t}\n");
        return sb;
    }

    public StringBuilder renderModals()
    {
        if (modals.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        for (var inject : modals)
        {
            sb.append("\tconst ")
                    .append(inject.referenceName())
                    .append(" = modal(")
                    .append(inject.value())
                    .append(");\n");
        }
        return sb;
    }

    public StringBuilder renderSignals()
    {
        if (signals.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        for (var inject : signals)
        {
            sb.append("\treadonly ")
                    .append(inject.referenceName()).append(" = signal")
                    .append(!Strings.isNullOrEmpty(inject.type()) ? "<" + inject.type() + ">" : "")
                    .append("(")
                    .append(inject.value())
                    .append(");\n");
        }
        return sb;
    }

    public StringBuilder renderInputs()
    {
        if (inputs.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        for (var input : inputs)
        {
            sb.append("\t")
                    .append(input.value().trim())
                    .append(" = input");
            if (input.mandatory())
            {
                sb.append(".required");
            }

            sb.append("<").append(AnnotationUtils.getTsFilename(input.type())).append(">();\n");

        }
        return sb;
    }

    public StringBuilder renderOutputs()
    {
        if (outputs.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        for (NgOutput output : outputs)
        {
            sb.append("\t").append(output.value().trim()).append(" = output<")
                    .append(AnnotationUtils.getTsFilename(output.type()))
                    .append(">();\n");
        }
        return sb;
    }

}
