package com.jwebmp.core.base.angular.client.services;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgDataTypeReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.services.interfaces.*;

import java.util.List;

public class DirectiveReferences extends AbstractReferences<DirectiveConfiguration<?>>
{
    private static final ThreadLocal<DirectiveConfiguration> DirectiveConfigurations = ThreadLocal.withInitial(() -> new DirectiveConfiguration<>());

    public void onDirectiveConfigure(INgDirective<?> directive, IComponent<?> component)
    {
        if (directive == null && component instanceof INgDirective cDs)
        {
            DirectiveConfigurations.get().setRootComponent(cDs);
        }
        boolean checkForParent = false;
        if (directive != null)
        {
            checkForParent = true;
        }
        processClass(component.getClass(), checkForParent);
        if (!checkForParent)
        {
            unwrapMethods(component, checkForParent);
        }

        if (directive == null && component instanceof INgDirective cDs)
        {
            var refs = AnnotationUtils.getAnnotation(component.getClass(), NgComponentReference.class);
            for (NgComponentReference ref : refs)
            {
                var refObject = IGuiceContext.get(ref.value());
                List<NgImportReference> importStatements = new ImportsStatementsComponent()
                {
                }.putRelativeLinkInMap(component.getClass(), ref);
                for (NgImportReference importStatement : importStatements)
                {
                    getConfiguration().getImportReferences().add(importStatement);
                }
                onDirectiveConfigure(cDs, (IComponent<?>) refObject);
            }
            var dataRefs = AnnotationUtils.getAnnotation(component.getClass(), NgDataTypeReference.class);
            for (NgDataTypeReference ref : dataRefs)
            {
                var refObject = IGuiceContext.get(ref.value());
                List<NgImportReference> importStatements = new ImportsStatementsComponent()
                {
                }.putRelativeLinkInMap(component.getClass(), ref);
                for (NgImportReference importStatement : importStatements)
                {
                    getConfiguration().getImportReferences().add(importStatement);
                }
                onDirectiveConfigure(cDs, (IComponent<?>) refObject);
            }
        }

        if (directive == null)
        {
            DirectiveConfigurations.get().splitComponentReferences();
        }
    }

    public DirectiveConfiguration getConfiguration()
    {
        return DirectiveConfigurations.get();
    }

    public static DirectiveConfiguration getDataServiceConfigurations(INgDirective<?> dataService)
    {
        if (DirectiveConfigurations.get().getRootComponent() == null)
        {
            DirectiveConfigurations.get().setRootComponent(dataService);
            new DirectiveReferences().onDirectiveConfigure(null, dataService);
        }
        return DirectiveConfigurations.get();
    }

    public static void clearThread()
    {
        DirectiveConfigurations.remove();
    }
}
