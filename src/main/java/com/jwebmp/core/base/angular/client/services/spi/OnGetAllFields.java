package com.jwebmp.core.base.angular.client.services.spi;

import com.guicedee.client.services.IDefaultService;
import com.jwebmp.core.base.angular.client.annotations.structures.*;

import java.util.*;

@FunctionalInterface
public interface OnGetAllFields extends IDefaultService<OnGetAllFields>
{
	void perform(List<NgField> allParameters, Object instance);
}
