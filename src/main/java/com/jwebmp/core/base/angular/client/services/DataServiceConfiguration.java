package com.jwebmp.core.base.angular.client.services;

import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnDestroy;
import com.jwebmp.core.base.angular.client.annotations.functions.NgOnInit;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportModule;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportProvider;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import com.jwebmp.core.base.angular.client.services.interfaces.INgComponent;
import com.jwebmp.core.base.angular.client.services.interfaces.INgDataService;
import com.jwebmp.core.base.angular.client.services.interfaces.INgDirective;
import com.jwebmp.core.base.html.interfaces.GlobalChildren;
import com.jwebmp.core.base.interfaces.IComponentHierarchyBase;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class DataServiceConfiguration<T extends INgDataService<T>> extends AbstractNgConfiguration<T>
{
    private T rootComponent;


    public DataServiceConfiguration<T> setRootComponent(T rootComponent)
    {
        this.rootComponent = (T) rootComponent;
        return this;
    }

}
