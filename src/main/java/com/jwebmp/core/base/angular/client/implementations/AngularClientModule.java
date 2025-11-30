package com.jwebmp.core.base.angular.client.implementations;

import com.google.inject.AbstractModule;
import com.guicedee.client.services.lifecycle.IGuiceModule;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;

public class AngularClientModule extends AbstractModule implements IGuiceModule<AngularClientModule>
{
    @Override
    protected void configure()
    {
        bind(AnnotationHelper.class).toInstance(AnnotationHelper.instance);
    }
}
