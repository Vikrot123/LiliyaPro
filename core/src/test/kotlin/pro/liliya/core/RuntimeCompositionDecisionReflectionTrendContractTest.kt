package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.decision.reflection.trend.RuntimeDecisionReflectionTrendState

class RuntimeCompositionDecisionReflectionTrendContractTest {

    @Test
    fun composition_owns_stable_trend_analyzer_and_query() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition
                .decisionReflectionTrendAnalyzer(),
            composition
                .decisionReflectionTrendAnalyzer()
        )

        assertSame(
            composition
                .decisionReflectionTrendQuery(),
            composition
                .decisionReflectionTrendQuery()
        )
    }

    @Test
    fun separate_compositions_own_independent_trend_queries() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.decisionReflectionTrendQuery(),
            second.decisionReflectionTrendQuery()
        )
    }

    @Test
    fun recorded_reflections_drive_composition_trend() {
        val composition =
            DefaultRuntimeComposition()

        val firstExplanation =
            composition
                .decisionExplainer()
                .explain(
                    RuntimeDecision(
                        command = null,
                        reason =
                            "first reflection",
                        confidence = 0.60
                    )
                )

        val secondExplanation =
            composition
                .decisionExplainer()
                .explain(
                    RuntimeDecision(
                        command = null,
                        reason =
                            "second reflection",
                        confidence = 0.85
                    )
                )

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                firstExplanation
            )

        composition
            .decisionReflectionRecorder()
            .analyzeAndRecord(
                secondExplanation
            )

        val trend =
            composition
                .decisionReflectionTrendQuery()
                .currentTrend()

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
    fun prepare_runtime_resets_trend_through_history_clear() {
        val composition =
            DefaultRuntimeComposition()

        repeat(2) { index ->
            val explanation =
                composition
                    .decisionExplainer()
                    .explain(
                        RuntimeDecision(
                            command = null,
                            reason =
                                "trend reset $index",
                            confidence =
                                0.80 + (index * 0.05)
                        )
                    )

            composition
                .decisionReflectionRecorder()
                .analyzeAndRecord(
                    explanation
                )
        }

        assertEquals(
            2,
            composition
                .decisionReflectionTrendQuery()
                .currentTrend()
                .sampleCount
        )

        composition.prepareRuntime()

        val after =
            composition
                .decisionReflectionTrendQuery()
                .currentTrend()

        assertEquals(
            0,
            after.sampleCount
        )

        assertEquals(
            RuntimeDecisionReflectionTrendState
                .INSUFFICIENT_DATA,
            after.state
        )
    }

    @Test
    fun prepare_runtime_preserves_trend_query_owner() {
        val composition =
            DefaultRuntimeComposition()

        val analyzer =
            composition
                .decisionReflectionTrendAnalyzer()

        val query =
            composition
                .decisionReflectionTrendQuery()

        composition.prepareRuntime()

        assertSame(
            analyzer,
            composition
                .decisionReflectionTrendAnalyzer()
        )

        assertSame(
            query,
            composition
                .decisionReflectionTrendQuery()
        )
    }
}
