package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionEvidence
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.history.DefaultRuntimeDecisionReflectionHistory

class DefaultRuntimeDecisionReflectionHistoryContractTest {

    @Test
    fun history_is_bounded_and_preserves_newest_reflections() {
        var now = 0L

        val history =
            DefaultRuntimeDecisionReflectionHistory(
                capacity = 2,
                clock = {
                    now += 1L
                    now
                }
            )

        val first =
            insight("first")

        val second =
            insight("second")

        val third =
            insight("third")

        history.record(first)
        history.record(second)
        history.record(third)

        val records =
            history.records()

        assertEquals(
            2,
            records.size
        )

        assertEquals(
            listOf(
                second,
                third
            ),
            records.map {
                it.insight
            }
        )

        assertEquals(
            listOf(
                2L,
                3L
            ),
            records.map {
                it.recordedAt
            }
        )
    }

    @Test
    fun clear_removes_all_reflection_history() {
        val history =
            DefaultRuntimeDecisionReflectionHistory()

        history.record(
            insight("one")
        )

        history.clear()

        assertTrue(
            history.records().isEmpty()
        )
    }

    @Test
    fun non_positive_capacity_is_rejected() {
        assertFailsWith<IllegalArgumentException> {
            DefaultRuntimeDecisionReflectionHistory(
                capacity = 0
            )
        }
    }

    private fun insight(
        summary: String
    ) =
        RuntimeDecisionReflectionInsight(
            evidence =
                RuntimeDecisionReflectionEvidence(
                    command = null,
                    decisionReason = summary,
                    confidence = 0.90,
                    knowledgeUsed = false,
                    provenanceAvailable = false,
                    provenanceValid = null,
                    provenanceDepth = 0
                ),
            trustworthyKnowledgeBasis = false,
            requiresAttention = false,
            summary = summary
        )
}
