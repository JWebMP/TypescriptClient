package com.jwebmp.core.base.angular.client.implementations;

import com.guicedee.client.services.IGuiceConfig;
import com.guicedee.client.services.lifecycle.IGuiceConfigurator;

public class GuicedConfig implements IGuiceConfigurator<GuicedConfig>
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
