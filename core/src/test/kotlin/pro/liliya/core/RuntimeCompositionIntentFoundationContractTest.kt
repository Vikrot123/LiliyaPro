package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.intent.RuntimeIntentState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeCompositionIntentFoundationContractTest {

    @Test
    fun composition_owns_stable_intent_deriver() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.intentDeriver(),
            composition.intentDeriver()
        )
    }

    @Test
    fun separate_compositions_own_independent_intent_derivers() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.intentDeriver(),
            second.intentDeriver()
        )
    }

    @Test
    fun composition_forms_autonomous_reasoning_to_intent_chain() {
        val composition =
            DefaultRuntimeComposition()

        val reasoning =
            composition
                .autonomousReasoningPipeline()
                .process(
                    RuntimeIntelligenceFixture.result(
                        significance =
                            RuntimeMeaningSignificance.CRITICAL,
                        confidence = 0.95
                    )
                )

        val intent =
            composition
                .intentDeriver()
                .derive(reasoning)

        assertEquals(
            RuntimeIntentState.RESTORE,
            intent.state
        )

        assertSame(
            reasoning,
            intent.reasoningResult
        )
    }

    @Test
    fun prepare_runtime_preserves_stateless_intent_deriver_owner() {
        val composition =
            DefaultRuntimeComposition()

        val deriver =
            composition.intentDeriver()

        composition.prepareRuntime()

        assertSame(
            deriver,
            composition.intentDeriver()
        )
    }
}
