package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.DefaultCognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata

class CognitiveContextMetadataPropagationContractTest {

    @Test
    fun builder_should_preserve_source_snapshot_metadata() {
        val metadata = CognitiveContextValueMetadata(
            relevance = 0.9,
            importance = 0.8,
            confidence = 0.7
        )

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("memory" to "important"),
                    metadata = mapOf("memory" to metadata)
                )
            }
        }

        val result = DefaultCognitiveContextBuilder().build(
            type = CognitiveContextType.TASK,
            sources = listOf(source)
        )

        val sourceSnapshot =
            result.values["source_0"] as CognitiveContextSnapshot

        assertEquals(
            metadata,
            sourceSnapshot.metadata["memory"]
        )
    }

    @Test
    fun builder_should_preserve_metadata_for_multiple_sources() {
        val firstMetadata = CognitiveContextValueMetadata(
            relevance = 0.9
        )

        val secondMetadata = CognitiveContextValueMetadata(
            importance = 0.8
        )

        val first = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("first" to "one"),
                    metadata = mapOf("first" to firstMetadata)
                )
            }
        }

        val second = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("second" to "two"),
                    metadata = mapOf("second" to secondMetadata)
                )
            }
        }

        val result = DefaultCognitiveContextBuilder().build(
            type = CognitiveContextType.WORKING,
            sources = listOf(first, second)
        )

        val firstSnapshot =
            result.values["source_0"] as CognitiveContextSnapshot

        val secondSnapshot =
            result.values["source_1"] as CognitiveContextSnapshot

        assertEquals(
            firstMetadata,
            firstSnapshot.metadata["first"]
        )

        assertEquals(
            secondMetadata,
            secondSnapshot.metadata["second"]
        )
    }

    @Test
    fun builder_should_preserve_metadata_without_mutating_source_snapshot() {
        val metadata = CognitiveContextValueMetadata(
            relevance = 0.6,
            importance = 0.5,
            confidence = 0.4
        )

        val sourceSnapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.PROCESSOR,
            values = mapOf("value" to "payload"),
            metadata = mapOf("value" to metadata)
        )

        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return sourceSnapshot
            }
        }

        val result = DefaultCognitiveContextBuilder().build(
            type = CognitiveContextType.PROCESSOR,
            sources = listOf(source)
        )

        val propagated =
            result.values["source_0"] as CognitiveContextSnapshot

        assertNotNull(propagated.metadata["value"])
        assertEquals(
            sourceSnapshot.metadata,
            propagated.metadata
        )
        assertEquals(
            sourceSnapshot.values,
            propagated.values
        )
    }
}
