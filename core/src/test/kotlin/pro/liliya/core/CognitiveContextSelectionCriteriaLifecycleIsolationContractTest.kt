package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.CognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextValueMetadata
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextSelectionCriteriaLifecycleIsolationContractTest {

    @Test
    fun strict_criteria_should_not_leak_into_following_process() {
        val pipeline = pipeline()
        val source = source("PAYLOAD")

        val rejected = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = strictCriteria()
        )

        assertEquals(0, rejected.selectedCount)
        assertEquals(1, rejected.rejectedCount)
        assertEquals(emptyMap<String, Any?>(), rejected.values)

        val accepted = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source)
        )

        assertEquals(1, accepted.selectedCount)
        assertEquals(0, accepted.rejectedCount)
        assertEquals(
            "PAYLOAD",
            accepted.values["payload"]
        )
    }

    @Test
    fun ordinary_criteria_should_not_inherit_previous_strict_criteria() {
        val pipeline = pipeline()
        val source = source("SECOND")

        pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = strictCriteria()
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = CognitiveContextSelectionCriteria(
                minimumRelevance = 0.0,
                minimumImportance = 0.0,
                minimumConfidence = 0.0
            )
        )

        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
        assertEquals(
            "SECOND",
            result.values["payload"]
        )
    }

    @Test
    fun strict_criteria_should_apply_only_to_its_own_process() {
        val pipeline = pipeline()
        val source = source("STABLE")

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = strictCriteria()
        )

        val second = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source)
        )

        val third = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = strictCriteria()
        )

        assertEquals(0, first.selectedCount)
        assertEquals(1, first.rejectedCount)

        assertEquals(1, second.selectedCount)
        assertEquals(0, second.rejectedCount)

        assertEquals(0, third.selectedCount)
        assertEquals(1, third.rejectedCount)
    }

    private fun pipeline(): DefaultCognitiveContextPipeline {
        return DefaultCognitiveContextPipeline(
            builder = MetadataPreservingBuilder(),
            selector = DefaultCognitiveContextSelector()
        )
    }

    private fun strictCriteria(): CognitiveContextSelectionCriteria {
        return CognitiveContextSelectionCriteria(
            minimumRelevance = 0.9,
            minimumImportance = 0.9,
            minimumConfidence = 0.9
        )
    }

    private fun source(value: String): CognitiveContextSource {
        return object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return CognitiveContextSnapshot(
                    type = type,
                    values = mapOf("payload" to value)
                )
            }
        }
    }

    private class MetadataPreservingBuilder : CognitiveContextBuilder {

        override fun build(
            type: CognitiveContextType,
            sources: List<CognitiveContextSource>
        ): CognitiveContextSnapshot {
            val source = sources.first()

            val nested = source.snapshot(type)
                ?: error("source returned null snapshot")
            val value = nested.values["payload"]

            return CognitiveContextSnapshot(
                type = type,
                values = mapOf("payload" to value),
                metadata = mapOf(
                    "payload" to CognitiveContextValueMetadata(
                        relevance = 0.1,
                        importance = 0.1,
                        confidence = 0.1
                    )
                )
            )
        }
    }
}
