package com.jwebmp.core.base.angular.client.implementations;

import com.google.inject.Inject;
import com.guicedee.guicedinjection.interfaces.IGuicePostStartup;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AngularTypeScriptPostStartup implements IGuicePostStartup<AngularTypeScriptPostStartup>
{
    @Inject
    private Vertx vertx;

    @Override
    public List<Future<Boolean>> postLoad()
    {
        return List.of(vertx.executeBlocking(() -> {
            AnnotationHelper.startup();
            return true;
        }));
    }
}
