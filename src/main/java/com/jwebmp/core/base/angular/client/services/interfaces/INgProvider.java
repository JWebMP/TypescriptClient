package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.angular.*;

import java.util.*;

@NgProvider()
public interface INgProvider<J extends INgProvider<J>> extends IComponent<J>
{
	default List<String> declarations() {
		return new ArrayList<>();
	}
	
	default List<String> bootstrap() {
		return new ArrayList<>();
	}
	
	default List<String> assets() {
		return new ArrayList<>();
	}
	
	default List<String> exports() {
		return new ArrayList<>();
	}
	
	default List<String> schemas() {
		return new ArrayList<>();
	}
	
	default List<String> providers() {
		return List.of();
	}
	
}
