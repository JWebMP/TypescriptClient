package com.jwebmp.core.base.angular.client;

import com.fasterxml.jackson.annotation.*;
import com.guicedee.guicedinjection.representations.*;
import com.jwebmp.core.base.angular.client.annotations.angular.*;
import com.jwebmp.core.base.angular.client.services.interfaces.*;

import java.util.*;

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
