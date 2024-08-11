package com.jwebmp.core.base.angular.client.services.spi;

import com.guicedee.guicedinjection.interfaces.IDefaultService;

import java.util.List;

@FunctionalInterface
public interface OnGetAllModuleImports extends IDefaultService<OnGetAllModuleImports>
{
    void perform(List<String> allImports, Object instance);
}
