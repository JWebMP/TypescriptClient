package com.jwebmp.core.base.angular.client.services.spi;

import com.guicedee.client.services.IDefaultService;
import com.jwebmp.core.base.angular.client.annotations.constructors.*;

import java.util.*;

@FunctionalInterface
public interface OnGetAllConstructorParameters extends IDefaultService<OnGetAllConstructorParameters>
{
	void perform(List<NgConstructorParameter> allParameters, Object instance);
}
