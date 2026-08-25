package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertNotSame
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.composition.DefaultCognitiveContextComposition

class CognitiveContextCompositionContractTest {

    @Test
    fun composition_should_expose_context() {
        val composition = DefaultCognitiveContextComposition()

        assertNotNull(composition.context())
    }

    @Test
    fun composition_should_keep_same_context_instance() {
        val composition = DefaultCognitiveContextComposition()

        assertSame(
            composition.context(),
            composition.context()
        )
    }

    @Test
    fun composition_should_expose_lifecycle() {
        val composition = DefaultCognitiveContextComposition()

        assertNotNull(composition.lifecycle())
    }

    @Test
    fun composition_should_keep_same_lifecycle_instance() {
        val composition = DefaultCognitiveContextComposition()

        assertSame(
            composition.lifecycle(),
            composition.lifecycle()
        )
    }

    @Test
    fun separate_compositions_should_not_share_context() {
        val first = DefaultCognitiveContextComposition()
        val second = DefaultCognitiveContextComposition()

        assertNotSame(
            first.context(),
            second.context()
        )
    }

    @Test
    fun separate_compositions_should_not_share_lifecycle() {
        val first = DefaultCognitiveContextComposition()
        val second = DefaultCognitiveContextComposition()

        assertNotSame(
            first.lifecycle(),
            second.lifecycle()
        )
    }

    @Test
    fun lifecycle_should_control_context_state() {
        val composition = DefaultCognitiveContextComposition()
        val lifecycle = composition.lifecycle()

        assertFalse(lifecycle.isStarted())

        lifecycle.start()

        assertTrue(lifecycle.isStarted())

        lifecycle.stop()

        assertFalse(lifecycle.isStarted())

        lifecycle.reset()

        assertFalse(lifecycle.isStarted())
    }

    @Test
    fun repeated_lifecycle_operations_should_be_safe() {
        val composition = DefaultCognitiveContextComposition()
        val lifecycle = composition.lifecycle()

        lifecycle.start()
        lifecycle.start()

        assertTrue(lifecycle.isStarted())

        lifecycle.stop()
        lifecycle.stop()

        assertFalse(lifecycle.isStarted())
    }
}
