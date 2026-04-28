package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgField;
import com.jwebmp.core.base.angular.client.services.AnnotationHelper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that @NgField(onParent=true) and @NgImportReference(onParent=true)
 * from a referenced class (via @NgComponentReference) are propagated to the
 * parent (referencing) component.
 * <p>
 * This replicates the bug where CapabilitiesPage references App.class via
 * {@code @NgComponentReference(App.class)}, and App declares:
 * <pre>
 * @NgImportReference(value = "inject", reference = "@angular/core", onParent = true, onSelf = false)
 * @NgField(value = "app: App = inject(App);", onParent = true, onSelf = false)
 * </pre>
 * but the generated TypeScript for CapabilitiesPage is missing both.
 */
class OnParentPropagationTest
{
    // ── Simulated referenced class (like App) ──────────────────────
    @NgImportReference(value = "inject", reference = "@angular/core", onParent = true, onSelf = false)
    @NgImportReference(value = "Injectable", reference = "@angular/core")
    @NgField(value = "app: MockDataType = inject(MockDataType);", onParent = true, onSelf = false)
    @NgField(value = "selfOnlyField: string = 'hello';") // default onSelf=true, onParent=false
    static class MockDataType
    {
    }

    // ── Simulated parent component (like CapabilitiesPage) ─────────
    @NgComponentReference(MockDataType.class)
    @NgField("ownField: number = 42;")
    @NgImportReference(value = "Component", reference = "@angular/core")
    static class MockParentComponent
    {
    }

    // ── Simulated intermediate base class (like WebsitePage) ───────
    @NgComponentReference(MockDataType.class)
    static class MockBasePage
    {
    }

    // ── Simulated child that inherits from base ────────────────────
    @NgComponentReference(MockDataType.class)
    static class MockChildPage extends MockBasePage
    {
    }

    @BeforeAll
    static void setup()
    {
        // Ensure AnnotationHelper has scanned our test classes
        AnnotationHelper helper = AnnotationHelper.instance;
        helper.scanClass(MockDataType.class);
        helper.scanClass(MockParentComponent.class);
        helper.scanClass(MockBasePage.class);
        helper.scanClass(MockChildPage.class);
    }

    // ─── Field propagation tests ──────────────────────────────────

    @Test
    void onParentField_shouldBeCollectedByReferencingComponent()
    {
        AnnotationHelper helper = AnnotationHelper.instance;

        // Get @NgField annotations from MockDataType
        List<NgField> dataTypeFields = helper.getAnnotationFromClass(MockDataType.class, NgField.class);
        assertNotNull(dataTypeFields);

        // There should be 2 fields on MockDataType
        assertEquals(2, dataTypeFields.size(), "MockDataType should have 2 @NgField annotations");

        // Verify one has onParent=true
        long onParentCount = dataTypeFields.stream().filter(NgField::onParent).count();
        assertEquals(1, onParentCount, "MockDataType should have exactly 1 @NgField with onParent=true");

        // Now simulate what getAllFields() does for MockParentComponent:
        // It should collect onParent=true fields from @NgComponentReference targets
        List<NgComponentReference> compRefs = helper.getAnnotationFromClass(MockParentComponent.class, NgComponentReference.class);
        assertFalse(compRefs.isEmpty(), "MockParentComponent should have @NgComponentReference");

        // Collect onParent fields from the reference
        List<NgField> parentFields = new java.util.ArrayList<>();
        for (NgComponentReference ref : compRefs)
        {
            for (NgField field : helper.getAnnotationFromClass(ref.value(), NgField.class))
            {
                if (field.onParent())
                {
                    parentFields.add(field);
                }
            }
        }
        assertEquals(1, parentFields.size(), "Should collect 1 onParent field from MockDataType");
        assertTrue(parentFields.get(0).value().contains("inject(MockDataType)"),
                "The propagated field should be the inject(MockDataType) field");
    }

    @Test
    void onParentField_shouldNotIncludeSelfOnlyFields()
    {
        AnnotationHelper helper = AnnotationHelper.instance;

        List<NgComponentReference> compRefs = helper.getAnnotationFromClass(MockParentComponent.class, NgComponentReference.class);

        List<NgField> parentFields = new java.util.ArrayList<>();
        for (NgComponentReference ref : compRefs)
        {
            for (NgField field : helper.getAnnotationFromClass(ref.value(), NgField.class))
            {
                if (field.onParent())
                {
                    parentFields.add(field);
                }
            }
        }

        // selfOnlyField should NOT be collected (onParent defaults to false)
        boolean hasSelfOnlyField = parentFields.stream().anyMatch(f -> f.value().contains("selfOnlyField"));
        assertFalse(hasSelfOnlyField, "selfOnlyField should not be propagated to parent");
    }

    // ─── Import reference propagation tests ───────────────────────

    @Test
    void onParentImportReference_shouldBeCollectedByReferencingComponent()
    {
        AnnotationHelper helper = AnnotationHelper.instance;

        // Get @NgImportReference annotations from MockDataType
        List<NgImportReference> dataTypeImports = helper.getAnnotationFromClass(MockDataType.class, NgImportReference.class);
        assertNotNull(dataTypeImports);

        // Verify there's one with onParent=true ("inject")
        long onParentCount = dataTypeImports.stream().filter(NgImportReference::onParent).count();
        assertEquals(1, onParentCount, "MockDataType should have exactly 1 @NgImportReference with onParent=true");

        // Now simulate what getAllImportAnnotations() SHOULD do:
        // It should collect onParent=true imports from @NgComponentReference targets
        List<NgComponentReference> compRefs = helper.getAnnotationFromClass(MockParentComponent.class, NgComponentReference.class);
        assertFalse(compRefs.isEmpty(), "MockParentComponent should have @NgComponentReference");

        List<NgImportReference> parentImports = new java.util.ArrayList<>();
        for (NgComponentReference ref : compRefs)
        {
            for (NgImportReference importRef : helper.getAnnotationFromClass(ref.value(), NgImportReference.class))
            {
                if (importRef.onParent())
                {
                    parentImports.add(importRef);
                }
            }
        }

        // THIS IS THE BUG: getAllImportAnnotations() does NOT do this collection
        assertEquals(1, parentImports.size(),
                "Should collect 1 onParent import reference from MockDataType");
        assertEquals("inject", parentImports.get(0).value(),
                "The propagated import should be 'inject'");
        assertEquals("@angular/core", parentImports.get(0).reference(),
                "The propagated import reference should be '@angular/core'");
    }

    @Test
    void onParentImportReference_shouldNotIncludeSelfOnlyImports()
    {
        AnnotationHelper helper = AnnotationHelper.instance;

        List<NgComponentReference> compRefs = helper.getAnnotationFromClass(MockParentComponent.class, NgComponentReference.class);

        List<NgImportReference> parentImports = new java.util.ArrayList<>();
        for (NgComponentReference ref : compRefs)
        {
            for (NgImportReference importRef : helper.getAnnotationFromClass(ref.value(), NgImportReference.class))
            {
                if (importRef.onParent())
                {
                    parentImports.add(importRef);
                }
            }
        }

        // "Injectable" has onParent=false (default), should NOT be collected
        boolean hasInjectable = parentImports.stream().anyMatch(i -> i.value().contains("Injectable"));
        assertFalse(hasInjectable, "Injectable import should not be propagated to parent (onParent is false)");
    }

    // ─── Integration-style test: verify the gap in getAllImportAnnotations ───

    @Test
    void getAllImportAnnotations_shouldIncludeOnParentImportsFromComponentReferences()
    {
        // This test documents the EXPECTED behavior after the fix.
        // getAllImportAnnotations() should collect onParent=true @NgImportReference
        // from classes referenced via @NgComponentReference, similar to how
        // getAllFields() already collects onParent=true @NgField.
        //
        // Currently, getAllImportAnnotations() in ImportsStatementsComponent.java
        // only collects:
        //   1. The relative import path for the referenced class itself
        //   2. @NgImportReference annotations directly on the current class
        //
        // It does NOT collect onParent=true @NgImportReference from the referenced class.
        //
        // The fix should add logic parallel to IComponent.getAllFields() lines 76-88:
        //   for each @NgComponentReference → get @NgImportReference from target → if onParent → add

        AnnotationHelper helper = AnnotationHelper.instance;

        // Get all imports that SHOULD be on MockParentComponent
        List<NgImportReference> allSelfImports = helper.getAnnotationFromClass(MockParentComponent.class, NgImportReference.class);

        // Filter to onSelf (what the component itself declares)
        long selfImports = allSelfImports.stream().filter(NgImportReference::onSelf).count();
        assertEquals(1, selfImports, "MockParentComponent has 1 direct @NgImportReference (Component)");

        // Now collect what should ALSO be present from @NgComponentReference(MockDataType.class)
        List<NgComponentReference> refs = helper.getAnnotationFromClass(MockParentComponent.class, NgComponentReference.class);
        List<NgImportReference> propagated = new java.util.ArrayList<>();
        for (NgComponentReference ref : refs)
        {
            for (NgImportReference imp : helper.getAnnotationFromClass(ref.value(), NgImportReference.class))
            {
                if (imp.onParent())
                {
                    propagated.add(imp);
                }
            }
        }
        assertEquals(1, propagated.size(), "1 import should be propagated from MockDataType via onParent");
        assertEquals("inject", propagated.get(0).value());
    }
}

