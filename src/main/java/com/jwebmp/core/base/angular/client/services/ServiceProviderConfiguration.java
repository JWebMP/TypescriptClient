package com.jwebmp.core.base.angular.client.services;

import com.jwebmp.core.base.angular.client.annotations.angular.NgServiceProvider;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.services.interfaces.*;
import lombok.Getter;

import java.util.List;

import static com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils.getNgComponentReference;

@Getter
public class ServiceProviderConfiguration<T extends INgServiceProvider<T>> extends AbstractNgConfiguration<T>
{
    private T rootComponent;


    public ServiceProviderConfiguration<T> setRootComponent(T rootComponent, NgServiceProvider annotation)
    {
        this.rootComponent = (T) rootComponent;

        getImportReferences().add(AnnotationUtils.getNgImportReference("inject", "@angular/core"));
        NgComponentReference reference = getNgComponentReference(annotation.value());
        var s = new ImportsStatementsComponent()
        {
        };
        List<NgImportReference> o = s.putRelativeLinkInMap(rootComponent.getClass(), reference);
        for (NgImportReference ngImportReference : o)
        {
            getImportReferences().add(AnnotationUtils.getNgImportReference(ngImportReference.value(), ngImportReference.reference(), ngImportReference.direct(), ngImportReference.wrapValueInBraces()));
        }

        NgComponentReference reference2 = getNgComponentReference(annotation.dataType());
        List<NgImportReference> o2 = s.putRelativeLinkInMap(rootComponent.getClass(), reference2);
        for (NgImportReference ngImportReference : o2)
        {
            getImportReferences().add(AnnotationUtils.getNgImportReference(ngImportReference.value(), ngImportReference.reference(), ngImportReference.direct(), ngImportReference.wrapValueInBraces()));
        }
        return this;
    }

}
