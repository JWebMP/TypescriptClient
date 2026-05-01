package com.jwebmp.core.base.angular.client.services.interfaces;

import com.jwebmp.core.base.angular.client.annotations.references.NgComponentReference;
import com.jwebmp.core.base.angular.client.annotations.structures.*;
import com.jwebmp.core.base.angular.client.services.ComponentConfiguration;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OnParentPropagationTest
{

    @NgSignal(value = "'initialValue'", referenceName = "parentSignal", type = "string", onParent = true, onSelf = false)
    @NgSignalComputed(value = "() => this.parentSignal().toUpperCase()", referenceName = "parentComputed", onParent = true, onSelf = false)
    @NgSignalEffect(value = "() => { console.log(this.parentSignal()); }", referenceName = "parentEffect", onParent = true, onSelf = false)
    static class ChildProvider
    {
    }

    @NgSignal(value = "'selfValue'", referenceName = "selfSignal", type = "string")
    @NgSignalComputed(value = "() => this.selfSignal().length", referenceName = "selfComputed")
    @NgSignalEffect(value = "() => { console.log(this.selfSignal()); }", referenceName = "selfEffect")
    @NgComponentReference(ChildProvider.class)
    static class ParentComponent
    {
    }

    @NgSignal(value = "'both'", referenceName = "bothSignal", type = "string", onParent = true, onSelf = true)
    @NgSignalComputed(value = "() => this.bothSignal()", referenceName = "bothComputed", onParent = true, onSelf = true)
    @NgSignalEffect(value = "() => { console.log('both'); }", referenceName = "bothEffect", onParent = true, onSelf = true)
    static class BothComponent
    {
    }

    @Test
    void testNgSignalOnSelfDefaultsTrue()
    {
        NgSignal[] signals = ParentComponent.class.getAnnotationsByType(NgSignal.class);
        assertEquals(1, signals.length);
        assertTrue(signals[0].onSelf());
        assertFalse(signals[0].onParent());
    }

    @Test
    void testNgSignalOnParentOnly()
    {
        NgSignal[] signals = ChildProvider.class.getAnnotationsByType(NgSignal.class);
        assertEquals(1, signals.length);
        assertTrue(signals[0].onParent());
        assertFalse(signals[0].onSelf());
    }

    @Test
    void testNgSignalComputedOnSelfDefaultsTrue()
    {
        NgSignalComputed[] annotations = ParentComponent.class.getAnnotationsByType(NgSignalComputed.class);
        assertEquals(1, annotations.length);
        assertTrue(annotations[0].onSelf());
        assertFalse(annotations[0].onParent());
    }

    @Test
    void testNgSignalComputedOnParentOnly()
    {
        NgSignalComputed[] annotations = ChildProvider.class.getAnnotationsByType(NgSignalComputed.class);
        assertEquals(1, annotations.length);
        assertTrue(annotations[0].onParent());
        assertFalse(annotations[0].onSelf());
    }

    @Test
    void testNgSignalEffectOnSelfDefaultsTrue()
    {
        NgSignalEffect[] annotations = ParentComponent.class.getAnnotationsByType(NgSignalEffect.class);
        assertEquals(1, annotations.length);
        assertTrue(annotations[0].onSelf());
        assertFalse(annotations[0].onParent());
    }

    @Test
    void testNgSignalEffectOnParentOnly()
    {
        NgSignalEffect[] annotations = ChildProvider.class.getAnnotationsByType(NgSignalEffect.class);
        assertEquals(1, annotations.length);
        assertTrue(annotations[0].onParent());
        assertFalse(annotations[0].onSelf());
    }

    @Test
    void testBothOnParentAndOnSelf()
    {
        NgSignal[] signals = BothComponent.class.getAnnotationsByType(NgSignal.class);
        NgSignalComputed[] computeds = BothComponent.class.getAnnotationsByType(NgSignalComputed.class);
        NgSignalEffect[] effects = BothComponent.class.getAnnotationsByType(NgSignalEffect.class);

        assertTrue(signals[0].onParent());
        assertTrue(signals[0].onSelf());
        assertTrue(computeds[0].onParent());
        assertTrue(computeds[0].onSelf());
        assertTrue(effects[0].onParent());
        assertTrue(effects[0].onSelf());
    }

    @Test
    void testParentPropagationFiltering()
    {
        NgSignalComputed[] childAnnotations = ChildProvider.class.getAnnotationsByType(NgSignalComputed.class);
        NgSignalComputed[] parentAnnotations = ParentComponent.class.getAnnotationsByType(NgSignalComputed.class);

        List<NgSignalComputed> selfComputeds = new ArrayList<>();
        for (NgSignalComputed a : parentAnnotations)
        {
            if (a.onSelf())
            {
                selfComputeds.add(a);
            }
        }
        assertEquals(1, selfComputeds.size());
        assertEquals("selfComputed", selfComputeds.get(0).referenceName());

        List<NgSignalComputed> parentComputeds = new ArrayList<>();
        for (NgSignalComputed a : childAnnotations)
        {
            if (a.onParent())
            {
                parentComputeds.add(a);
            }
        }
        assertEquals(1, parentComputeds.size());
        assertEquals("parentComputed", parentComputeds.get(0).referenceName());
    }

    @Test
    void testParentPropagationFilteringForEffects()
    {
        NgSignalEffect[] childAnnotations = ChildProvider.class.getAnnotationsByType(NgSignalEffect.class);
        NgSignalEffect[] parentAnnotations = ParentComponent.class.getAnnotationsByType(NgSignalEffect.class);

        List<NgSignalEffect> selfEffects = new ArrayList<>();
        for (NgSignalEffect a : parentAnnotations)
        {
            if (a.onSelf())
            {
                selfEffects.add(a);
            }
        }
        assertEquals(1, selfEffects.size());
        assertEquals("selfEffect", selfEffects.get(0).referenceName());

        List<NgSignalEffect> parentEffects = new ArrayList<>();
        for (NgSignalEffect a : childAnnotations)
        {
            if (a.onParent())
            {
                parentEffects.add(a);
            }
        }
        assertEquals(1, parentEffects.size());
        assertEquals("parentEffect", parentEffects.get(0).referenceName());
    }

    @Test
    void testMyNgSignalComputedFactory()
    {
        var computed = AnnotationUtils.getNgSignalComputed("myRef", "() => 42");
        assertEquals("myRef", computed.referenceName());
        assertEquals("() => 42", computed.value());
        assertTrue(computed.onSelf());
        assertFalse(computed.onParent());
        assertEquals(NgSignalComputed.class, computed.annotationType());
    }

    @Test
    void testMyNgSignalEffectFactory()
    {
        var effect = AnnotationUtils.getNgSignalEffect("myEffect", "() => { }");
        assertEquals("myEffect", effect.referenceName());
        assertEquals("() => { }", effect.value());
        assertTrue(effect.onSelf());
        assertFalse(effect.onParent());
        assertEquals(NgSignalEffect.class, effect.annotationType());
    }

    @Test
    void testMyNgSignalComputedEquality()
    {
        var a = AnnotationUtils.getNgSignalComputed("ref1", "() => 1");
        var b = AnnotationUtils.getNgSignalComputed("ref1", "() => 1");
        var c = AnnotationUtils.getNgSignalComputed("ref2", "() => 1");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    void testMyNgSignalEffectEquality()
    {
        var a = AnnotationUtils.getNgSignalEffect("eff1", "() => {}");
        var b = AnnotationUtils.getNgSignalEffect("eff1", "() => {}");
        var c = AnnotationUtils.getNgSignalEffect("eff2", "() => {}");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    void testRenderSignals()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignals().add(AnnotationUtils.getNgSignal("mySignal", "'hello'", "string"));
        StringBuilder output = config.renderSignals();
        assertTrue(output.toString().contains("readonly mySignal = signal<string>('hello');"));
    }

    @Test
    void testRenderSignalComputeds()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed("upperName", "() => this.name().toUpperCase()"));
        StringBuilder output = config.renderSignalComputeds();
        String result = output.toString();
        assertTrue(result.contains("readonly upperName = computed(() => this.name().toUpperCase());"), "Got: " + result);
    }

    @Test
    void testRenderSignalEffects()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalEffects().add(AnnotationUtils.getNgSignalEffect("logEffect", "() => { console.log('hi'); }"));
        StringBuilder output = config.renderSignalEffects();
        String result = output.toString();
        assertTrue(result.contains("readonly logEffect = effect(() => { console.log('hi'); });"), "Got: " + result);
    }

    @Test
    void testRenderSignalComputedsEmpty()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        assertEquals(0, config.renderSignalComputeds().length());
    }

    @Test
    void testRenderSignalEffectsEmpty()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        assertEquals(0, config.renderSignalEffects().length());
    }

    @Test
    void testRenderMultipleSignalComputeds()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed("comp1", "() => 1"));
        config.getSignalComputeds().add(AnnotationUtils.getNgSignalComputed("comp2", "() => 2"));
        String result = config.renderSignalComputeds().toString();
        assertTrue(result.contains("readonly comp1 = computed(() => 1);"));
        assertTrue(result.contains("readonly comp2 = computed(() => 2);"));
    }

    @Test
    void testRenderMultipleSignalEffects()
    {
        ComponentConfiguration<?> config = new ComponentConfiguration<>();
        config.getSignalEffects().add(AnnotationUtils.getNgSignalEffect("eff1", "() => {}"));
        config.getSignalEffects().add(AnnotationUtils.getNgSignalEffect("eff2", "() => { x(); }"));
        String result = config.renderSignalEffects().toString();
        assertTrue(result.contains("readonly eff1 = effect(() => {});"));
        assertTrue(result.contains("readonly eff2 = effect(() => { x(); });"));
    }
}



