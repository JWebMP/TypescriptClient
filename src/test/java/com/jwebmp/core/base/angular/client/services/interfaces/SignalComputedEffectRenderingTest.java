package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.references.NgImportReference;
import com.jwebmp.core.base.angular.client.annotations.structures.NgSignal;
import com.jwebmp.core.base.angular.client.annotations.structures.NgSignalComputed;
import com.jwebmp.core.base.angular.client.annotations.structures.NgSignalEffect;
import com.jwebmp.core.base.angular.client.services.ComponentConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that NgSignalComputed and NgSignalEffect render in the correct position
 * in the TypeScript class output, with proper imports and syntax.
 *
 * In Angular TypeScript:
 * - computed() and effect() MUST be class-level field initializers
 * - They render AFTER signals (since computeds/effects typically reference signals)
 * - computed() requires: import {computed} from '@angular/core'
 * - effect() requires: import {effect} from '@angular/core'
 * - Syntax: readonly name = computed(() => expression);
 * - Syntax: readonly name = effect(() => { statements });
 */
class SignalComputedEffectRenderingTest
{
    // ═══════════════════════════════════════════════════════════════════════════
    // Rendering format tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testComputedRendersAsReadonlyFieldWithComputed()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed(
                "fullName", "() => this.firstName() + ' ' + this.lastName()"));

        String result = config.renderSignalComputeds().toString();

        // Must be a readonly field assignment using computed()
        assertTrue(result.contains("readonly fullName = computed(() => this.firstName() + ' ' + this.lastName());"),
                "Computed must render as: readonly <name> = computed(<value>);\nGot: " + result);
    }

    @Test
    void testEffectRendersAsReadonlyFieldWithEffect()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalEffects().add(AnnotationUtils.getNgSignalEffect(
                "logEffect", "() => { console.log(this.count()); }"));

        String result = config.renderSignalEffects().toString();

        // Must be a readonly field assignment using effect()
        assertTrue(result.contains("readonly logEffect = effect(() => { console.log(this.count()); });"),
                "Effect must render as: readonly <name> = effect(<value>);\nGot: " + result);
    }

    @Test
    void testComputedIsIndentedWithTab()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed("x", "() => 1"));

        String result = config.renderSignalComputeds().toString();
        assertTrue(result.startsWith("\t"), "Computed field must be tab-indented. Got: " + result);
    }

    @Test
    void testEffectIsIndentedWithTab()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalEffects().add(AnnotationUtils.getNgSignalEffect("x", "() => {}"));

        String result = config.renderSignalEffects().toString();
        assertTrue(result.startsWith("\t"), "Effect field must be tab-indented. Got: " + result);
    }

    @Test
    void testComputedEndsWithSemicolonAndNewline()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed("x", "() => 1"));

        String result = config.renderSignalComputeds().toString();
        assertTrue(result.trim().endsWith(";"), "Computed must end with semicolon. Got: " + result);
        assertTrue(result.endsWith("\n"), "Computed must end with newline. Got: " + result);
    }

    @Test
    void testEffectEndsWithSemicolonAndNewline()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalEffects().add(AnnotationUtils.getNgSignalEffect("x", "() => {}"));

        String result = config.renderSignalEffects().toString();
        assertTrue(result.trim().endsWith(";"), "Effect must end with semicolon. Got: " + result);
        assertTrue(result.endsWith("\n"), "Effect must end with newline. Got: " + result);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Placement order tests - computeds/effects render AFTER signals
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testRenderOrderSignalThenComputedThenEffect()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignals().add(AnnotationUtils.getNgSignal("count", "0", "number"));
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed("doubled", "() => this.count() * 2"));
        config.getSignalEffects().add(AnnotationUtils.getNgSignalEffect("logger", "() => { console.log(this.count()); }"));

        // Simulate INgComponent.renderFields() order
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderSignals());
        sb.append(config.renderSignalComputeds());
        sb.append(config.renderSignalEffects());

        String result = sb.toString();

        int signalPos = result.indexOf("signal");
        int computedPos = result.indexOf("computed");
        int effectPos = result.indexOf("effect");

        assertTrue(signalPos < computedPos,
                "Signal must come before computed in output.\nGot: " + result);
        assertTrue(computedPos < effectPos,
                "Computed must come before effect in output.\nGot: " + result);
    }

    @Test
    void testFullFieldsRenderOrder()
    {
        // Simulates the complete INgComponent.renderFields() output
        ComponentConfiguration<?> config = new ComponentConfiguration<>();

        // Add inject
        config.getInjects().add(AnnotationUtils.getNgInject("SomeService", "svc"));
        // Add signal
        config.getSignals().add(AnnotationUtils.getNgSignal("count", "0", "number"));
        // Add computed
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed("doubled", "() => this.count() * 2"));
        // Add effect
        config.getSignalEffects().add(AnnotationUtils.getNgSignalEffect("logger", "() => { console.log(this.count()); }"));

        // Render in the same order as INgComponent.renderFields()
        StringBuilder sb = new StringBuilder();
        sb.append(config.renderInjects());
        sb.append(config.renderModels());
        sb.append(config.renderSignals());
        sb.append(config.renderSignalComputeds());
        sb.append(config.renderSignalEffects());
        sb.append(config.renderInputs());
        sb.append(config.renderOutputs());
        sb.append(config.renderFields());

        String result = sb.toString();

        int injectPos = result.indexOf("inject(");
        int signalPos = result.indexOf("= signal");
        int computedPos = result.indexOf("= computed");
        int effectPos = result.indexOf("= effect");

        assertTrue(injectPos >= 0, "Should contain inject. Got: " + result);
        assertTrue(signalPos >= 0, "Should contain signal. Got: " + result);
        assertTrue(computedPos >= 0, "Should contain computed. Got: " + result);
        assertTrue(effectPos >= 0, "Should contain effect. Got: " + result);

        assertTrue(injectPos < signalPos, "Inject must come before signal");
        assertTrue(signalPos < computedPos, "Signal must come before computed");
        assertTrue(computedPos < effectPos, "Computed must come before effect");
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Import requirements tests
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testComputedRequiresComputedImport()
    {
        // When signalComputeds are present, ConfigureImportReferences adds:
        // import {computed} from '@angular/core'
        // import {Signal} from '@angular/core'
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed("x", "() => 1"));
        config.getImportReferences().add(AnnotationUtils.getNgImportReference("computed", "@angular/core"));
        config.getImportReferences().add(AnnotationUtils.getNgImportReference("Signal", "@angular/core"));

        String imports = config.renderImportStatements().toString();
        assertTrue(imports.contains("computed"), "Must import 'computed'. Got: " + imports);
        assertTrue(imports.contains("@angular/core"), "Must import from '@angular/core'. Got: " + imports);
    }

    @Test
    void testEffectRequiresEffectImport()
    {
        // When signalEffects are present, ConfigureImportReferences adds:
        // import {effect} from '@angular/core'
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalEffects().add(AnnotationUtils.getNgSignalEffect("x", "() => {}"));
        config.getImportReferences().add(AnnotationUtils.getNgImportReference("effect", "@angular/core"));

        String imports = config.renderImportStatements().toString();
        assertTrue(imports.contains("effect"), "Must import 'effect'. Got: " + imports);
        assertTrue(imports.contains("@angular/core"), "Must import from '@angular/core'. Got: " + imports);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Edge cases
    // ═══════════════════════════════════════════════════════════════════════════

    @Test
    void testComputedWithComplexExpression()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed(
                "filteredItems",
                "() => this.items().filter(i => i.active && i.name.includes(this.search()))"));

        String result = config.renderSignalComputeds().toString();
        assertTrue(result.contains("readonly filteredItems = computed(() => this.items().filter(i => i.active && i.name.includes(this.search())));"),
                "Complex computed expression. Got: " + result);
    }

    @Test
    void testEffectWithMultilineBody()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalEffects().add(AnnotationUtils.getNgSignalEffect(
                "persistEffect",
                "() => { const val = this.state(); localStorage.setItem('state', JSON.stringify(val)); }"));

        String result = config.renderSignalEffects().toString();
        assertTrue(result.contains("readonly persistEffect = effect(() => { const val = this.state(); localStorage.setItem('state', JSON.stringify(val)); });"),
                "Complex effect body. Got: " + result);
    }

    @Test
    void testMultipleComputedsRenderInOrder()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed("first", "() => 1"));
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed("second", "() => 2"));
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed("third", "() => 3"));

        String result = config.renderSignalComputeds().toString();

        int firstPos = result.indexOf("first");
        int secondPos = result.indexOf("second");
        int thirdPos = result.indexOf("third");

        assertTrue(firstPos < secondPos && secondPos < thirdPos,
                "Multiple computeds must render in insertion order. Got: " + result);
    }

    @Test
    void testMultipleEffectsRenderInOrder()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalEffects().add(AnnotationUtils.getNgSignalEffect("eff1", "() => { a(); }"));
        config.getSignalEffects().add(AnnotationUtils.getNgSignalEffect("eff2", "() => { b(); }"));

        String result = config.renderSignalEffects().toString();

        int pos1 = result.indexOf("eff1");
        int pos2 = result.indexOf("eff2");

        assertTrue(pos1 < pos2, "Multiple effects must render in insertion order. Got: " + result);
    }

    @Test
    void testComputedAndEffectDoNotDuplicateWithSameReferenceName()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        // LinkedHashSet should deduplicate by equals/hashCode (based on referenceName)
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed("dup", "() => 1"));
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed("dup", "() => 1"));

        String result = config.renderSignalComputeds().toString();
        int firstOccurrence = result.indexOf("readonly dup");
        int secondOccurrence = result.indexOf("readonly dup", firstOccurrence + 1);

        assertEquals(-1, secondOccurrence,
                "Duplicate computeds with same referenceName should be deduplicated. Got: " + result);
    }
}

