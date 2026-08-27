package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.goal.RuntimeGoalState
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance

class RuntimeCompositionGoalFoundationContractTest {

    @Test
    fun composition_owns_stable_goal_deriver() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.goalDeriver(),
            composition.goalDeriver()
        )
    }

    @Test
    fun separate_compositions_own_independent_goal_derivers() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.goalDeriver(),
            second.goalDeriver()
        )
    }

    @Test
    fun composition_goal_deriver_translates_runtime_intelligence_into_goal() {
        val composition =
            DefaultRuntimeComposition()

        val goal =
            composition
                .goalDeriver()
                .derive(
                    RuntimeIntelligenceFixture.result(
                        significance =
                            RuntimeMeaningSignificance.CRITICAL,
                        confidence = 0.95
                    )
                )

        assertEquals(
            RuntimeGoalState.RECOVER,
            goal.state
        )
    }

    @Test
    fun prepare_runtime_preserves_stateless_goal_deriver_owner() {
        val composition =
            DefaultRuntimeComposition()

        val deriver =
            composition.goalDeriver()

        composition.prepareRuntime()

        assertSame(
            deriver,
            composition.goalDeriver()
        )
    }
}
