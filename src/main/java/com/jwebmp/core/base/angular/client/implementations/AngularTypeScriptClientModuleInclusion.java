package com.jwebmp.core.base.angular.client.implementations;

import com.guicedee.client.services.config.IGuiceScanModuleInclusions;

import java.util.*;

public class AngularTypeScriptClientModuleInclusion implements IGuiceScanModuleInclusions<AngularTypeScriptClientModuleInclusion>
{
	@Override
	public Set<String> includeModules()
	{
		return Set.of("com.jwebmp.core.base.angular.client");
	}
}
