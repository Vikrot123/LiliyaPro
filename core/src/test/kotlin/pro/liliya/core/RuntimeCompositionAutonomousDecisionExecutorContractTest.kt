package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionAutonomousDecisionExecutorContractTest {

    @Test
    fun composition_owns_stable_autonomous_decision_executor() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.autonomousDecisionExecutor(),
            composition.autonomousDecisionExecutor()
        )
    }

    @Test
    fun separate_compositions_own_independent_autonomous_decision_executors() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.autonomousDecisionExecutor(),
            second.autonomousDecisionExecutor()
        )
    }

    @Test
    fun prepare_runtime_preserves_autonomous_decision_executor_owner() {
        val composition =
            DefaultRuntimeComposition()

        val executor =
            composition.autonomousDecisionExecutor()

        composition.prepareRuntime()

        assertSame(
            executor,
            composition.autonomousDecisionExecutor()
        )
    }
}
