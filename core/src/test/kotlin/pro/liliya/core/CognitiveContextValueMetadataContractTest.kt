package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata

class CognitiveContextValueMetadataContractTest {

    @Test
    fun default_metadata_should_be_neutral() {
        val metadata = CognitiveContextValueMetadata()

        assertEquals(0.0, metadata.relevance)
        assertEquals(0.0, metadata.importance)
        assertEquals(0.0, metadata.confidence)
    }

    @Test
    fun metadata_should_preserve_fractional_scores() {
        val metadata = CognitiveContextValueMetadata(
            relevance = 0.25,
            importance = 0.50,
            confidence = 0.75
        )

        assertEquals(0.25, metadata.relevance)
        assertEquals(0.50, metadata.importance)
        assertEquals(0.75, metadata.confidence)
    }

    @Test
    fun metadata_should_be_value_based() {
        val first = CognitiveContextValueMetadata(
            relevance = 0.8,
            importance = 0.7,
            confidence = 0.9
        )

        val second = CognitiveContextValueMetadata(
            relevance = 0.8,
            importance = 0.7,
            confidence = 0.9
        )

        assertEquals(first, second)
        assertEquals(first.hashCode(), second.hashCode())
    }

    @Test
    fun metadata_values_should_be_independent() {
        val first = CognitiveContextValueMetadata(
            relevance = 0.1,
            importance = 0.2,
            confidence = 0.3
        )

        val second = CognitiveContextValueMetadata(
            relevance = 0.9,
            importance = 0.8,
            confidence = 0.7
        )

        assertNotEquals(first, second)
    }

    @Test
    fun metadata_should_allow_maximum_scores() {
        val metadata = CognitiveContextValueMetadata(
            relevance = 1.0,
            importance = 1.0,
            confidence = 1.0
        )

        assertEquals(1.0, metadata.relevance)
        assertEquals(1.0, metadata.importance)
        assertEquals(1.0, metadata.confidence)
    }
}
