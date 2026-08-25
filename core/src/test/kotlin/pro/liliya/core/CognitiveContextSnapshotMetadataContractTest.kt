package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata

class CognitiveContextSnapshotMetadataContractTest {

    @Test
    fun snapshot_should_expose_empty_metadata_by_default() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK
        )

        assertTrue(snapshot.metadata.isEmpty())
    }

    @Test
    fun snapshot_should_preserve_explicit_metadata() {
        val metadata = CognitiveContextValueMetadata(
            relevance = 0.8,
            importance = 0.7,
            confidence = 0.9
        )

        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.WORKING,
            values = mapOf("memory" to "important"),
            metadata = mapOf("memory" to metadata)
        )

        assertEquals(
            metadata,
            snapshot.metadata["memory"]
        )
    }

    @Test
    fun snapshot_metadata_should_be_keyed_by_value_key() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.PROCESSOR,
            values = mapOf(
                "first" to "A",
                "second" to "B"
            ),
            metadata = mapOf(
                "first" to CognitiveContextValueMetadata(
                    relevance = 0.9
                ),
                "second" to CognitiveContextValueMetadata(
                    importance = 0.8
                )
            )
        )

        assertNotNull(snapshot.metadata["first"])
        assertNotNull(snapshot.metadata["second"])

        assertEquals(
            0.9,
            snapshot.metadata["first"]?.relevance
        )

        assertEquals(
            0.8,
            snapshot.metadata["second"]?.importance
        )
    }

    @Test
    fun snapshot_values_should_remain_independent_from_metadata() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.SESSION,
            values = mapOf(
                "value" to "payload"
            ),
            metadata = mapOf(
                "other" to CognitiveContextValueMetadata(
                    confidence = 0.5
                )
            )
        )

        assertEquals(
            "payload",
            snapshot.values["value"]
        )

        assertTrue(
            !snapshot.metadata.containsKey("value")
        )
    }

    @Test
    fun legacy_snapshot_construction_should_remain_valid() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.GLOBAL,
            values = mapOf(
                "legacy" to "value"
            )
        )

        assertEquals(
            "value",
            snapshot.values["legacy"]
        )

        assertTrue(
            snapshot.metadata.isEmpty()
        )
    }
}
