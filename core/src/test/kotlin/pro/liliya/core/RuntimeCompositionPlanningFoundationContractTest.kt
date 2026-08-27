package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.planning.RuntimePlanState

class RuntimeCompositionPlanningFoundationContractTest {

    @Test
    fun composition_owns_stable_planner() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.planner(),
            composition.planner()
        )
    }

    @Test
    fun separate_compositions_own_independent_planners() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.planner(),
            second.planner()
        )
    }

    @Test
    fun composition_goal_and_planner_form_autonomous_planning_chain() {
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
                .derive(intelligence)

        val plan =
            composition
                .planner()
                .plan(goal)

        assertEquals(
            RuntimePlanState.RECOVER,
            plan.state
        )

        assertEquals(
            goal,
            plan.goal
        )
    }

    @Test
    fun prepare_runtime_preserves_stateless_planner_owner() {
        val composition =
            DefaultRuntimeComposition()

        val planner =
            composition.planner()

        composition.prepareRuntime()

        assertSame(
            planner,
            composition.planner()
        )
    }
}
