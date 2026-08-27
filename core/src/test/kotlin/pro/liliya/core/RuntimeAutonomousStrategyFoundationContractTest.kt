package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.goal.DefaultRuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.intent.DefaultRuntimeIntentDeriver
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.planning.DefaultRuntimePlanner
import pro.liliya.core.runtime.intelligence.reasoning.DefaultRuntimeReasoningAnalyzer
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.DefaultRuntimeAutonomousReasoningPipeline
import pro.liliya.core.runtime.intelligence.strategy.DefaultRuntimeStrategyDeriver
import pro.liliya.core.runtime.intelligence.strategy.RuntimeStrategyState

class RuntimeAutonomousStrategyFoundationContractTest {

    private val reasoningPipeline =
        DefaultRuntimeAutonomousReasoningPipeline(
            goalDeriver =
                DefaultRuntimeGoalDeriver(),
            planner =
                DefaultRuntimePlanner(),
            reasoningAnalyzer =
                DefaultRuntimeReasoningAnalyzer()
        )

    private val intentDeriver =
        DefaultRuntimeIntentDeriver()

    private val strategyDeriver =
        DefaultRuntimeStrategyDeriver()

    @Test
    fun stable_intelligence_flows_into_preservation_strategy() {
        val reasoning =
            reasoningPipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.STABLE,
                    confidence = 0.90
                )
            )

        val intent =
            intentDeriver.derive(reasoning)

        val strategy =
            strategyDeriver.derive(intent)

        assertEquals(
            RuntimeStrategyState.PRESERVE,
            strategy.state
        )

        assertSame(
            intent,
            strategy.intent
        )
    }

    @Test
    fun warning_intelligence_flows_into_diagnostic_strategy() {
        val reasoning =
            reasoningPipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.WARNING,
                    confidence = 0.80
                )
            )

        val intent =
            intentDeriver.derive(reasoning)

        val strategy =
            strategyDeriver.derive(intent)

        assertEquals(
            RuntimeStrategyState.DIAGNOSE,
            strategy.state
        )

        assertTrue(strategy.actionable)
    }

    @Test
    fun critical_intelligence_flows_into_restoration_strategy() {
        val reasoning =
            reasoningPipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.CRITICAL,
                    confidence = 0.95
                )
            )

        val strategy =
            strategyDeriver.derive(
                intentDeriver.derive(reasoning)
            )

        assertEquals(
            RuntimeStrategyState.RESTORE,
            strategy.state
        )

        assertTrue(strategy.actionable)
    }

    @Test
    fun unknown_intelligence_flows_into_monitoring_strategy() {
        val reasoning =
            reasoningPipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.UNKNOWN,
                    confidence = 0.50
                )
            )

        val strategy =
            strategyDeriver.derive(
                intentDeriver.derive(reasoning)
            )

        assertEquals(
            RuntimeStrategyState.MONITOR,
            strategy.state
        )

        assertTrue(strategy.actionable)
    }
}
