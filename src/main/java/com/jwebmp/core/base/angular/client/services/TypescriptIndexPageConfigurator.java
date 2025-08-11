package com.jwebmp.core.base.angular.client.services;

import com.guicedee.guicedinjection.interfaces.IDefaultService;
import com.jwebmp.core.services.IPage;

public interface TypescriptIndexPageConfigurator<J extends TypescriptIndexPageConfigurator<J>>
        extends IDefaultService<J>
{
    IPage<?> configure(IPage<?> page);
}
