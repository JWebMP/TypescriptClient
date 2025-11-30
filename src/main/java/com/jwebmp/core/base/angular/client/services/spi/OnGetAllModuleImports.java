package com.jwebmp.core.base.angular.client.services.spi;

import com.guicedee.client.services.IDefaultService;

import java.util.Set;

@FunctionalInterface
public interface OnGetAllModuleImports extends IDefaultService<OnGetAllModuleImports>
{
    void perform(Set<String> allImports, Object instance);
}
