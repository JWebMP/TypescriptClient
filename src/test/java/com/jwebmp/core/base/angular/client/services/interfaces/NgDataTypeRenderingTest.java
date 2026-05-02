package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.angular.NgDataType;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorBody;
import com.jwebmp.core.base.angular.client.annotations.constructors.NgConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.functions.*;
import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportModule;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportProvider;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for NgDataType TypeScript rendering.
 * Covers:
 * - DataTypeClass variants (Class, Interface, Function, Enum, Const)
 * - Java field -> TypeScript type mapping
 * - onParent/onSelf propagation on referenced data types
 * - Annotation rendering on data type classes
 */
class NgDataTypeRenderingTest
{
    // ═══════════════════════════════════════════════════════════════════════════
    // Test data types with various DataTypeClass settings
    // ═══════════════════════════════════════════════════════════════════════════

    @NgDataType(NgDataType.DataTypeClass.Class)
    static class ClassDataType implements INgDataType<ClassDataType>
    {
        private String name;
        private Integer age;
    }

    @NgDataType(NgDataType.DataTypeClass.Interface)
    static class InterfaceDataType implements INgDataType<InterfaceDataType>
    {
        private String title;
        private boolean active;
    }

    @NgDataType(value = NgDataType.DataTypeClass.Function, returnType = "Observable<any>", name = "fetchData")
    static class FunctionDataType implements INgDataType<FunctionDataType>
    {
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Data type with all supported Java field types
    // ═══════════════════════════════════════════════════════════════════════════

    @NgDataType(NgDataType.DataTypeClass.Class)
    static class AllFieldTypesDataType implements INgDataType<AllFieldTypesDataType>
    {
        private String stringField;
        private int intField;
        private Integer integerField;
        private double doubleField;
        private Double doubleObjField;
        private boolean boolField;
        private Boolean boolObjField;
        private long longField;
        private Long longObjField;
        private BigDecimal decimalField;
        private LocalDateTime dateTimeField;
        private LocalDate dateField;
        private OffsetDateTime offsetField;
        private UUID uuidField;
        private List<String> stringList;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Data type with onParent/onSelf annotations
    // ═══════════════════════════════════════════════════════════════════════════

    @NgDataType(NgDataType.DataTypeClass.Class)
    @NgImportReference(value = "Injectable", reference = "@angular/core", onParent = false, onSelf = true)
    @NgImportReference(value = "inject", reference = "@angular/core", onParent = true, onSelf = false)
    @NgImportModule(value = "SharedModule", onParent = true, onSelf = false)
    @NgImportProvider(value = "DataService", onParent = true, onSelf = false)
    @NgField(value = "parentField: string = 'hello';", onParent = true, onSelf = false)
    @NgField(value = "selfField: number = 0;", onParent = false, onSelf = true)
    @NgGlobalField(value = "const DATA_CONST = 'value';", onParent = true, onSelf = false)
    @NgSignal(value = "'init'", type = "string", referenceName = "dataSignal", onParent = true, onSelf = false)
    @NgSignalComputed(value = "() => this.dataSignal().length", referenceName = "dataLength", onParent = true, onSelf = false)
    @NgSignalEffect(value = "() => { console.log(this.dataSignal()); }", referenceName = "dataLog", onParent = true, onSelf = false)
    @NgMethod(value = "parentMethod(): void { }", onParent = true, onSelf = false)
    @NgMethod(value = "selfMethod(): string { return ''; }", onParent = false, onSelf = true)
    @NgInterface(value = "OnInit", onParent = true, onSelf = false)
    @NgInject(value = "SomeService", referenceName = "svc", onParent = true, onSelf = false)
    @NgConstructorParameter(value = "private parentParam: ParentService", onParent = true, onSelf = false)
    @NgConstructorParameter(value = "private selfParam: SelfService", onParent = false, onSelf = true)
    @NgConstructorBody(value = "console.log('parent body');", onParent = true, onSelf = false)
    @NgConstructorBody(value = "console.log('self body');", onParent = false, onSelf = true)
    @NgOnInit(value = "this.loadData();", onParent = true, onSelf = false)
    @NgOnDestroy(value = "this.cleanup();", onParent = true, onSelf = false)
    @NgAfterViewInit(value = "this.renderChart();", onParent = true, onSelf = false)
    @NgAfterViewChecked(value = "this.detectChanges();", onParent = true, onSelf = false)
    @NgAfterContentInit(value = "this.contentReady();", onParent = true, onSelf = false)
    @NgAfterContentChecked(value = "this.validateContent();", onParent = true, onSelf = false)
    static class OnParentDataType implements INgDataType<OnParentDataType>
    {
        private String value;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Data type referenced by another component
    // ═══════════════════════════════════════════════════════════════════════════

    @NgComponentReference(OnParentDataType.class)
    static class ConsumerOfDataType
    {
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NgDataType annotation attribute tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testDataTypeClassAnnotation()
    {
        NgDataType ann = ClassDataType.class.getAnnotation(NgDataType.class);
        assertNotNull(ann);
        assertEquals(NgDataType.DataTypeClass.Class, ann.value());
    }

    @Test
    void testDataTypeInterfaceAnnotation()
    {
        NgDataType ann = InterfaceDataType.class.getAnnotation(NgDataType.class);
        assertEquals(NgDataType.DataTypeClass.Interface, ann.value());
    }

    @Test
    void testDataTypeFunctionAnnotation()
    {
        NgDataType ann = FunctionDataType.class.getAnnotation(NgDataType.class);
        assertEquals(NgDataType.DataTypeClass.Function, ann.value());
        assertEquals("Observable<any>", ann.returnType());
        assertEquals("fetchData", ann.name());
    }

    @Test
    void testDataTypeClassDescription()
    {
        assertEquals("class", NgDataType.DataTypeClass.Class.description());
        assertEquals("interface", NgDataType.DataTypeClass.Interface.description());
        assertEquals("function", NgDataType.DataTypeClass.Function.description());
        assertEquals("enum", NgDataType.DataTypeClass.Enum.description());
        assertEquals("const", NgDataType.DataTypeClass.Const.description());
        assertEquals("abstractclass", NgDataType.DataTypeClass.AbstractClass.description());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Field type mapping tests (INgDataType.typeField)
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testStringFieldRendersAsString() throws NoSuchFieldException
    {
        var dt = new AllFieldTypesDataType();
        var field = AllFieldTypesDataType.class.getDeclaredField("stringField");
        String type = dt.typeField(String.class, field);
        assertEquals("string", type);
    }

    @Test
    void testIntFieldRendersAsNumber() throws NoSuchFieldException
    {
        var dt = new AllFieldTypesDataType();
        var field = AllFieldTypesDataType.class.getDeclaredField("intField");
        String type = dt.typeField(int.class, field);
        assertEquals("number", type);
    }

    @Test
    void testIntegerFieldRendersAsNumber() throws NoSuchFieldException
    {
        var dt = new AllFieldTypesDataType();
        var field = AllFieldTypesDataType.class.getDeclaredField("integerField");
        String type = dt.typeField(Integer.class, field);
        assertEquals("number", type);
    }

    @Test
    void testDoubleFieldRendersAsNumber() throws NoSuchFieldException
    {
        var dt = new AllFieldTypesDataType();
        var field = AllFieldTypesDataType.class.getDeclaredField("doubleField");
        String type = dt.typeField(double.class, field);
        assertEquals("number", type);
    }

    @Test
    void testBooleanFieldRendersAsBoolean() throws NoSuchFieldException
    {
        var dt = new AllFieldTypesDataType();
        var field = AllFieldTypesDataType.class.getDeclaredField("boolField");
        String type = dt.typeField(boolean.class, field);
        assertEquals("boolean", type);
    }

    @Test
    void testBooleanObjFieldRendersAsBoolean() throws NoSuchFieldException
    {
        var dt = new AllFieldTypesDataType();
        var field = AllFieldTypesDataType.class.getDeclaredField("boolObjField");
        String type = dt.typeField(Boolean.class, field);
        assertEquals("boolean", type);
    }

    @Test
    void testLongFieldRendersAsNumber() throws NoSuchFieldException
    {
        var dt = new AllFieldTypesDataType();
        var field = AllFieldTypesDataType.class.getDeclaredField("longField");
        String type = dt.typeField(long.class, field);
        assertEquals("number", type);
    }

    @Test
    void testBigDecimalFieldRendersAsNumber() throws NoSuchFieldException
    {
        var dt = new AllFieldTypesDataType();
        var field = AllFieldTypesDataType.class.getDeclaredField("decimalField");
        String type = dt.typeField(BigDecimal.class, field);
        assertEquals("number", type);
    }

    @Test
    void testLocalDateTimeFieldRendersAsDate() throws NoSuchFieldException
    {
        var dt = new AllFieldTypesDataType();
        var field = AllFieldTypesDataType.class.getDeclaredField("dateTimeField");
        String type = dt.typeField(LocalDateTime.class, field);
        assertEquals("Date", type);
    }

    @Test
    void testLocalDateFieldRendersAsDate() throws NoSuchFieldException
    {
        var dt = new AllFieldTypesDataType();
        var field = AllFieldTypesDataType.class.getDeclaredField("dateField");
        String type = dt.typeField(LocalDate.class, field);
        assertEquals("Date", type);
    }

    @Test
    void testOffsetDateTimeFieldRendersAsDate() throws NoSuchFieldException
    {
        var dt = new AllFieldTypesDataType();
        var field = AllFieldTypesDataType.class.getDeclaredField("offsetField");
        String type = dt.typeField(OffsetDateTime.class, field);
        assertEquals("Date", type);
    }

    @Test
    void testUUIDFieldRendersAsString() throws NoSuchFieldException
    {
        var dt = new AllFieldTypesDataType();
        var field = AllFieldTypesDataType.class.getDeclaredField("uuidField");
        String type = dt.typeField(UUID.class, field);
        assertEquals("string", type);
    }

    @Test
    void testListFieldRendersAsArray() throws NoSuchFieldException
    {
        var dt = new AllFieldTypesDataType();
        var field = AllFieldTypesDataType.class.getDeclaredField("stringList");
        String type = dt.typeField(List.class, field);
        assertEquals("string[]", type);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // onParent/onSelf annotation filtering on data types
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testOnParentImportReferences()
    {
        NgImportReference[] refs = OnParentDataType.class.getAnnotationsByType(NgImportReference.class);
        List<NgImportReference> parentRefs = new ArrayList<>();
        List<NgImportReference> selfRefs = new ArrayList<>();
        for (NgImportReference r : refs)
        {
            if (r.onParent()) parentRefs.add(r);
            if (r.onSelf()) selfRefs.add(r);
        }
        assertEquals(1, parentRefs.size());
        assertEquals("inject", parentRefs.get(0).value());
        assertEquals(1, selfRefs.size());
        assertEquals("Injectable", selfRefs.get(0).value());
    }

    @Test
    void testOnParentFields()
    {
        NgField[] fields = OnParentDataType.class.getAnnotationsByType(NgField.class);
        List<NgField> parentFields = new ArrayList<>();
        List<NgField> selfFields = new ArrayList<>();
        for (NgField f : fields)
        {
            if (f.onParent()) parentFields.add(f);
            if (f.onSelf()) selfFields.add(f);
        }
        assertEquals(1, parentFields.size());
        assertTrue(parentFields.get(0).value().contains("parentField"));
        assertEquals(1, selfFields.size());
        assertTrue(selfFields.get(0).value().contains("selfField"));
    }

    @Test
    void testOnParentSignals()
    {
        NgSignal[] signals = OnParentDataType.class.getAnnotationsByType(NgSignal.class);
        assertEquals(1, signals.length);
        assertTrue(signals[0].onParent());
        assertFalse(signals[0].onSelf());
        assertEquals("dataSignal", signals[0].referenceName());
    }

    @Test
    void testOnParentSignalComputed()
    {
        NgSignalComputed[] computeds = OnParentDataType.class.getAnnotationsByType(NgSignalComputed.class);
        assertEquals(1, computeds.length);
        assertTrue(computeds[0].onParent());
        assertFalse(computeds[0].onSelf());
        assertEquals("dataLength", computeds[0].referenceName());
    }

    @Test
    void testOnParentSignalEffect()
    {
        NgSignalEffect[] effects = OnParentDataType.class.getAnnotationsByType(NgSignalEffect.class);
        assertEquals(1, effects.length);
        assertTrue(effects[0].onParent());
        assertFalse(effects[0].onSelf());
        assertEquals("dataLog", effects[0].referenceName());
    }

    @Test
    void testOnParentMethods()
    {
        NgMethod[] methods = OnParentDataType.class.getAnnotationsByType(NgMethod.class);
        List<NgMethod> parentMethods = new ArrayList<>();
        List<NgMethod> selfMethods = new ArrayList<>();
        for (NgMethod m : methods)
        {
            if (m.onParent()) parentMethods.add(m);
            if (m.onSelf()) selfMethods.add(m);
        }
        assertEquals(1, parentMethods.size());
        assertTrue(parentMethods.get(0).value().contains("parentMethod"));
        assertEquals(1, selfMethods.size());
        assertTrue(selfMethods.get(0).value().contains("selfMethod"));
    }

    @Test
    void testOnParentInterface()
    {
        NgInterface[] ifaces = OnParentDataType.class.getAnnotationsByType(NgInterface.class);
        assertEquals(1, ifaces.length);
        assertTrue(ifaces[0].onParent());
        assertFalse(ifaces[0].onSelf());
        assertEquals("OnInit", ifaces[0].value());
    }

    @Test
    void testOnParentInject()
    {
        NgInject[] injects = OnParentDataType.class.getAnnotationsByType(NgInject.class);
        assertEquals(1, injects.length);
        assertTrue(injects[0].onParent());
        assertFalse(injects[0].onSelf());
        assertEquals("svc", injects[0].referenceName());
    }

    @Test
    void testOnParentConstructorParameters()
    {
        NgConstructorParameter[] params = OnParentDataType.class.getAnnotationsByType(NgConstructorParameter.class);
        List<NgConstructorParameter> parentParams = new ArrayList<>();
        List<NgConstructorParameter> selfParams = new ArrayList<>();
        for (NgConstructorParameter p : params)
        {
            if (p.onParent()) parentParams.add(p);
            if (p.onSelf()) selfParams.add(p);
        }
        assertEquals(1, parentParams.size());
        assertTrue(parentParams.get(0).value().contains("parentParam"));
        assertEquals(1, selfParams.size());
        assertTrue(selfParams.get(0).value().contains("selfParam"));
    }

    @Test
    void testOnParentConstructorBodies()
    {
        NgConstructorBody[] bodies = OnParentDataType.class.getAnnotationsByType(NgConstructorBody.class);
        List<NgConstructorBody> parentBodies = new ArrayList<>();
        List<NgConstructorBody> selfBodies = new ArrayList<>();
        for (NgConstructorBody b : bodies)
        {
            if (b.onParent()) parentBodies.add(b);
            if (b.onSelf()) selfBodies.add(b);
        }
        assertEquals(1, parentBodies.size());
        assertTrue(parentBodies.get(0).value().contains("parent body"));
        assertEquals(1, selfBodies.size());
        assertTrue(selfBodies.get(0).value().contains("self body"));
    }

    @Test
    void testOnParentLifecycleMethods()
    {
        NgOnInit[] inits = OnParentDataType.class.getAnnotationsByType(NgOnInit.class);
        assertEquals(1, inits.length);
        assertTrue(inits[0].onParent());
        assertFalse(inits[0].onSelf());

        NgOnDestroy[] destroys = OnParentDataType.class.getAnnotationsByType(NgOnDestroy.class);
        assertEquals(1, destroys.length);
        assertTrue(destroys[0].onParent());
        assertFalse(destroys[0].onSelf());

        NgAfterViewInit[] avis = OnParentDataType.class.getAnnotationsByType(NgAfterViewInit.class);
        assertEquals(1, avis.length);
        assertTrue(avis[0].onParent());

        NgAfterViewChecked[] avcs = OnParentDataType.class.getAnnotationsByType(NgAfterViewChecked.class);
        assertEquals(1, avcs.length);
        assertTrue(avcs[0].onParent());

        NgAfterContentInit[] acis = OnParentDataType.class.getAnnotationsByType(NgAfterContentInit.class);
        assertEquals(1, acis.length);
        assertTrue(acis[0].onParent());

        NgAfterContentChecked[] accs = OnParentDataType.class.getAnnotationsByType(NgAfterContentChecked.class);
        assertEquals(1, accs.length);
        assertTrue(accs[0].onParent());
    }

    @Test
    void testOnParentImportModule()
    {
        NgImportModule[] modules = OnParentDataType.class.getAnnotationsByType(NgImportModule.class);
        assertEquals(1, modules.length);
        assertTrue(modules[0].onParent());
        assertFalse(modules[0].onSelf());
        assertEquals("SharedModule", modules[0].value());
    }

    @Test
    void testOnParentImportProvider()
    {
        NgImportProvider[] providers = OnParentDataType.class.getAnnotationsByType(NgImportProvider.class);
        assertEquals(1, providers.length);
        assertTrue(providers[0].onParent());
        assertFalse(providers[0].onSelf());
        assertEquals("DataService", providers[0].value());
    }

    @Test
    void testOnParentGlobalField()
    {
        NgGlobalField[] gfields = OnParentDataType.class.getAnnotationsByType(NgGlobalField.class);
        assertEquals(1, gfields.length);
        assertTrue(gfields[0].onParent());
        assertFalse(gfields[0].onSelf());
        assertTrue(gfields[0].value().contains("DATA_CONST"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Consumer referencing the data type picks up onParent annotations
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testConsumerHasComponentReference()
    {
        NgComponentReference[] refs = ConsumerOfDataType.class.getAnnotationsByType(NgComponentReference.class);
        assertEquals(1, refs.length);
        assertEquals(OnParentDataType.class, refs[0].value());
    }

    @Test
    void testConsumerPicksUpOnParentAnnotationsFromReference()
    {
        // When ConsumerOfDataType references OnParentDataType,
        // all annotations with onParent=true should propagate.
        // Verify the referenced class has the expected onParent annotations
        NgComponentReference ref = ConsumerOfDataType.class.getAnnotation(NgComponentReference.class);
        Class<?> referenced = ref.value();

        // Check import references with onParent=true
        NgImportReference[] refs = referenced.getAnnotationsByType(NgImportReference.class);
        long parentImports = Arrays.stream(refs).filter(NgImportReference::onParent).count();
        assertEquals(1, parentImports, "Should have 1 import ref with onParent=true");

        // Check fields with onParent=true
        NgField[] fields = referenced.getAnnotationsByType(NgField.class);
        long parentFields = Arrays.stream(fields).filter(NgField::onParent).count();
        assertEquals(1, parentFields, "Should have 1 field with onParent=true");

        // Check signals with onParent=true
        NgSignal[] signals = referenced.getAnnotationsByType(NgSignal.class);
        long parentSignals = Arrays.stream(signals).filter(NgSignal::onParent).count();
        assertEquals(1, parentSignals, "Should have 1 signal with onParent=true");

        // Check computed with onParent=true
        NgSignalComputed[] computeds = referenced.getAnnotationsByType(NgSignalComputed.class);
        long parentComputeds = Arrays.stream(computeds).filter(NgSignalComputed::onParent).count();
        assertEquals(1, parentComputeds, "Should have 1 computed with onParent=true");

        // Check effect with onParent=true
        NgSignalEffect[] effects = referenced.getAnnotationsByType(NgSignalEffect.class);
        long parentEffects = Arrays.stream(effects).filter(NgSignalEffect::onParent).count();
        assertEquals(1, parentEffects, "Should have 1 effect with onParent=true");

        // Check methods with onParent=true
        NgMethod[] methods = referenced.getAnnotationsByType(NgMethod.class);
        long parentMethods = Arrays.stream(methods).filter(NgMethod::onParent).count();
        assertEquals(1, parentMethods, "Should have 1 method with onParent=true");

        // Check lifecycle with onParent=true
        NgOnInit[] inits = referenced.getAnnotationsByType(NgOnInit.class);
        assertTrue(inits[0].onParent(), "OnInit should have onParent=true");

        NgOnDestroy[] destroys = referenced.getAnnotationsByType(NgOnDestroy.class);
        assertTrue(destroys[0].onParent(), "OnDestroy should have onParent=true");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // renderObjectStructure tests
    // ═══════════════════════════════════════════════════════════════════════════

    @NgDataType(NgDataType.DataTypeClass.Class)
    static class SimpleStructDataType implements INgDataType<SimpleStructDataType>
    {
        private String name;
        private int count;
        private boolean active;
    }

    @Test
    void testRenderObjectStructure()
    {
        StringBuilder result = INgDataType.renderObjectStructure(SimpleStructDataType.class);
        String str = result.toString();

        assertTrue(str.startsWith("{"), "Should start with {");
        assertTrue(str.endsWith("}"), "Should end with }");
        assertTrue(str.contains("name"), "Should contain 'name' field");
        assertTrue(str.contains("count"), "Should contain 'count' field");
        assertTrue(str.contains("active"), "Should contain 'active' field");
        assertTrue(str.contains("''"), "String should default to ''");
        assertTrue(str.contains("0"), "Int should default to 0");
        assertTrue(str.contains("false"), "Boolean should default to false");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // fields() rendering test
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testFieldsMethodGeneratesTypeScriptFields()
    {
        var dt = new ClassDataType();
        List<String> fields = dt.fields();
        assertNotNull(fields);
        assertFalse(fields.isEmpty());

        String combined = String.join("\n", fields);
        assertTrue(combined.contains("name"), "Should contain 'name' field. Got:\n" + combined);
        assertTrue(combined.contains("string"), "Name should be string type. Got:\n" + combined);
        assertTrue(combined.contains("age"), "Should contain 'age' field. Got:\n" + combined);
        assertTrue(combined.contains("number"), "Age (Integer) should be number type. Got:\n" + combined);
    }

    @Test
    void testInterfaceFieldsNoPublicPrefix()
    {
        var dt = new InterfaceDataType();
        List<String> fields = dt.fields();
        String combined = String.join("\n", fields);

        // Interface fields should NOT have "public" prefix
        assertTrue(combined.contains("title"), "Should contain 'title' field");
        assertTrue(combined.contains("active"), "Should contain 'active' field");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // isFieldRequired test
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testPrimitiveFieldIsRequired() throws NoSuchFieldException
    {
        // Integer (boxed) is not considered required unless @NotNull
        var field = AllFieldTypesDataType.class.getDeclaredField("intField");
        assertTrue(INgDataType.isFieldRequired(field), "Primitive int should be required");
    }

    @Test
    void testObjectFieldIsOptional() throws NoSuchFieldException
    {
        var field = ClassDataType.class.getDeclaredField("name");
        assertFalse(INgDataType.isFieldRequired(field), "String (no @NotNull) should be optional");
    }
}





