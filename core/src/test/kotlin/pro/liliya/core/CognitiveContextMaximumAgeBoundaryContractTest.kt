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

class CognitiveContextMaximumAgeBoundaryContractTest {

    @Test
    fun snapshot_at_exact_maximum_age_should_be_accepted() {
        val now = 1_000L
        val pipeline = pipeline(
            now = now,
            timestamp = 900L
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source("EXACT")),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 100L
            )
        )

        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
        assertEquals(
            "EXACT",
            snapshotValue(result.values["source_0"])
        )
    }

    @Test
    fun snapshot_just_older_than_maximum_age_should_be_rejected() {
        val now = 1_000L
        val pipeline = pipeline(
            now = now,
            timestamp = 899L
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source("TOO_OLD")),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 100L
            )
        )

        assertEquals(0, result.selectedCount)
        assertEquals(1, result.rejectedCount)
        assertEquals(
            emptyMap<String, Any?>(),
            result.values
        )
    }

    @Test
    fun zero_maximum_age_should_accept_exact_timestamp() {
        val now = 1_000L
        val pipeline = pipeline(
            now = now,
            timestamp = 1_000L
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source("NOW")),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 0L
            )
        )

        assertEquals(1, result.selectedCount)
        assertEquals(0, result.rejectedCount)
        assertEquals(
            "NOW",
            snapshotValue(result.values["source_0"])
        )
    }

    @Test
    fun one_millisecond_over_boundary_should_be_rejected() {
        val now = 1_000L
        val pipeline = pipeline(
            now = now,
            timestamp = 899L
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source("BOUNDARY")),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 100L
            )
        )

        assertEquals(0, result.selectedCount)
        assertEquals(1, result.rejectedCount)
        assertEquals(
            emptyMap<String, Any?>(),
            result.values
        )
    }

    @Test
    fun boundary_selection_should_not_mutate_snapshot() {
        val now = 1_000L
        val timestamp = 900L
        val pipeline = pipeline(
            now = now,
            timestamp = timestamp
        )

        val result = pipeline.process(
            type = CognitiveContextType.TASK,
            sources = listOf(source("STABLE")),
            criteria = CognitiveContextSelectionCriteria(
                maximumAgeMillis = 100L
            )
        )

        val selected =
            result.values["source_0"] as CognitiveContextSnapshot

        val selectedTimestamp = selected.timestamp

        assertEquals(
            CognitiveContextType.TASK,
            selected.type
        )
        assertEquals(
            "STABLE",
            selected.values["payload"]
        )
        assertEquals(
            selectedTimestamp,
            selected.timestamp
        )
    }

    private fun pipeline(
        now: Long,
        timestamp: Long
    ): DefaultCognitiveContextPipeline {
        return DefaultCognitiveContextPipeline(
            builder = FixedTimestampBuilder(
                timestamp = timestamp
            ),
            selector = DefaultCognitiveContextSelector(
                nowProvider = { now }
            )
        )
    }

    private fun source(
        value: String
    ): CognitiveContextSource {
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
                    ?: error("source returned null snapshot")

                values["source_$index"] = snapshot
            }

            return CognitiveContextSnapshot(
                type = type,
                values = values,
                timestamp = timestamp
            )
        }
    }

    private fun snapshotValue(
        value: Any?
    ): Any? {
        return (value as? CognitiveContextSnapshot)
            ?.values
            ?.get("payload")
    }
}
