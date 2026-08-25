package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionCognitiveContextLifecycleIntegrationContractTest {

    @Test
    fun runtime_start_should_start_cognitive_context_lifecycle() {
        val composition = DefaultRuntimeComposition()

        composition.prepareRuntime()

        assertFalse(
            composition.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )

        composition.startRuntime()

        assertTrue(
            composition.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )

        composition.stopRuntime()

        assertFalse(
            composition.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )
    }

    @Test
    fun prepare_runtime_should_reset_cognitive_context_lifecycle() {
        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        assertTrue(
            composition.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )

        composition.prepareRuntime()

        assertFalse(
            composition.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )
    }

    @Test
    fun repeated_runtime_start_stop_should_preserve_cognitive_lifecycle_isolation() {
        val composition = DefaultRuntimeComposition()

        composition.startRuntime()

        assertTrue(
            composition.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )

        composition.stopRuntime()

        assertFalse(
            composition.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )

        composition.prepareRuntime()
        composition.startRuntime()

        assertTrue(
            composition.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )

        composition.stopRuntime()

        assertFalse(
            composition.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )
    }

    @Test
    fun separate_runtime_compositions_should_not_share_cognitive_lifecycle() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        first.startRuntime()

        assertTrue(
            first.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )

        assertFalse(
            second.cognitiveContextComposition()
                .lifecycle()
                .isStarted()
        )

        first.stopRuntime()
    }
}
