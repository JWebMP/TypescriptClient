package com.jwebmp.core.base.angular.client.services;

import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnInit;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportModule;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportProvider;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import com.jwebmp.core.base.angular.client.services.interfaces.IComponent;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public abstract class AbstractNgConfiguration<T extends IComponent<?>>
{
    protected final Set<NgOnInit> onInit = new LinkedHashSet<>();
    protected final Set<NgOnDestroy> onDestroy = new LinkedHashSet<>();

    protected final Set<NgImportReference> importReferences = new LinkedHashSet<>();

    protected final Set<NgField> fields = new LinkedHashSet<>();
    protected final Set<NgGlobalField> globalFields = new LinkedHashSet<>();
    protected final Set<NgMethod> methods = new LinkedHashSet<>();
    protected final Set<NgInterface> interfaces = new LinkedHashSet<>();

    protected final Set<NgImportModule> importModules = new LinkedHashSet<>();
    protected final Set<NgImportProvider> importProviders = new LinkedHashSet<>();

    protected final Set<NgConstructorParameter> constructorParameters = new LinkedHashSet<>();
    protected final Set<NgConstructorBody> constructorBodies = new LinkedHashSet<>();

    protected final Set<NgInject> injects = new LinkedHashSet<>();

    public abstract T getRootComponent();


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
            sb.append("\t\t").append(ngOnInit.value().trim()).append("\n");
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
            for (String s : ngOnDestroy.value().split("\n"))
            {
                sb.append("\t\t").append(s).append("\n");
            }
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
            String value = field.value().trim();
            if (!value.endsWith(";"))
            {
                value += ";";
            }
            sb.append("\t").append(value).append("\n");
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
            String value = field.value().trim();
            if (!value.endsWith(";"))
            {
                value += ";";
            }
            sb.append("\t").append(value).append("\n");
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
            var lines = method.value().split("\n");
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

                sb.append(value).append("\n");
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
            sb.append(parameter.value().trim());
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
            var lines = constructorBody.value().split("\n");
            for (String line : lines)
            {
                String value = line;
                if (!value.startsWith("\t"))
                {
                    value = "\t\t" + value;
                }
                sb.append(value).append("\n");
            }
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
            sb.append("\n\t\t" + importProvider.value().trim()).append(",\n");
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
            sb.append("\t\t" + importProvider.value().trim()).append(",\n");
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
            sb.append(importReference.value().trim());
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
