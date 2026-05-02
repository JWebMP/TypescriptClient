package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.boot.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that NgBoot* annotations render correctly into the boot TypeScript output
 * (app.config.ts, boot component constructor, global fields, etc.)
 */
class BootAnnotationRenderingTest
{
    // ═══════════════════════════════════════════════════════════════════════════
    // Test fixture classes with Boot annotations
    // ═══════════════════════════════════════════════════════════════════════════

    @NgBootImportReference(value = "ModuleRegistry", reference = "@ag-grid-community/core")
    @NgBootImportReference(value = "AllCommunityModule", reference = "@ag-grid-community/all-modules")
    @NgBootImportProvider(value = "provideAnimations()")
    @NgBootConstructorBody(value = "ModuleRegistry.registerModules([AllCommunityModule]);")
    @NgBootConstructorParameter(value = "private router: Router")
    @NgBootGlobalField(value = "gridReady: boolean = false;")
    static class BootAnnotatedClass
    {
    }

    @NgBootImportReference(value = "SideEffectLib", reference = "side-effect-lib", sideEffect = true)
    @NgBootImportReference(value = "DefaultExport", reference = "default-lib", direct = true)
    @NgBootImportReference(value = "PlainImport", reference = "plain-lib", wrapValueInBraces = false)
    @NgBootImportReference(value = "GlobalClipboard", reference = "clipboard", assignToGlobal = true)
    static class BootSideEffectClass
    {
    }

    @NgBootImportReference(value = "ParentBootRef", reference = "@parent/lib", onParent = true, onSelf = false)
    @NgBootConstructorBody(value = "console.log('parent boot body');", onParent = true, onSelf = false)
    @NgBootConstructorParameter(value = "private parentService: ParentService", onParent = true, onSelf = false)
    @NgBootGlobalField(value = "parentGlobal: string = 'propagated';", onParent = true, onSelf = false)
    static class BootOnParentClass
    {
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NgBootImportReference rendering tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testBootImportReferenceRendering()
    {
        NgBootImportReference[] refs = BootAnnotatedClass.class.getAnnotationsByType(NgBootImportReference.class);
        StringBuilder sb = renderBootImportReferences(refs);
        String result = sb.toString();

        assertTrue(result.contains("import {ModuleRegistry} from '@ag-grid-community/core'"));
        assertTrue(result.contains("import {AllCommunityModule} from '@ag-grid-community/all-modules'"));
    }

    @Test
    void testBootImportReferenceSideEffect()
    {
        NgBootImportReference[] refs = BootSideEffectClass.class.getAnnotationsByType(NgBootImportReference.class);
        StringBuilder sb = renderBootImportReferences(refs);
        String result = sb.toString();

        assertTrue(result.contains("import 'side-effect-lib'"), "Side effect import. Got:\n" + result);
    }

    @Test
    void testBootImportReferenceDirect()
    {
        NgBootImportReference[] refs = BootSideEffectClass.class.getAnnotationsByType(NgBootImportReference.class);
        StringBuilder sb = renderBootImportReferences(refs);
        String result = sb.toString();

        assertTrue(result.contains("import DefaultExport from 'default-lib'"), "Direct import. Got:\n" + result);
    }

    @Test
    void testBootImportReferenceNoBraces()
    {
        NgBootImportReference[] refs = BootSideEffectClass.class.getAnnotationsByType(NgBootImportReference.class);
        StringBuilder sb = renderBootImportReferences(refs);
        String result = sb.toString();

        assertTrue(result.contains("import PlainImport from 'plain-lib'"), "No braces import. Got:\n" + result);
    }

    @Test
    void testBootImportReferenceAssignToGlobal()
    {
        NgBootImportReference[] refs = BootSideEffectClass.class.getAnnotationsByType(NgBootImportReference.class);
        StringBuilder sb = renderBootImportReferences(refs);
        String result = sb.toString();

        assertTrue(result.contains("(globalThis as any).GlobalClipboard = GlobalClipboard;"), "Global assignment. Got:\n" + result);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NgBootImportProvider rendering tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testBootImportProviderRendering()
    {
        NgBootImportProvider[] providers = BootAnnotatedClass.class.getAnnotationsByType(NgBootImportProvider.class);
        StringBuilder sb = renderBootImportProviders(providers);
        String result = sb.toString();

        assertTrue(result.contains("provideAnimations(),"), "Provider rendering. Got:\n" + result);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NgBootConstructorBody rendering tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testBootConstructorBodyRendering()
    {
        NgBootConstructorBody[] bodies = BootAnnotatedClass.class.getAnnotationsByType(NgBootConstructorBody.class);
        StringBuilder sb = renderBootConstructorBodies(bodies);
        String result = sb.toString();

        assertTrue(result.contains("ModuleRegistry.registerModules([AllCommunityModule]);"), "Body rendering. Got:\n" + result);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NgBootConstructorParameter rendering tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testBootConstructorParameterRendering()
    {
        NgBootConstructorParameter[] params = BootAnnotatedClass.class.getAnnotationsByType(NgBootConstructorParameter.class);
        StringBuilder sb = renderBootConstructorParameters(params);
        String result = sb.toString();

        assertTrue(result.contains("private router: Router"), "Param rendering. Got:\n" + result);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // NgBootGlobalField rendering tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testBootGlobalFieldRendering()
    {
        NgBootGlobalField[] fields = BootAnnotatedClass.class.getAnnotationsByType(NgBootGlobalField.class);
        StringBuilder sb = renderBootGlobalFields(fields);
        String result = sb.toString();

        assertTrue(result.contains("gridReady: boolean = false;"), "Global field rendering. Got:\n" + result);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // onParent/onSelf filtering tests for Boot annotations
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testBootImportReferenceOnParentFiltering()
    {
        NgBootImportReference[] refs = BootOnParentClass.class.getAnnotationsByType(NgBootImportReference.class);

        // onSelf=false means it should NOT render on self
        List<NgBootImportReference> selfRefs = filterSelf(refs);
        assertTrue(selfRefs.isEmpty(), "onSelf=false should exclude from self");

        // onParent=true means it SHOULD propagate to parent
        List<NgBootImportReference> parentRefs = filterParent(refs);
        assertEquals(1, parentRefs.size());
        assertEquals("ParentBootRef", parentRefs.get(0).value());
    }

    @Test
    void testBootConstructorBodyOnParentFiltering()
    {
        NgBootConstructorBody[] bodies = BootOnParentClass.class.getAnnotationsByType(NgBootConstructorBody.class);

        List<NgBootConstructorBody> selfBodies = new ArrayList<>();
        List<NgBootConstructorBody> parentBodies = new ArrayList<>();
        for (NgBootConstructorBody b : bodies)
        {
            if (b.onSelf()) selfBodies.add(b);
            if (b.onParent()) parentBodies.add(b);
        }

        assertTrue(selfBodies.isEmpty());
        assertEquals(1, parentBodies.size());
        assertTrue(parentBodies.get(0).value().contains("parent boot body"));
    }

    @Test
    void testBootConstructorParameterOnParentFiltering()
    {
        NgBootConstructorParameter[] params = BootOnParentClass.class.getAnnotationsByType(NgBootConstructorParameter.class);

        List<NgBootConstructorParameter> selfParams = new ArrayList<>();
        List<NgBootConstructorParameter> parentParams = new ArrayList<>();
        for (NgBootConstructorParameter p : params)
        {
            if (p.onSelf()) selfParams.add(p);
            if (p.onParent()) parentParams.add(p);
        }

        assertTrue(selfParams.isEmpty());
        assertEquals(1, parentParams.size());
        assertTrue(parentParams.get(0).value().contains("parentService"));
    }

    @Test
    void testBootGlobalFieldOnParentFiltering()
    {
        NgBootGlobalField[] fields = BootOnParentClass.class.getAnnotationsByType(NgBootGlobalField.class);

        List<NgBootGlobalField> selfFields = new ArrayList<>();
        List<NgBootGlobalField> parentFields = new ArrayList<>();
        for (NgBootGlobalField f : fields)
        {
            if (f.onSelf()) selfFields.add(f);
            if (f.onParent()) parentFields.add(f);
        }

        assertTrue(selfFields.isEmpty());
        assertEquals(1, parentFields.size());
        assertTrue(parentFields.get(0).value().contains("parentGlobal"));
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Rendering helpers (mimic processAppConfigFile logic from AngularAppSetup)
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Mimics the import rendering logic from AngularAppSetup.processAppConfigFile
     */
    private StringBuilder renderBootImportReferences(NgBootImportReference[] refs)
    {
        Set<String> imports = new LinkedHashSet<>();
        List<String> globalAssignments = new ArrayList<>();

        for (NgBootImportReference ref : refs)
        {
            String importString;
            if (ref.sideEffect())
            {
                importString = "import '" + ref.reference() + "'";
            }
            else if (ref.direct() || !ref.wrapValueInBraces())
            {
                importString = "import " + ref.value() + " from '" + ref.reference() + "'";
            }
            else
            {
                importString = "import {" + ref.value() + "} from '" + ref.reference() + "'";
            }
            imports.add(importString);
            if (ref.assignToGlobal() && !ref.sideEffect())
            {
                globalAssignments.add("(globalThis as any)." + ref.value() + " = " + ref.value() + ";");
            }
        }

        StringBuilder sb = new StringBuilder();
        imports.forEach(a -> sb.append(a).append("\n"));
        globalAssignments.forEach(a -> sb.append(a).append("\n"));
        return sb;
    }

    private StringBuilder renderBootImportProviders(NgBootImportProvider[] providers)
    {
        StringBuilder sb = new StringBuilder();
        for (NgBootImportProvider provider : providers)
        {
            sb.append(provider.value()).append(",\n");
        }
        return sb;
    }

    private StringBuilder renderBootConstructorBodies(NgBootConstructorBody[] bodies)
    {
        StringBuilder sb = new StringBuilder();
        for (NgBootConstructorBody body : bodies)
        {
            if (body.onSelf())
            {
                sb.append("\t\t").append(body.value()).append("\n");
            }
        }
        return sb;
    }

    private StringBuilder renderBootConstructorParameters(NgBootConstructorParameter[] params)
    {
        StringBuilder sb = new StringBuilder();
        for (NgBootConstructorParameter param : params)
        {
            if (param.onSelf())
            {
                sb.append(param.value()).append(", ");
            }
        }
        return sb;
    }

    private StringBuilder renderBootGlobalFields(NgBootGlobalField[] fields)
    {
        StringBuilder sb = new StringBuilder();
        for (NgBootGlobalField field : fields)
        {
            if (field.onSelf())
            {
                sb.append("\t").append(field.value()).append("\n");
            }
        }
        return sb;
    }

    private List<NgBootImportReference> filterSelf(NgBootImportReference[] refs)
    {
        List<NgBootImportReference> out = new ArrayList<>();
        for (NgBootImportReference r : refs)
        {
            if (r.onSelf()) out.add(r);
        }
        return out;
    }

    private List<NgBootImportReference> filterParent(NgBootImportReference[] refs)
    {
        List<NgBootImportReference> out = new ArrayList<>();
        for (NgBootImportReference r : refs)
        {
            if (r.onParent()) out.add(r);
        }
        return out;
    }
}

