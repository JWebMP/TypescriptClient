package com.jwebmp.core.base.angular.client;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.guicedee.services.jsonrepresentation.IJsonRepresentation;
import com.jwebmp.core.base.angular.client.annotations.angular.NgDataType;
import com.jwebmp.core.base.angular.client.services.interfaces.INgDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
                getterVisibility = JsonAutoDetect.Visibility.NONE,
                setterVisibility = JsonAutoDetect.Visibility.NONE)
@NgDataType
public final class DynamicData implements INgDataType<DynamicData>, IJsonRepresentation<DynamicData>
{
	private List<Object> out = new ArrayList<>();
	
	public DynamicData addData(INgDataType<?>... out)
	{
		this.out.addAll(Arrays.asList(out));
		return this;
	}
	public DynamicData addData(Object out)
	{
		this.out.addAll(Arrays.asList(out));
		return this;
	}
}
