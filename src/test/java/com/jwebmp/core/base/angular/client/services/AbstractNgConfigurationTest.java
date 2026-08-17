package com.jwebmp.core.base.angular.client.services;

import com.jwebmp.core.base.angular.client.annotations.angular.NgDataType;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.services.interfaces.AnnotationUtils;
import com.jwebmp.core.base.angular.client.services.interfaces.IComponent;
import com.jwebmp.core.base.angular.client.services.interfaces.INgDataType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbstractNgConfigurationTest
{
    @Test
    void splitComponentReferencesAddsNewFieldForDataTypeClass()
    {
        TestConfiguration configuration = new TestConfiguration();

        configuration.getComponentReferences()
                     .add(AnnotationUtils.getNgComponentReference(PlainDataType.class));

        configuration.splitComponentReferences();

        assertTrue(configuration.renderFields()
                                .toString()
                                .contains("plainDataType = new PlainDataType();"));
        assertTrue(configuration.getImportReferences()
                                .stream()
                                .anyMatch(ref -> ref.value().equals("PlainDataType") && ref.reference().equals("./PlainDataType")));
    }

    @Test
    void splitComponentReferencesAddsInjectFieldForInjectableDataType()
    {
        TestConfiguration configuration = new TestConfiguration();

        configuration.getComponentReferences()
                     .add(AnnotationUtils.getNgComponentReference(InjectableDataType.class));

        configuration.splitComponentReferences();

        String fields = configuration.renderFields()
                                     .toString();
        assertTrue(fields.contains("readonly injectableDataType = inject(InjectableDataType);"), "Got:\n" + fields);
        assertFalse(fields.contains("new InjectableDataType()"), "Injectable datatypes should not be manually constructed. Got:\n" + fields);
        assertTrue(configuration.getImportReferences()
                                .stream()
                                .anyMatch(ref -> ref.value().equals("inject") && ref.reference().equals("@angular/core")));
        assertTrue(configuration.getImportReferences()
                                .stream()
                                .anyMatch(ref -> ref.value().equals("InjectableDataType") && ref.reference().equals("./InjectableDataType")));
    }

    @Test
    void splitComponentReferencesDoesNotAddFieldForDataTypeInterface()
    {
        TestConfiguration configuration = new TestConfiguration();

        configuration.getComponentReferences()
                     .add(AnnotationUtils.getNgComponentReference(InterfaceDataType.class));

        configuration.splitComponentReferences();

        assertEquals("", configuration.renderFields()
                                      .toString());
    }

    static class TestConfiguration extends AbstractNgConfiguration<TestRoot>
    {
        private final TestRoot rootComponent = new TestRoot();

        @Override
        public TestRoot getRootComponent()
        {
            return rootComponent;
        }

        @Override
        protected List<NgImportReference> retrieveRelativePathForReference(NgComponentReference importReference)
        {
            String tsName = AnnotationUtils.getTsFilename(importReference.value());
            return List.of(AnnotationUtils.getNgImportReference(tsName, "./" + tsName));
        }
    }

    static class TestRoot implements IComponent<TestRoot>
    {
    }

    @NgDataType(NgDataType.DataTypeClass.Class)
    static class PlainDataType implements INgDataType<PlainDataType>
    {
    }

    @NgDataType(value = NgDataType.DataTypeClass.Class, injectable = true)
    static class InjectableDataType implements INgDataType<InjectableDataType>
    {
    }

    @NgDataType(NgDataType.DataTypeClass.Interface)
    static class InterfaceDataType implements INgDataType<InterfaceDataType>
    {
    }
}
