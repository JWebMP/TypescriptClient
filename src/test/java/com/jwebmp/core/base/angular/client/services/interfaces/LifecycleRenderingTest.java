package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.services.ComponentConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that all 6 Angular lifecycle methods render correctly:
 * ngOnInit, ngOnDestroy, ngAfterViewInit, ngAfterViewChecked,
 * ngAfterContentInit, ngAfterContentChecked.
 *
 * In Angular TypeScript, lifecycle methods render as:
 *   ngOnInit() {
 *       body;
 *   }
 *
 * They render in a specific order inside renderMethods():
 * onInit -> afterViewInit -> afterContentInit -> afterViewChecked -> afterContentChecked -> methods -> onDestroy
 */
class LifecycleRenderingTest
{
    // ═══════════════════════════════════════════════════════════════════════════
    // ngOnInit
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testOnInitRendersMethodSignature()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getOnInit().add(AnnotationUtils.getNgOnInit("this.loadData();"));
        String result = config.renderOnInit().toString();

        assertTrue(result.contains("ngOnInit()"), "Must contain ngOnInit() signature. Got:\n" + result);
    }

    @Test
    void testOnInitRendersBody()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getOnInit().add(AnnotationUtils.getNgOnInit("this.loadData();"));
        String result = config.renderOnInit().toString();

        assertTrue(result.contains("this.loadData();"), "Must contain body. Got:\n" + result);
    }

    @Test
    void testOnInitRendersWithBraces()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getOnInit().add(AnnotationUtils.getNgOnInit("x();"));
        String result = config.renderOnInit().toString();

        assertTrue(result.contains("{"), "Must have opening brace");
        assertTrue(result.contains("}"), "Must have closing brace");
    }

    @Test
    void testOnInitMultipleBodies()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getOnInit().add(AnnotationUtils.getNgOnInit("this.initA();"));
        config.getOnInit().add(AnnotationUtils.getNgOnInit("this.initB();"));
        String result = config.renderOnInit().toString();

        assertTrue(result.contains("this.initA();"), "Must contain first body");
        assertTrue(result.contains("this.initB();"), "Must contain second body");
        // Should only have ONE ngOnInit method containing both bodies
        assertEquals(1, countOccurrences(result, "ngOnInit()"),
                "Multiple bodies should merge into one ngOnInit method");
    }

    @Test
    void testOnInitEmptyReturnsEmpty()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        assertEquals(0, config.renderOnInit().length());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ngOnDestroy
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testOnDestroyRendersMethodSignature()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getOnDestroy().add(AnnotationUtils.getNgOnDestroy("this.subscription.unsubscribe();"));
        String result = config.renderOnDestroy().toString();

        assertTrue(result.contains("ngOnDestroy()"), "Must contain ngOnDestroy() signature. Got:\n" + result);
    }

    @Test
    void testOnDestroyRendersBody()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getOnDestroy().add(AnnotationUtils.getNgOnDestroy("this.subscription.unsubscribe();"));
        String result = config.renderOnDestroy().toString();

        assertTrue(result.contains("this.subscription.unsubscribe();"), "Must contain body. Got:\n" + result);
    }

    @Test
    void testOnDestroyEmptyReturnsEmpty()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        assertEquals(0, config.renderOnDestroy().length());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ngAfterViewInit
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testAfterViewInitRendersMethodSignature()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getAfterViewInit().add(AnnotationUtils.getNgAfterViewInit("this.chart.render();"));
        String result = config.renderAfterViewInit().toString();

        assertTrue(result.contains("ngAfterViewInit()"), "Must contain ngAfterViewInit(). Got:\n" + result);
    }

    @Test
    void testAfterViewInitRendersBody()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getAfterViewInit().add(AnnotationUtils.getNgAfterViewInit("this.chart.render();"));
        String result = config.renderAfterViewInit().toString();

        assertTrue(result.contains("this.chart.render();"), "Must contain body");
    }

    @Test
    void testAfterViewInitEmptyReturnsEmpty()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        assertEquals(0, config.renderAfterViewInit().length());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ngAfterViewChecked
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testAfterViewCheckedRendersMethodSignature()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getAfterViewChecked().add(AnnotationUtils.getNgAfterViewChecked("this.cdr.detectChanges();"));
        String result = config.renderAfterViewChecked().toString();

        assertTrue(result.contains("ngAfterViewChecked()"), "Must contain ngAfterViewChecked(). Got:\n" + result);
    }

    @Test
    void testAfterViewCheckedRendersBody()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getAfterViewChecked().add(AnnotationUtils.getNgAfterViewChecked("this.cdr.detectChanges();"));
        String result = config.renderAfterViewChecked().toString();

        assertTrue(result.contains("this.cdr.detectChanges();"), "Must contain body");
    }

    @Test
    void testAfterViewCheckedEmptyReturnsEmpty()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        assertEquals(0, config.renderAfterViewChecked().length());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ngAfterContentInit
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testAfterContentInitRendersMethodSignature()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getAfterContentInit().add(AnnotationUtils.getNgAfterContentInit("this.contentReady = true;"));
        String result = config.renderAfterContentInit().toString();

        assertTrue(result.contains("ngAfterContentInit()"), "Must contain ngAfterContentInit(). Got:\n" + result);
    }

    @Test
    void testAfterContentInitRendersBody()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getAfterContentInit().add(AnnotationUtils.getNgAfterContentInit("this.contentReady = true;"));
        String result = config.renderAfterContentInit().toString();

        assertTrue(result.contains("this.contentReady = true;"), "Must contain body");
    }

    @Test
    void testAfterContentInitEmptyReturnsEmpty()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        assertEquals(0, config.renderAfterContentInit().length());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // ngAfterContentChecked
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testAfterContentCheckedRendersMethodSignature()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getAfterContentChecked().add(AnnotationUtils.getNgAfterContentChecked("this.validate();"));
        String result = config.renderAfterContentChecked().toString();

        assertTrue(result.contains("ngAfterContentChecked()"), "Must contain ngAfterContentChecked(). Got:\n" + result);
    }

    @Test
    void testAfterContentCheckedRendersBody()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getAfterContentChecked().add(AnnotationUtils.getNgAfterContentChecked("this.validate();"));
        String result = config.renderAfterContentChecked().toString();

        assertTrue(result.contains("this.validate();"), "Must contain body");
    }

    @Test
    void testAfterContentCheckedEmptyReturnsEmpty()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        assertEquals(0, config.renderAfterContentChecked().length());
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Rendering order (matching INgComponent.renderMethods())
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testLifecycleMethodRenderOrder()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getOnInit().add(AnnotationUtils.getNgOnInit("init();"));
        config.getAfterViewInit().add(AnnotationUtils.getNgAfterViewInit("afterViewInit();"));
        config.getAfterContentInit().add(AnnotationUtils.getNgAfterContentInit("afterContentInit();"));
        config.getAfterViewChecked().add(AnnotationUtils.getNgAfterViewChecked("afterViewChecked();"));
        config.getAfterContentChecked().add(AnnotationUtils.getNgAfterContentChecked("afterContentChecked();"));
        config.getOnDestroy().add(AnnotationUtils.getNgOnDestroy("destroy();"));

        // Replicate INgComponent.renderMethods() order
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderOnInit());
        sb.append(config.renderAfterViewInit());
        sb.append(config.renderAfterContentInit());
        sb.append(config.renderAfterViewChecked());
        sb.append(config.renderAfterContentChecked());
        sb.append(config.renderMethods());
        sb.append(config.renderOnDestroy());

        String result = sb.toString();

        int onInitPos = result.indexOf("ngOnInit()");
        int afterViewInitPos = result.indexOf("ngAfterViewInit()");
        int afterContentInitPos = result.indexOf("ngAfterContentInit()");
        int afterViewCheckedPos = result.indexOf("ngAfterViewChecked()");
        int afterContentCheckedPos = result.indexOf("ngAfterContentChecked()");
        int onDestroyPos = result.indexOf("ngOnDestroy()");

        assertTrue(onInitPos >= 0, "ngOnInit must be present");
        assertTrue(afterViewInitPos >= 0, "ngAfterViewInit must be present");
        assertTrue(afterContentInitPos >= 0, "ngAfterContentInit must be present");
        assertTrue(afterViewCheckedPos >= 0, "ngAfterViewChecked must be present");
        assertTrue(afterContentCheckedPos >= 0, "ngAfterContentChecked must be present");
        assertTrue(onDestroyPos >= 0, "ngOnDestroy must be present");

        // ngOnInit first, ngOnDestroy last
        assertTrue(onInitPos < afterViewInitPos, "ngOnInit should come before ngAfterViewInit");
        assertTrue(afterViewInitPos < afterContentInitPos, "ngAfterViewInit should come before ngAfterContentInit");
        assertTrue(afterContentInitPos < afterViewCheckedPos, "ngAfterContentInit should come before ngAfterViewChecked");
        assertTrue(afterViewCheckedPos < afterContentCheckedPos, "ngAfterViewChecked should come before ngAfterContentChecked");
        assertTrue(afterContentCheckedPos < onDestroyPos, "ngAfterContentChecked should come before ngOnDestroy");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Indentation and structure
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testLifecycleMethodIndentation()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getOnInit().add(AnnotationUtils.getNgOnInit("this.start();"));
        String result = config.renderOnInit().toString();

        // Method signature indented with 1 tab
        assertTrue(result.contains("\tngOnInit()"), "Method signature should be tab-indented");
        // Body indented with 2 tabs
        assertTrue(result.contains("\t\tthis.start();"), "Body should be double-tab-indented");
        // Closing brace indented with 1 tab
        assertTrue(result.contains("\t}"), "Closing brace should be tab-indented");
    }

    @Test
    void testLifecycleMethodStructure()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getOnInit().add(AnnotationUtils.getNgOnInit("doSomething();"));
        String result = config.renderOnInit().toString();

        // Should follow: \tmethod()\n\t{\n\t\tbody\n\t}\n
        String[] lines = result.split("\n");
        assertEquals("\tngOnInit()", lines[0], "First line: method signature");
        assertEquals("\t{", lines[1], "Second line: opening brace");
        assertEquals("\t\tdoSomething();", lines[2], "Third line: body");
        assertEquals("\t}", lines[3], "Fourth line: closing brace");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Body trimming
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testLifecycleBodyIsTrimmed()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getOnInit().add(AnnotationUtils.getNgOnInit("  this.start();  "));
        String result = config.renderOnInit().toString();

        assertTrue(result.contains("\t\tthis.start();"), "Body should be trimmed. Got:\n" + result);
        assertFalse(result.contains("\t\t  this.start();"), "Leading spaces should be trimmed");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Utility
    // ═══════════════════════════════════════════════════════════════════════════

    private int countOccurrences(String text, String substring)
    {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(substring, idx)) != -1)
        {
            count++;
            idx += substring.length();
        }
        return count;
    }
}

