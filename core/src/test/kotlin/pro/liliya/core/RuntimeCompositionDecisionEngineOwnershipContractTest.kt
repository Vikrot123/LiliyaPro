package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecisionEngine

class RuntimeCompositionDecisionEngineOwnershipContractTest {

    @Test
    fun root_exposes_decision_engine() {
        val composition = DefaultRuntimeComposition()

        val engine = composition.decisionEngine()

        assertNotNull(engine)
    }

    @Test
    fun root_exposes_runtime_decision_engine_contract() {
        val composition = DefaultRuntimeComposition()

        val engine = composition.decisionEngine()

        assertNotNull(engine as RuntimeDecisionEngine)
    }

    @Test
    fun decision_engine_is_owned_by_composition() {
        val composition = DefaultRuntimeComposition()

        val first = composition.decisionEngine()
        val second = composition.decisionEngine()

        assertSame(first, second)
    }
}
