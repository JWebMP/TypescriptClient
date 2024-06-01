package com.jwebmp.core.base.angular.client.implementations;

import com.guicedee.guicedinjection.interfaces.IGuicePostStartup;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AngularTypeScriptPostStartup implements IGuicePostStartup<AngularTypeScriptPostStartup>
{
    @Override
    public List<CompletableFuture<Boolean>> postLoad()
    {
        return List.of(CompletableFuture.supplyAsync(() -> {
            AnnotationHelper.startup();
            return true;
        }));
    }
}
