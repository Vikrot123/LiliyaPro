package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.decision.explanation.RuntimeDecisionExplanation
import pro.liliya.core.runtime.intelligence.decision.explanation.history.DefaultRuntimeDecisionExplanationHistory

class DefaultRuntimeDecisionExplanationHistoryContractTest {

    @Test
    fun history_is_bounded_and_preserves_newest_records() {
        var now = 0L

        val history =
            DefaultRuntimeDecisionExplanationHistory(
                capacity = 2,
                clock = {
                    now += 1L
                    now
                }
            )

        val first =
            explanation("first")

        val second =
            explanation("second")

        val third =
            explanation("third")

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
            listOf(second, third),
            records.map {
                it.explanation
            }
        )

        assertEquals(
            listOf(2L, 3L),
            records.map {
                it.recordedAt
            }
        )
    }

    @Test
    fun clear_removes_all_explanation_history() {
        val history =
            DefaultRuntimeDecisionExplanationHistory(
                capacity = 2
            )

        history.record(
            explanation("one")
        )

        history.clear()

        assertEquals(
            emptyList(),
            history.records()
        )
    }

    private fun explanation(
        reason: String
    ): RuntimeDecisionExplanation {

        return RuntimeDecisionExplanation(
            command = null,
            decisionReason = reason,
            confidence = 0.9,
            knowledgeStatement = null,
            knowledgeSelectionReason = null,
            knowledgeRelevanceScore = 0.0
        )
    }
}
