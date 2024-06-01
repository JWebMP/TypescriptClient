package com.jwebmp.core.base.angular.client.implementations;

import com.guicedee.guicedinjection.interfaces.*;

import java.util.*;

public class AngularTypeScriptClientModuleInclusion implements IGuiceScanModuleInclusions<AngularTypeScriptClientModuleInclusion>
{
	@Override
	public Set<String> includeModules()
	{
		return Set.of("com.jwebmp.core.base.angular.client");
	}
}
