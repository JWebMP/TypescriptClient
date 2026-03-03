package com.jwebmp.core.base.angular.client.implementations;

import com.google.inject.Inject;
import com.guicedee.client.services.lifecycle.IGuicePostStartup;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;

import java.util.List;

public class AngularTypeScriptPostStartup implements IGuicePostStartup<AngularTypeScriptPostStartup> {
    @Override
    public List<Uni<Boolean>> postLoad() {
        return List.of(Uni.createFrom().item(() -> {
            AnnotationHelper.startup();
            return true;
        }));
    }
}
