package com.jwebmp.core.base.angular.client.services.interfaces;

import java.util.*;

public interface INgRoutable<J extends Enum<J> & INgRoutable<J>> extends IComponent<J>
{
	default Map<String, String> imports()
	{
		return new HashMap<>();
	}
}
