package com.jwebmp.core.base.angular.client.implementations;

import com.guicedee.guicedinjection.interfaces.IGuicePostStartup;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;

public class AngularTypeScriptPostStartup implements IGuicePostStartup<AngularTypeScriptPostStartup>
{
    @Override
    public void postLoad()
    {
        AnnotationHelper.startup();
    }
}
