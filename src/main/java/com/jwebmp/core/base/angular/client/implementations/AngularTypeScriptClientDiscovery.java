package com.jwebmp.core.base.angular.client.implementations;

import com.guicedee.client.services.IGuiceDiscovery;
import com.jwebmp.core.base.angular.client.DynamicData;
import com.jwebmp.core.base.angular.client.services.ContextIdService;
import com.jwebmp.core.base.angular.client.services.EventBusListenerDirective;
import com.jwebmp.core.base.angular.client.services.EventBusService;
import com.jwebmp.core.base.angular.client.services.LocaleService;
import com.jwebmp.core.base.angular.client.services.TranslationService;
import com.jwebmp.core.base.angular.client.services.interfaces.INgComponent;
import com.jwebmp.core.base.angular.client.services.interfaces.INgDataService;
import com.jwebmp.core.base.angular.client.services.interfaces.INgDirective;
import com.jwebmp.core.base.angular.client.services.interfaces.INgFormControlValidatorFunction;
import com.jwebmp.core.base.angular.client.services.interfaces.INgModule;
import com.jwebmp.core.base.angular.client.services.interfaces.INgProvider;
import com.jwebmp.core.base.angular.client.services.interfaces.INgRestClient;
import com.jwebmp.core.base.angular.client.services.interfaces.INgServiceProvider;
import com.jwebmp.core.base.angular.client.services.interfaces.INgValidatorDirective;
import com.jwebmp.core.base.angular.client.services.tstypes.bool;

import java.util.List;

/** Angular annotation metadata declared by the TypeScript client module. */
public final class AngularTypeScriptClientDiscovery implements IGuiceDiscovery<AngularTypeScriptClientDiscovery>
{
    @Override
    public List<Class<?>> classes()
    {
        return List.of(
                DynamicData.class,
                ContextIdService.class,
                EventBusListenerDirective.class,
                EventBusService.class,
                LocaleService.class,
                TranslationService.class,
                INgComponent.class,
                INgDataService.class,
                INgDirective.class,
                INgFormControlValidatorFunction.class,
                INgModule.class,
                INgProvider.class,
                INgRestClient.class,
                INgServiceProvider.class,
                INgValidatorDirective.class,
                bool.class);
    }
}
