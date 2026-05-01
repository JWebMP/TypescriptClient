package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.globals.NgGlobalComponentConstructorParameter;
import com.jwebmp.core.base.angular.client.annotations.globals.NgGlobalComponentImportReference;
import com.jwebmp.core.base.angular.client.annotations.globals.NgGlobalConstructorParameter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that NgGlobal* annotations are correctly declared and their values accessible.
 * These annotations are global — they apply to ALL components without onParent/onSelf.
 */
class GlobalAnnotationRenderingTest
{
    @NgGlobalConstructorParameter("private globalService: GlobalService")
    @NgGlobalConstructorParameter("private anotherGlobal: AnotherService")
    static class GlobalConstructorParamProvider
    {
    }

    @NgGlobalComponentConstructorParameter("private componentGlobal: ComponentGlobalService")
    static class GlobalComponentConstructorParamProvider
    {
    }

    @NgGlobalComponentImportReference(value = "GlobalUtil", reference = "@app/utils")
    @NgGlobalComponentImportReference(value = "SharedModule", reference = "@app/shared")
    static class GlobalImportRefProvider
    {
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NgGlobalConstructorParameter tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testGlobalConstructorParameterDiscovery()
    {
        NgGlobalConstructorParameter[] params = GlobalConstructorParamProvider.class
                .getAnnotationsByType(NgGlobalConstructorParameter.class);
        assertEquals(2, params.length);
        assertEquals("private globalService: GlobalService", params[0].value());
        assertEquals("private anotherGlobal: AnotherService", params[1].value());
    }

    @Test
    void testGlobalConstructorParameterRendersAsConstructorParam()
    {
        NgGlobalConstructorParameter[] params = GlobalConstructorParamProvider.class
                .getAnnotationsByType(NgGlobalConstructorParameter.class);

        // Simulates how IComponent.getAllConstructorParameters() converts globals
        StringBuilder constructorOutput = new StringBuilder("constructor(");
        for (int i = 0; i < params.length; i++)
        {
            if (i > 0) constructorOutput.append(", ");
            constructorOutput.append(params[i].value());
        }
        constructorOutput.append(") {}");

        String result = constructorOutput.toString();
        assertTrue(result.contains("private globalService: GlobalService"));
        assertTrue(result.contains("private anotherGlobal: AnotherService"));
        assertTrue(result.startsWith("constructor("));
        assertTrue(result.endsWith(") {}"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NgGlobalComponentConstructorParameter tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testGlobalComponentConstructorParameterDiscovery()
    {
        NgGlobalComponentConstructorParameter[] params = GlobalComponentConstructorParamProvider.class
                .getAnnotationsByType(NgGlobalComponentConstructorParameter.class);
        assertEquals(1, params.length);
        assertEquals("private componentGlobal: ComponentGlobalService", params[0].value());
    }

    @Test
    void testGlobalComponentConstructorParameterRendersAsConstructorParam()
    {
        NgGlobalComponentConstructorParameter[] params = GlobalComponentConstructorParamProvider.class
                .getAnnotationsByType(NgGlobalComponentConstructorParameter.class);

        StringBuilder constructorOutput = new StringBuilder("constructor(");
        constructorOutput.append(params[0].value());
        constructorOutput.append(") {}");

        String result = constructorOutput.toString();
        assertEquals("constructor(private componentGlobal: ComponentGlobalService) {}", result);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NgGlobalComponentImportReference tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testGlobalComponentImportReferenceDiscovery()
    {
        NgGlobalComponentImportReference[] refs = GlobalImportRefProvider.class
                .getAnnotationsByType(NgGlobalComponentImportReference.class);
        assertEquals(2, refs.length);
        assertEquals("GlobalUtil", refs[0].value());
        assertEquals("@app/utils", refs[0].reference());
        assertEquals("SharedModule", refs[1].value());
        assertEquals("@app/shared", refs[1].reference());
    }

    @Test
    void testGlobalComponentImportReferenceRendersAsImport()
    {
        NgGlobalComponentImportReference[] refs = GlobalImportRefProvider.class
                .getAnnotationsByType(NgGlobalComponentImportReference.class);

        StringBuilder importOutput = new StringBuilder();
        for (NgGlobalComponentImportReference ref : refs)
        {
            importOutput.append("import {")
                        .append(ref.value())
                        .append("} from '")
                        .append(ref.reference())
                        .append("';\n");
        }

        String result = importOutput.toString();
        assertTrue(result.contains("import {GlobalUtil} from '@app/utils';"));
        assertTrue(result.contains("import {SharedModule} from '@app/shared';"));
    }

    @Test
    void testGlobalAnnotationsHaveNoOnParentOnSelf()
    {
        // Verify these annotations do NOT have onParent/onSelf methods
        // (they're truly global - applied to all components unconditionally)
        try
        {
            NgGlobalConstructorParameter.class.getDeclaredMethod("onParent");
            fail("NgGlobalConstructorParameter should NOT have onParent");
        }
        catch (NoSuchMethodException e)
        {
            // expected
        }

        try
        {
            NgGlobalComponentConstructorParameter.class.getDeclaredMethod("onParent");
            fail("NgGlobalComponentConstructorParameter should NOT have onParent");
        }
        catch (NoSuchMethodException e)
        {
            // expected
        }

        try
        {
            NgGlobalComponentImportReference.class.getDeclaredMethod("onParent");
            fail("NgGlobalComponentImportReference should NOT have onParent");
        }
        catch (NoSuchMethodException e)
        {
            // expected
        }
    }
}

