package com.jwebmp.core.base.angular.client.services;

import com.jwebmp.core.base.angular.client.services.interfaces.INgDataService;
import com.jwebmp.core.base.angular.client.services.interfaces.INgDirective;
import lombok.Getter;

@Getter
public class DirectiveConfiguration<T extends INgDirective<T>> extends AbstractNgConfiguration<T>
{
    private T rootComponent;


    public DirectiveConfiguration<T> setRootComponent(T rootComponent)
    {
        this.rootComponent = (T) rootComponent;
        return this;
    }

}
