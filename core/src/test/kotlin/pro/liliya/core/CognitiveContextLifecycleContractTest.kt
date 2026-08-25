package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.lifecycle.DefaultCognitiveContextLifecycle

class CognitiveContextLifecycleContractTest {

    @Test
    fun new_lifecycle_should_be_stopped() {
        val lifecycle = DefaultCognitiveContextLifecycle()

        assertFalse(lifecycle.isStarted())
    }

    @Test
    fun start_should_make_lifecycle_started() {
        val lifecycle = DefaultCognitiveContextLifecycle()

        lifecycle.start()

        assertTrue(lifecycle.isStarted())
    }

    @Test
    fun stop_should_make_lifecycle_stopped() {
        val lifecycle = DefaultCognitiveContextLifecycle()

        lifecycle.start()
        lifecycle.stop()

        assertFalse(lifecycle.isStarted())
    }

    @Test
    fun repeated_start_should_remain_started() {
        val lifecycle = DefaultCognitiveContextLifecycle()

        lifecycle.start()
        lifecycle.start()

        assertTrue(lifecycle.isStarted())
    }

    @Test
    fun repeated_stop_should_remain_stopped() {
        val lifecycle = DefaultCognitiveContextLifecycle()

        lifecycle.start()
        lifecycle.stop()
        lifecycle.stop()

        assertFalse(lifecycle.isStarted())
    }

    @Test
    fun reset_should_make_started_lifecycle_stopped() {
        val lifecycle = DefaultCognitiveContextLifecycle()

        lifecycle.start()
        lifecycle.reset()

        assertFalse(lifecycle.isStarted())
    }

    @Test
    fun reset_should_be_idempotent() {
        val lifecycle = DefaultCognitiveContextLifecycle()

        lifecycle.reset()
        lifecycle.reset()

        assertFalse(lifecycle.isStarted())
    }

    @Test
    fun lifecycle_should_be_reusable_after_reset() {
        val lifecycle = DefaultCognitiveContextLifecycle()

        lifecycle.start()
        lifecycle.reset()
        lifecycle.start()

        assertTrue(lifecycle.isStarted())
    }
}
