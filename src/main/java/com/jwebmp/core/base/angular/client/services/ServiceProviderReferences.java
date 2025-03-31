package com.jwebmp.core.base.angular.client.services;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgDataTypeReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.services.interfaces.*;

import java.util.List;

public class ServiceProviderReferences extends AbstractReferences<ServiceProviderConfiguration<?>>
{
    private static final ThreadLocal<ServiceProviderConfiguration> ServiceProviderConfigurations = ThreadLocal.withInitial(() -> new ServiceProviderConfiguration<>());

    public void onServiceProviderRender(INgServiceProvider<?> dataService, IComponent<?> component)
    {
        if (dataService == null && component instanceof INgServiceProvider cDs)
        {
            ServiceProviderConfigurations.get().setRootComponent(cDs, cDs.getAnnotation());
        }
        boolean checkForParent = false;
        if (dataService != null)
        {
            checkForParent = true;
        }
        processClass(component.getClass(), checkForParent);
        if (!checkForParent)
        {
            unwrapMethods(component, checkForParent);
        }

        if (dataService == null && component instanceof INgServiceProvider cDs)
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
                onServiceProviderRender(cDs, (IComponent<?>) refObject);
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
                onServiceProviderRender(cDs, (IComponent<?>) refObject);
            }

        }
    }

    public ServiceProviderConfiguration getConfiguration()
    {
        return ServiceProviderConfigurations.get();
    }

    public static ServiceProviderConfiguration getServiceProviderConfigurations(INgServiceProvider<?> dataService)
    {
        if (ServiceProviderConfigurations.get().getRootComponent() == null)
        {
            ServiceProviderConfigurations.get().setRootComponent(dataService, dataService.getAnnotation());
            new ServiceProviderReferences().onServiceProviderRender(null, dataService);
        }
        return ServiceProviderConfigurations.get();
    }

    public static void clearThread()
    {
        ServiceProviderConfigurations.remove();
    }
}
