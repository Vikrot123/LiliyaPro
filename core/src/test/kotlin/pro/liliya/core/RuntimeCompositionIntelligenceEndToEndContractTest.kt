package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionStability

class RuntimeCompositionIntelligenceEndToEndContractTest {

    @Test
    fun root_exposes_real_intelligence_orchestration_result() {
        val composition = DefaultRuntimeComposition()

        val result = composition
            .intelligenceOrchestrator()
            .process()

        assertNotNull(result)

        assertNotNull(result.selfModel)
        assertNotNull(result.reflection)
        assertNotNull(result.trend)
        assertNotNull(result.meaning)
        assertNotNull(result.experienceKnowledge)
    }

    @Test
    fun real_intelligence_orchestration_preserves_runtime_state_flow() {
        val composition = DefaultRuntimeComposition()

        val result = composition
            .intelligenceOrchestrator()
            .process()

        assertEquals(
            composition.runtimeState().name,
            result.selfModel.snapshot.runtimeState
        )

        assertEquals(
            result.reflection.healthy,
            result.trend.healthyRatio == 1.0
        )
    }

    @Test
    fun real_intelligence_orchestration_produces_consistent_reflection_meaning_chain() {
        val composition = DefaultRuntimeComposition()

        val result = composition
            .intelligenceOrchestrator()
            .process()

        if (result.reflection.healthy) {
            assertEquals(
                RuntimeReflectionStability.STABLE,
                result.trend.stability
            )

            assertEquals(
                RuntimeMeaningSignificance.STABLE,
                result.meaning.significance
            )
        }
    }

    @Test
    fun real_intelligence_orchestrator_is_owned_by_composition() {
        val composition = DefaultRuntimeComposition()

        val first = composition.intelligenceOrchestrator()
        val second = composition.intelligenceOrchestrator()

        assertSame(first, second)
    }

    @Test
    fun real_intelligence_result_has_expected_contract_type() {
        val composition = DefaultRuntimeComposition()

        val result = composition
            .intelligenceOrchestrator()
            .process()

        assertNotNull(
            result as RuntimeIntelligenceOrchestrationResult
        )
    }
}
