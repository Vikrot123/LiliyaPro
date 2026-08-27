package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.reasoning.RuntimeReasoningState

class RuntimeCompositionReasoningHardeningContractTest {

    @Test
    fun composition_reasoning_chain_is_repeatable() {
        val composition =
            DefaultRuntimeComposition()

        val intelligence =
            RuntimeIntelligenceFixture.result(
                significance =
                    RuntimeMeaningSignificance.CRITICAL,
                confidence = 0.95
            )

        val goal =
            composition
                .goalDeriver()
                .derive(
                    intelligence
                )

        val plan =
            composition
                .planner()
                .plan(
                    goal
                )

        val first =
            composition
                .reasoningAnalyzer()
                .analyze(
                    goal,
                    plan
                )

        val second =
            composition
                .reasoningAnalyzer()
                .analyze(
                    goal,
                    plan
                )

        assertEquals(
            RuntimeReasoningState.COHERENT,
            first.state
        )

        assertEquals(
            first,
            second
        )
    }

    @Test
    fun separate_compositions_keep_reasoning_ownership_isolated() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.reasoningAnalyzer(),
            second.reasoningAnalyzer()
        )

        assertNotSame(
            first.planner(),
            second.planner()
        )

        assertNotSame(
            first.goalDeriver(),
            second.goalDeriver()
        )
    }

    @Test
    fun prepare_preserves_complete_stateless_autonomy_chain() {
        val composition =
            DefaultRuntimeComposition()

        val goalDeriver =
            composition.goalDeriver()

        val planner =
            composition.planner()

        val reasoning =
            composition.reasoningAnalyzer()

        composition.prepareRuntime()

        assertSame(
            goalDeriver,
            composition.goalDeriver()
        )

        assertSame(
            planner,
            composition.planner()
        )

        assertSame(
            reasoning,
            composition.reasoningAnalyzer()
        )
    }
}
