package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionCognitiveContextRuntimeTimestampPropagationContractTest {

    @Test
    fun runtime_timestamp_should_propagate_into_cognitive_context() {
        val composition = DefaultRuntimeComposition()

        val result = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val snapshot =
            result.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(snapshot)

        val timestamp =
            snapshot.values["runtimeTimestamp"]

        assertNotNull(timestamp)
        assertTrue(timestamp is Long)
        assertTrue(timestamp > 0L)
    }

    @Test
    fun following_process_should_observe_current_runtime_timestamp() {
        val composition = DefaultRuntimeComposition()

        val first = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val second = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val firstSnapshot =
            first.values["source_0"] as? CognitiveContextSnapshot

        val secondSnapshot =
            second.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(firstSnapshot)
        assertNotNull(secondSnapshot)

        val firstTimestamp =
            firstSnapshot.values["runtimeTimestamp"] as? Long

        val secondTimestamp =
            secondSnapshot.values["runtimeTimestamp"] as? Long

        assertNotNull(firstTimestamp)
        assertNotNull(secondTimestamp)

        assertTrue(secondTimestamp >= firstTimestamp)
    }

    @Test
    fun separate_compositions_should_keep_runtime_timestamps_isolated() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        val firstResult = first
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val secondResult = second
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val firstSnapshot =
            firstResult.values["source_0"] as? CognitiveContextSnapshot

        val secondSnapshot =
            secondResult.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(firstSnapshot)
        assertNotNull(secondSnapshot)

        val firstTimestamp =
            firstSnapshot.values["runtimeTimestamp"] as? Long

        val secondTimestamp =
            secondSnapshot.values["runtimeTimestamp"] as? Long

        assertNotNull(firstTimestamp)
        assertNotNull(secondTimestamp)

        assertTrue(firstTimestamp > 0L)
        assertTrue(secondTimestamp > 0L)
    }

    @Test
    fun runtime_timestamp_should_be_captured_in_each_snapshot() {
        val composition = DefaultRuntimeComposition()

        val first = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        Thread.sleep(2)

        val second = composition
            .cognitiveContextComposition()
            .service()
            .process(CognitiveContextType.TASK)

        val firstSnapshot =
            first.values["source_0"] as? CognitiveContextSnapshot

        val secondSnapshot =
            second.values["source_0"] as? CognitiveContextSnapshot

        assertNotNull(firstSnapshot)
        assertNotNull(secondSnapshot)

        val firstTimestamp =
            firstSnapshot.values["runtimeTimestamp"] as? Long

        val secondTimestamp =
            secondSnapshot.values["runtimeTimestamp"] as? Long

        assertNotNull(firstTimestamp)
        assertNotNull(secondTimestamp)

        assertTrue(
            secondTimestamp > firstTimestamp,
            "Each cognitive snapshot must capture a fresh runtime timestamp"
        )
    }
}
