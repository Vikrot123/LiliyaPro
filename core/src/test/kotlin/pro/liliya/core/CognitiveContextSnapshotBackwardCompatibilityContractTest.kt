package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class CognitiveContextSnapshotBackwardCompatibilityContractTest {

    @Test
    fun positional_construction_with_type_and_values_should_remain_valid() {
        val snapshot = CognitiveContextSnapshot(
            CognitiveContextType.TASK,
            mapOf("value" to "payload")
        )

        assertEquals(
            "payload",
            snapshot.values["value"]
        )

        assertTrue(snapshot.metadata.isEmpty())
    }

    @Test
    fun timestamp_should_remain_explicitly_available() {
        val timestamp = 123_456L

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING,
            values = emptyMap(),
            timestamp = timestamp
        )

        assertEquals(
            timestamp,
            snapshot.timestamp
        )
    }

    @Test
    fun explicit_metadata_and_timestamp_should_coexist() {
        val timestamp = 987_654L

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.PROCESSOR,
            values = mapOf("value" to "payload"),
            metadata = emptyMap(),
            timestamp = timestamp
        )

        assertEquals(
            timestamp,
            snapshot.timestamp
        )

        assertEquals(
            "payload",
            snapshot.values["value"]
        )

        assertTrue(snapshot.metadata.isEmpty())
    }
}
