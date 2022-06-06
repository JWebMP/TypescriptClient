package com.jwebmp.core.base.angular.client.services.spi;

import com.guicedee.guicedinjection.interfaces.*;
import com.jwebmp.core.base.angular.client.annotations.constructors.*;
import com.jwebmp.core.base.angular.client.annotations.structures.*;

import java.util.*;

@FunctionalInterface
public interface OnGetAllFields extends IDefaultService<OnGetAllFields>
{
	void perform(List<NgField> allParameters, Object instance);
}
