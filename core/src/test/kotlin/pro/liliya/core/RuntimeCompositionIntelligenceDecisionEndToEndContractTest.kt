package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeCompositionIntelligenceDecisionEndToEndContractTest {

    @Test
    fun composition_exposes_real_intelligence_to_real_decision_engine() {
        val composition = DefaultRuntimeComposition()

        val intelligence = composition
            .intelligenceOrchestrator()
            .process()

        val decision = composition
            .decisionEngine()
            .decide(intelligence)

        assertNotNull(intelligence)
        assertNotNull(decision)
        assertNotNull(decision.reason)
    }

    @Test
    fun real_intelligence_result_can_be_consumed_by_decision_engine() {
        val composition = DefaultRuntimeComposition()

        val intelligence = composition
            .intelligenceOrchestrator()
            .process()

        val decision = composition
            .decisionEngine()
            .decide(intelligence)

        assertNotNull(
            intelligence as RuntimeIntelligenceOrchestrationResult
        )
        assertNotNull(
            decision as RuntimeDecision
        )
    }

    @Test
    fun stable_intelligence_produces_no_action_decision() {
        val composition = DefaultRuntimeComposition()

        val intelligence = composition
            .intelligenceOrchestrator()
            .process()

        if (intelligence.meaning.significance ==
            RuntimeMeaningSignificance.STABLE
        ) {
            val decision = composition
                .decisionEngine()
                .decide(intelligence)

            assertNull(decision.command)
        }
    }

    @Test
    fun decision_preserves_intelligence_confidence() {
        val composition = DefaultRuntimeComposition()

        val intelligence = composition
            .intelligenceOrchestrator()
            .process()

        val decision = composition
            .decisionEngine()
            .decide(intelligence)

        assertEquals(
            intelligence.meaning.confidence,
            decision.confidence
        )
    }

    @Test
    fun decision_engine_is_same_composed_instance_during_flow() {
        val composition = DefaultRuntimeComposition()

        val first = composition.decisionEngine()

        composition
            .intelligenceOrchestrator()
            .process()

        val second = composition.decisionEngine()

        assertSame(first, second)
    }

    @Test
    fun decision_command_matches_real_meaning_significance() {
        val composition = DefaultRuntimeComposition()

        val intelligence = composition
            .intelligenceOrchestrator()
            .process()

        val decision = composition
            .decisionEngine()
            .decide(intelligence)

        when (intelligence.meaning.significance) {
            RuntimeMeaningSignificance.STABLE ->
                assertNull(decision.command)

            RuntimeMeaningSignificance.WARNING ->
                assertEquals(
                    RuntimeCommand.HEALTH_CHECK,
                    decision.command
                )

            RuntimeMeaningSignificance.CRITICAL ->
                assertEquals(
                    RuntimeCommand.RECOVER,
                    decision.command
                )

            RuntimeMeaningSignificance.UNKNOWN ->
                assertEquals(
                    RuntimeCommand.HEALTH_CHECK,
                    decision.command
                )
        }
    }
}
