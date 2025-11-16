package com.jwebmp.core.base.angular.client.services;

import com.guicedee.client.IGuiceContext;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgDataTypeReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils;
import com.jwebmp.core.base.angular.client.services.interfaces.IComponent;
import com.jwebmp.core.base.angular.client.services.interfaces.INgDataService;
import com.jwebmp.core.base.angular.client.services.interfaces.ImportsStatementsComponent;

import java.util.List;

public class DataServiceReferences extends AbstractReferences<DataServiceConfiguration<?>>
{
    private static final ThreadLocal<DataServiceConfiguration> DataServiceConfigurations = ThreadLocal.withInitial(() -> new DataServiceConfiguration<>());

    public void onDirectiveConfigure(INgDataService<?> dataService, IComponent<?> component)
    {
        if (dataService == null && component instanceof INgDataService cDs)
        {
            DataServiceConfigurations.get().setRootComponent(cDs);
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

        if (dataService == null && component instanceof INgDataService cDs)
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
    }

    public DataServiceConfiguration<?> getConfiguration()
    {
        return DataServiceConfigurations.get();
    }

    public static DataServiceConfiguration getDataServiceConfigurations(INgDataService<?> dataService)
    {
        if (DataServiceConfigurations.get().getRootComponent() == null)
        {
            DataServiceConfigurations.get().setRootComponent(dataService);
            new DataServiceReferences().onDirectiveConfigure(null, dataService);
        }
        return DataServiceConfigurations.get();
    }

    public static void clearThread()
    {
        DataServiceConfigurations.remove();
    }
}
