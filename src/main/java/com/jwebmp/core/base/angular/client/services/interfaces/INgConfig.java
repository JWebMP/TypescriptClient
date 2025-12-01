package com.jwebmp.core.base.angular.client.services.interfaces;

/**
	* This type of angular interface is to configure components only with no actual rendering
	*/
public interface INgConfig<J extends INgConfig<J>> extends IComponent<J>
{
		/**
			* @return Top level override so it doesn't render anything
			*/
		@Override
		default StringBuilder renderClassTs()
		{
				return new StringBuilder();
		}
}
