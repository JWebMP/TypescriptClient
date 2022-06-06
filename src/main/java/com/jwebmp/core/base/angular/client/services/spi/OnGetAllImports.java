package com.jwebmp.core.base.angular.client.services.spi;

import com.guicedee.guicedinjection.interfaces.*;
import com.jwebmp.core.base.angular.client.annotations.references.*;
import com.jwebmp.core.base.angular.client.annotations.structures.*;

import java.util.*;

@FunctionalInterface
public interface OnGetAllImports extends IDefaultService<OnGetAllImports>
{
	void perform(List<NgImportReference> allParameters, Object instance);
}
