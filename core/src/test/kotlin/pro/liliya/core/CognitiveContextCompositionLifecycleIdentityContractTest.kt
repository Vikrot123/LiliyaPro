package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextCompositionLifecycleIdentityContractTest {

    @Test
    fun composition_should_expose_same_lifecycle_instance() {
        val composition = DefaultCognitiveContextComposition()

        assertSame(
            composition.lifecycle(),
            composition.lifecycle()
        )
    }

    @Test
    fun lifecycle_state_should_be_visible_through_composition() {
        val composition = DefaultCognitiveContextComposition()

        val lifecycle = composition.lifecycle()

        assertFalse(lifecycle.isStarted())

        lifecycle.start()

        assertTrue(composition.lifecycle().isStarted())

        lifecycle.stop()

        assertFalse(composition.lifecycle().isStarted())
    }

    @Test
    fun lifecycle_should_remain_reusable_through_composition() {
        val composition = DefaultCognitiveContextComposition()

        composition.lifecycle().start()
        assertTrue(composition.lifecycle().isStarted())

        composition.lifecycle().reset()
        assertFalse(composition.lifecycle().isStarted())

        composition.lifecycle().start()
        assertTrue(composition.lifecycle().isStarted())
    }

    @Test
    fun lifecycle_state_should_not_be_shared_between_compositions() {
        val first = DefaultCognitiveContextComposition()
        val second = DefaultCognitiveContextComposition()

        first.lifecycle().start()

        assertTrue(first.lifecycle().isStarted())
        assertFalse(second.lifecycle().isStarted())

        second.lifecycle().start()
        first.lifecycle().stop()

        assertFalse(first.lifecycle().isStarted())
        assertTrue(second.lifecycle().isStarted())
    }
}
