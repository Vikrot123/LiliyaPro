package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.strategy.RuntimeStrategyState

class RuntimeCompositionStrategyFoundationContractTest {

    @Test
    fun composition_owns_stable_strategy_deriver() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.strategyDeriver(),
            composition.strategyDeriver()
        )
    }

    @Test
    fun separate_compositions_own_independent_strategy_derivers() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.strategyDeriver(),
            second.strategyDeriver()
        )
    }

    @Test
    fun composition_forms_reasoning_intent_strategy_chain() {
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

        val strategy =
            composition
                .strategyDeriver()
                .derive(intent)

        assertEquals(
            RuntimeStrategyState.RESTORE,
            strategy.state
        )

        assertSame(
            intent,
            strategy.intent
        )
    }

    @Test
    fun prepare_runtime_preserves_stateless_strategy_deriver_owner() {
        val composition =
            DefaultRuntimeComposition()

        val deriver =
            composition.strategyDeriver()

        composition.prepareRuntime()

        assertSame(
            deriver,
            composition.strategyDeriver()
        )
    }
}
