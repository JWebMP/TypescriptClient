package com.jwebmp.core.base.angular.client.services;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LocaleServiceRenderingTest
{
    @Test
    void rendersRuntimeService() throws Exception
    {
        var service = new LocaleService();
        // Exclude unrelated test fixtures' global constructor parameters from this standalone artifact.
        var globals = AnnotationHelper.instance.getGlobalAnnotations(
                com.jwebmp.core.base.angular.client.annotations.globals.NgGlobalConstructorParameter.class);
        var saved = new java.util.ArrayList<>(globals);
        var previousDirectory = com.jwebmp.core.base.angular.client.services.interfaces.IComponent.getCurrentAppFile().get();
        String output;
        try
        {
            globals.clear();
            output = service.renderClassTs().toString();
            Path translations = Path.of("target", "locale-runtime", "TranslationService.ts");
            Files.createDirectories(translations.getParent());
            com.jwebmp.core.base.angular.client.services.interfaces.IComponent.getCurrentAppFile().set(Path.of("target", "locale-runtime").toFile());
            Files.writeString(translations, new TranslationService().renderClassTs().toString());
        }
        finally
        {
            globals.addAll(saved);
            com.jwebmp.core.base.angular.client.services.interfaces.IComponent.getCurrentAppFile().set(previousDirectory);
        }
        assertTrue(output.contains("providedIn: 'root'"), output);
        assertTrue(output.contains("readonly locale = this.currentLocale.asReadonly()"), output);
        assertTrue(output.contains("setLocale(locale: string)"), output);
        assertTrue(output.contains("getLocaleId(locale)"), output);
        assertFalse(output.contains("public localeService: LocaleService"), output);
        Path directory = Path.of("target", "locale-runtime");
        Files.createDirectories(directory);
        Files.writeString(directory.resolve("LocaleService.ts"), output);
    }
}
