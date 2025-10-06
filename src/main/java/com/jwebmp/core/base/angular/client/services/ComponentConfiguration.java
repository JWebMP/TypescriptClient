package com.jwebmp.core.base.angular.client.services;

import com.google.common.base.Strings;
import com.jwebmp.core.base.angular.client.annotations.components.NgInput;
import com.jwebmp.core.base.angular.client.annotations.components.NgOutput;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.*;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportModule;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportProvider;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils;
import com.jwebmp.core.base.angular.client.services.interfaces.INgComponent;
import com.jwebmp.core.base.angular.client.services.tstypes.bool;
import com.jwebmp.core.base.html.interfaces.GlobalChildren;
import com.jwebmp.core.base.interfaces.IComponentHierarchyBase;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class ComponentConfiguration<T extends IComponentHierarchyBase<?, T> & INgComponent<T>>
{
    private T rootComponent;

    private final Set<NgOnInit> onInit = new LinkedHashSet<>();
    private final Set<NgOnDestroy> onDestroy = new LinkedHashSet<>();
    private final Set<NgAfterViewInit> afterViewInit = new LinkedHashSet<>();
    private final Set<NgAfterViewChecked> afterViewChecked = new LinkedHashSet<>();
    private final Set<NgAfterContentInit> afterContentInit = new LinkedHashSet<>();
    private final Set<NgAfterContentChecked> afterContentChecked = new LinkedHashSet<>();

    private final Set<NgImportReference> importReferences = new LinkedHashSet<>();


    private final Set<NgField> fields = new LinkedHashSet<>();
    private final Set<NgGlobalField> globalFields = new LinkedHashSet<>();
    private final Set<NgMethod> methods = new LinkedHashSet<>();
    private final Set<NgInterface> interfaces = new LinkedHashSet<>();

    private final Set<NgInput> inputs = new LinkedHashSet<>();
    private final Set<NgOutput> outputs = new LinkedHashSet<>();

    private final Set<NgImportModule> importModules = new LinkedHashSet<>();
    private final Set<NgImportProvider> importProviders = new LinkedHashSet<>();


    private final Set<NgConstructorParameter> constructorParameters = new LinkedHashSet<>();
    private final Set<NgConstructorBody> constructorBodies = new LinkedHashSet<>();


    private final Set<NgInject> injects = new LinkedHashSet<>();
    private final Set<NgModel> models = new LinkedHashSet<>();
    private final Set<NgSignal> signals = new LinkedHashSet<>();


    public ComponentConfiguration<T> setRootComponent(IComponentHierarchyBase<GlobalChildren, ?> rootComponent)
    {
        this.rootComponent = (T) rootComponent;
        return this;
    }

    public StringBuilder renderOnInit()
    {
        if (onInit.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\tngOnInit()\n");
        sb.append("\t{\n");
        for (var ngOnInit : onInit)
        {
            sb.append("\t\t")
              .append(ngOnInit.value()
                              .trim())
              .append("\n");
        }
        sb.append("\t}\n");
        return sb;
    }

    public StringBuilder renderOnDestroy()
    {
        if (onDestroy.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        sb.append("\tngOnDestroy()\n");
        sb.append("\t{\n");
        for (var ngOnDestroy : onDestroy)
        {
            sb.append("\t\t")
              .append(ngOnDestroy.value()
                                 .trim())
              .append("\n");
        }
        sb.append("\t}\n");
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
            sb.append("\t\t")
              .append(ngAfterViewInit.value()
                                     .trim())
              .append("\n");
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
            sb.append("\t\t")
              .append(ngAfterViewChecked.value()
                                        .trim())
              .append("\n");
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
            sb.append("\t\t")
              .append(ngAfterContentInit.value()
                                        .trim())
              .append("\n");
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
            sb.append("\t\t")
              .append(ngAfterContentChecked.value()
                                           .trim())
              .append("\n");
        }
        sb.append("\t}\n");
        return sb;
    }

    public StringBuilder renderInjects()
    {
        if (injects.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        for (NgInject inject : injects)
        {
            sb.append("\treadonly ")
              .append(inject.referenceName())
              .append(" = inject(")
              .append(inject.value())
              .append(");\n");
        }
        return sb;
    }

    public StringBuilder renderModels()
    {
        if (models.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        for (var inject : models)
        {
            sb.append("\treadonly ")
              .append(inject.referenceName())
              .append(" = model<")
              .append(inject.dataType()
                            .getSimpleName())
              .append(">(")
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
              .append(inject.referenceName())
              .append(" = signal")
              .append(!Strings.isNullOrEmpty(inject.type()) ? "<" + inject.type() + ">" : "")
              .append("(")
              .append(inject.value())
              .append(");\n");
        }
        return sb;
    }

    public StringBuilder renderInterfaces()
    {
        if (interfaces.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(" implements ");
        String joinedInterfaces = interfaces.stream()
                                            .map(NgInterface::value)
                                            .collect(Collectors.joining(","));
        sb.append(joinedInterfaces);
        return sb;
    }

    public StringBuilder renderFields()
    {
        if (fields.isEmpty())
        {
            return new StringBuilder();
        }

        StringBuilder sb = new StringBuilder();
        for (NgField field : fields)
        {
            String value = field.value()
                                .trim();
            if (!value.endsWith(";"))
            {
                value += ";";
            }
            sb.append("\t")
              .append(value)
              .append("\n");
        }
        return sb;
    }

    public StringBuilder renderGlobalFields()
    {
        if (globalFields.isEmpty())
        {
            return new StringBuilder();
        }

        StringBuilder sb = new StringBuilder();
        for (var field : globalFields)
        {
            String value = field.value()
                                .trim();
            if (!value.endsWith(";"))
            {
                value += ";";
            }
            sb.append("\t")
              .append(value)
              .append("\n");
        }
        return sb;
    }

    public StringBuilder renderMethods()
    {
        if (methods.isEmpty())
        {
            return new StringBuilder();
        }

        StringBuilder sb = new StringBuilder();
        for (var method : methods)
        {
            var lines = method.value()
                              .split("\n");
            boolean openBracketHit = false;
            for (String line : lines)
            {
                String value = line;
                if (value.startsWith("}"))
                {
                    openBracketHit = false;
                }
                if (!value.startsWith("\t"))
                {
                    value = "\t" + value;
                }
                if (value.startsWith("{"))
                {
                    openBracketHit = true;
                }

                if (openBracketHit)
                {
                    value = "\t" + value;
                }

                sb.append(value)
                  .append("\n");
            }
        }
        sb.append("\n");
        return sb;
    }

    public StringBuilder renderConstructorParameters()
    {
        if (constructorParameters.isEmpty())
        {
            return new StringBuilder();
        }

        StringBuilder sb = new StringBuilder();
        int size = constructorParameters.size();
        int index = 0;

        for (NgConstructorParameter parameter : constructorParameters)
        {
            sb.append(parameter.value()
                               .trim());
            if (index < size - 1)
            {
                sb.append(",");
            }
            index++;
        }
        return sb;
    }

    public StringBuilder renderConstructorBodies()
    {
        if (constructorBodies.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        for (var constructorBody : constructorBodies)
        {
            var lines = constructorBody.value()
                                       .split("\n");
            for (String line : lines)
            {
                String value = line;
                if (!value.startsWith("\t"))
                {
                    value = "\t\t" + value;
                }
                sb.append(value)
                  .append("\n");
            }
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
              .append(input.value()
                           .trim())
              .append(" = input");
            if (input.mandatory())
            {
                sb.append(".required");
            }
            if (input.type() != null && input.type()
                                             .equals(bool.class))
            {
                sb.append("<")
                  .append("boolean")
                  .append(">();\n");
            }
            else
            {
                sb.append("<")
                  .append(AnnotationUtils.getTsFilename(input.type()));
                if (input.array())
                {
                    sb.append("[]");
                }
                sb.append(">();\n");
            }
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
            sb.append("\t")
              .append(output.value()
                            .trim())
              .append(" = output<")
              .append(AnnotationUtils.getTsFilename(output.type()))
              .append(">();\n");
        }
        return sb;
    }

    public StringBuilder renderImportProviders()
    {
        if (importProviders.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        for (var importProvider : importProviders)
        {
            sb.append("\n\t\t" + importProvider.value()
                                               .trim())
              .append(",\n");
        }
        return sb;
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
            sb.append("\t\t" + importProvider.value()
                                             .trim())
              .append(",\n");
        }
        return sb;
    }

    public StringBuilder renderImportStatements()
    {
        if (importReferences.isEmpty())
        {
            return new StringBuilder();
        }
        StringBuilder sb = new StringBuilder();
        for (var importReference : importReferences)
        {
            sb.append("import ");
            if (importReference.wrapValueInBraces())
            {
                sb.append("{");
            }
            sb.append(importReference.value()
                                     .trim());
            if (importReference.wrapValueInBraces())
            {
                sb.append("}");
            }
            sb.append(" from '");
            sb.append(importReference.reference());
            sb.append("';\n");
        }

        return sb;
    }
}
