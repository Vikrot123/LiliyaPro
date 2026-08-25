package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.builder.CognitiveContextBuilder
import pro.liliya.core.runtime.intelligence.context.cognitive.pipeline.DefaultCognitiveContextPipeline
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.CognitiveContextSelectionCriteria
import pro.liliya.core.runtime.intelligence.context.cognitive.selection.DefaultCognitiveContextSelector

class CognitiveContextMaximumAgeLifecycleIsolationContractTest {

    @Test
    fun maximum_age_should_not_leak_into_following_process() {
        val now = 1_000L
        val pipeline = pipeline(now)
        val source = source("PAYLOAD")

        val rejected = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 100L
            )
        )

        assertEquals(0, rejected.selectedCount)
        assertEquals(1, rejected.rejectedCount)
        assertEquals(emptyMap<String, Any?>(), rejected.values)

        val accepted = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 2_000L
            )
        )

        assertEquals(1, accepted.selectedCount)
        assertEquals(0, accepted.rejectedCount)
        assertEquals(
            "PAYLOAD",
            (accepted.values["source_0"] as CognitiveContextSnapshot)
                .values["payload"]
        )
    }

    @Test
    fun permissive_maximum_age_should_not_override_following_strict_age() {
        val now = 1_000L
        val pipeline = pipeline(now)
        val source = source("PAYLOAD")

        val accepted = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = Long.MAX_VALUE
            )
        )

        assertEquals(1, accepted.selectedCount)
        assertEquals(0, accepted.rejectedCount)

        val rejected = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 100L
            )
        )

        assertEquals(0, rejected.selectedCount)
        assertEquals(1, rejected.rejectedCount)
    }

    @Test
    fun maximum_age_should_apply_only_to_its_own_process() {
        val now = 1_000L
        val pipeline = pipeline(now)
        val source = source("STABLE")

        val first = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 100L
            )
        )

        val second = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 2_000L
            )
        )

        val third = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 100L
            )
        )

        assertEquals(0, first.selectedCount)
        assertEquals(1, first.rejectedCount)

        assertEquals(1, second.selectedCount)
        assertEquals(0, second.rejectedCount)

        assertEquals(0, third.selectedCount)
        assertEquals(1, third.rejectedCount)
    }

    private fun pipeline(now: Long): DefaultCognitiveContextPipeline {
        return DefaultCognitiveContextPipeline(
            builder = FixedTimestampBuilder(timestamp = 0L),
            selector = DefaultCognitiveContextSelector(
                nowProvider = { now }
            )
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

    private class FixedTimestampBuilder(
        private val timestamp: Long
    ) : CognitiveContextBuilder {

        override fun build(
            type: CognitiveContextType,
            sources: List<CognitiveContextSource>
        ): CognitiveContextSnapshot {
            val values = linkedMapOf<String, Any?>()

            sources.forEachIndexed { index, source ->
                val snapshot = source.snapshot(type)

                if (snapshot != null) {
                    values["source_$index"] = snapshot
                }
            }

            return CognitiveContextSnapshot(
                type = type,
                values = values,
                timestamp = timestamp
            )
        }
    }
}
