package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionEvidence
import pro.liliya.core.runtime.intelligence.decision.reflection.RuntimeDecisionReflectionInsight
import pro.liliya.core.runtime.intelligence.decision.reflection.history.DefaultRuntimeDecisionReflectionHistory
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.DefaultRuntimeDecisionReflectionTrendQuery
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendState

class DefaultRuntimeDecisionReflectionTrendQueryContractTest {

    @Test
    fun query_reads_current_bounded_history_snapshot() {
        val history =
            DefaultRuntimeDecisionReflectionHistory(
                capacity = 2
            )

        val query =
            DefaultRuntimeDecisionReflectionTrendQuery(
                history = history
            )

        history.record(
            insight(
                confidence = 0.60,
                trustworthy = false,
                attention = true
            )
        )

        history.record(
            insight(
                confidence = 0.90,
                trustworthy = true,
                attention = false
            )
        )

        val trend =
            query.currentTrend()

        assertEquals(
            2,
            trend.sampleCount
        )

        assertEquals(
            RuntimeDecisionReflectionTrendState.IMPROVING,
            trend.state
        )
    }

    @Test
    fun clear_immediately_returns_insufficient_trend() {
        val history =
            DefaultRuntimeDecisionReflectionHistory()

        val query =
            DefaultRuntimeDecisionReflectionTrendQuery(
                history = history
            )

        history.record(
            insight(
                confidence = 0.90,
                trustworthy = true,
                attention = false
            )
        )

        history.clear()

        assertEquals(
            RuntimeDecisionReflectionTrendState
                .INSUFFICIENT_DATA,
            query.currentTrend().state
        )
    }

    private fun insight(
        confidence: Double,
        trustworthy: Boolean,
        attention: Boolean
    ) =
        RuntimeDecisionReflectionInsight(
            evidence =
                RuntimeDecisionReflectionEvidence(
                    command = null,
                    decisionReason =
                        "query trend evidence",
                    confidence =
                        confidence,
                    knowledgeUsed =
                        trustworthy || attention,
                    provenanceAvailable =
                        trustworthy,
                    provenanceValid =
                        if (trustworthy) true
                        else if (attention) false
                        else null,
                    provenanceDepth =
                        if (trustworthy) 1 else 0
                ),
            trustworthyKnowledgeBasis =
                trustworthy,
            requiresAttention =
                attention,
            summary =
                "query trend insight"
        )
}
