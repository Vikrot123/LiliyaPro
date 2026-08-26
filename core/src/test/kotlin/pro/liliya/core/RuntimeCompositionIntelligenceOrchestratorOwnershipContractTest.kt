package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.composition.RuntimeComposition
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrator

class RuntimeCompositionIntelligenceOrchestratorOwnershipContractTest {

    @Test
    fun composition_exposes_intelligence_orchestrator() {
        val composition: RuntimeComposition =
            DefaultRuntimeComposition()

        val orchestrator =
            composition.intelligenceOrchestrator()

        assertNotNull(orchestrator)
    }

    @Test
    fun composition_reuses_same_intelligence_orchestrator_instance() {
        val composition: RuntimeComposition =
            DefaultRuntimeComposition()

        val first = composition.intelligenceOrchestrator()
        val second = composition.intelligenceOrchestrator()

        assertSame(first, second)
    }

    @Test
    fun composition_exposes_actual_intelligence_orchestrator_contract() {
        val composition = DefaultRuntimeComposition()

        val orchestrator =
            composition.intelligenceOrchestrator()

        assertNotNull(
            orchestrator as RuntimeIntelligenceOrchestrator
        )
    }
}
