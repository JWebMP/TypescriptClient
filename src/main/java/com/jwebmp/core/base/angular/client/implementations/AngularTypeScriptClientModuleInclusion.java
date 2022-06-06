package com.jwebmp.core.base.angular.client.implementations;

import com.guicedee.guicedinjection.interfaces.*;
import jakarta.validation.constraints.*;

import java.util.*;

public class AngularTypeScriptClientModuleInclusion implements IGuiceScanModuleInclusions<AngularTypeScriptClientModuleInclusion>
{
	@Override
	public @NotNull Set<String> includeModules()
	{
		return Set.of("com.jwebmp.core.base.angular.client");
	}
}
