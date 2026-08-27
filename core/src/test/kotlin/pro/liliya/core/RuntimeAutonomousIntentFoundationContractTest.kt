package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.goal.DefaultRuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.intent.DefaultRuntimeIntentDeriver
import pro.liliya.core.runtime.intelligence.intent.RuntimeIntentState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.planning.DefaultRuntimePlanner
import pro.liliya.core.runtime.intelligence.reasoning.DefaultRuntimeReasoningAnalyzer
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.DefaultRuntimeAutonomousReasoningPipeline

class RuntimeAutonomousIntentFoundationContractTest {

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

    @Test
    fun stable_intelligence_flows_into_preserve_intent() {
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

        assertEquals(
            RuntimeIntentState.PRESERVE,
            intent.state
        )

        assertSame(
            reasoning,
            intent.reasoningResult
        )
    }

    @Test
    fun warning_intelligence_flows_into_investigate_intent() {
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

        assertEquals(
            RuntimeIntentState.INVESTIGATE,
            intent.state
        )

        assertTrue(intent.actionable)
    }

    @Test
    fun critical_intelligence_flows_into_restore_intent() {
        val reasoning =
            reasoningPipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.CRITICAL,
                    confidence = 0.95
                )
            )

        val intent =
            intentDeriver.derive(reasoning)

        assertEquals(
            RuntimeIntentState.RESTORE,
            intent.state
        )

        assertTrue(intent.actionable)
    }

    @Test
    fun unknown_intelligence_flows_into_observe_intent() {
        val reasoning =
            reasoningPipeline.process(
                RuntimeIntelligenceFixture.result(
                    significance =
                        RuntimeMeaningSignificance.UNKNOWN,
                    confidence = 0.50
                )
            )

        val intent =
            intentDeriver.derive(reasoning)

        assertEquals(
            RuntimeIntentState.OBSERVE,
            intent.state
        )

        assertTrue(intent.actionable)
    }
}
