package com.jwebmp.core.base.angular.client.implementations;

import com.guicedee.guicedinjection.interfaces.IGuiceConfig;
import com.guicedee.guicedinjection.interfaces.IGuiceConfigurator;

public class GuicedConfig implements IGuiceConfigurator
{
    @Override
    public IGuiceConfig<?> configure(IGuiceConfig<?> config)
    {
        config.setAnnotationScanning(true)
              .setClasspathScanning(true)
              .setFieldInfo(true)
              .setFieldScanning(true)
              .setMethodInfo(true);
        return config;
    }
}
