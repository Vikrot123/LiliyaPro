package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame
import kotlin.test.assertNotSame
import kotlin.test.assertFalse
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.memory.composition.DefaultRuntimeMemoryComposition

class RuntimeMemoryCompositionLifecycleContractTest {

    @Test
    fun composition_should_expose_memory_lifecycle() {

        val composition =
            DefaultRuntimeMemoryComposition()

        assertNotNull(
            composition.lifecycle()
        )
    }

    @Test
    fun composition_should_keep_same_lifecycle_instance() {

        val composition =
            DefaultRuntimeMemoryComposition()

        val first =
            composition.lifecycle()

        val second =
            composition.lifecycle()

        assertSame(
            first,
            second
        )
    }

    @Test
    fun separate_compositions_should_not_share_lifecycle() {

        val first =
            DefaultRuntimeMemoryComposition()

        val second =
            DefaultRuntimeMemoryComposition()

        assertNotSame(
            first.lifecycle(),
            second.lifecycle()
        )
    }

    @Test
    fun lifecycle_should_control_memory_state() {

        val composition =
            DefaultRuntimeMemoryComposition()

        val lifecycle =
            composition.lifecycle()

        assertFalse(
            lifecycle.isStarted()
        )

        lifecycle.start()

        assertTrue(
            lifecycle.isStarted()
        )

        lifecycle.stop()

        assertFalse(
            lifecycle.isStarted()
        )

        lifecycle.reset()

        assertFalse(
            lifecycle.isStarted()
        )
    }
}
