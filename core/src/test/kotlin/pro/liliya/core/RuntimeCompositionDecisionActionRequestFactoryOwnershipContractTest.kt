package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecisionActionRequestFactory

class RuntimeCompositionDecisionActionRequestFactoryOwnershipContractTest {

    @Test
    fun root_exposes_decision_action_request_factory() {
        val composition = DefaultRuntimeComposition()

        val factory =
            composition.decisionActionRequestFactory()

        assertNotNull(factory)
    }

    @Test
    fun root_exposes_runtime_factory_contract() {
        val composition = DefaultRuntimeComposition()

        val factory =
            composition.decisionActionRequestFactory()

        assertNotNull(factory as RuntimeDecisionActionRequestFactory)
    }

    @Test
    fun factory_is_owned_by_composition() {
        val composition = DefaultRuntimeComposition()

        val first =
            composition.decisionActionRequestFactory()

        val second =
            composition.decisionActionRequestFactory()

        assertSame(first, second)
    }
}
