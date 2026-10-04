package com.jwebmp.core.base.angular.client.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventBusSecureSubscriptionRenderingTest {
    @Test void exposesActualSessionAndNeverQueuesCapabilitiesForReconnect() throws Exception {
        var directory = com.jwebmp.core.base.angular.client.services.interfaces.IComponent.getCurrentAppFile();
        var previous = directory.get();
        var globals = AnnotationHelper.instance.getGlobalAnnotations(
                com.jwebmp.core.base.angular.client.annotations.globals.NgGlobalConstructorParameter.class);
        var saved = new java.util.ArrayList<>(globals);
        String output;
        try {
            globals.clear();
            directory.set(java.nio.file.Path.of("target", "eventbus-runtime").toFile());
            output = new EventBusService().renderClassTs().toString();
            var root = java.nio.file.Path.of("target", "eventbus-runtime");
            java.nio.file.Files.createDirectories(root);
            java.nio.file.Files.writeString(root.resolve("EventBusService.ts"), output);
            java.nio.file.Files.writeString(root.resolve("ContextIdService.ts"), new ContextIdService().renderClassTs().toString());
        } finally { globals.addAll(saved); directory.set(previous); }
        assertTrue(output.contains("public connectionId(): string | null"));
        assertTrue(output.contains("public connectionChanges(): Observable<boolean>"));
        assertTrue(output.contains("this.serverConnectionId = frame.headers['session'] || null"));
        assertTrue(output.contains("public subscribeSecure(destination: string, headers: Record<string, string>"));
        assertTrue(output.contains("this.secureSubscriptions.clear()"));
        assertTrue(output.contains("this.connectionId() === connection"));
        assertTrue(output.contains("subscription.unsubscribe()"));
        assertTrue(output.contains("...headers, id: this.generateGUID(), ack: 'auto'"));
        int begin = output.indexOf("public subscribeSecure(");
        int end = output.indexOf("private normalizeAddress(", begin);
        assertTrue(end > begin);
        String method = output.substring(begin, end);
        assertFalse(method.contains("queueListener"));
        assertFalse(method.contains("messageQueue"));
        assertFalse(method.contains("console."));
        assertFalse(method.contains("Storage"));
    }
}
