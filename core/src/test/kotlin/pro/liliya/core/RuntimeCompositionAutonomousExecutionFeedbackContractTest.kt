package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionAutonomousExecutionFeedbackContractTest {

    @Test
    fun composition_owns_stable_autonomous_execution_feedback_deriver() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.autonomousExecutionFeedbackDeriver(),
            composition.autonomousExecutionFeedbackDeriver()
        )
    }

    @Test
    fun separate_compositions_own_independent_feedback_derivers() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.autonomousExecutionFeedbackDeriver(),
            second.autonomousExecutionFeedbackDeriver()
        )
    }

    @Test
    fun prepare_runtime_preserves_stateless_feedback_deriver_owner() {
        val composition =
            DefaultRuntimeComposition()

        val deriver =
            composition.autonomousExecutionFeedbackDeriver()

        composition.prepareRuntime()

        assertSame(
            deriver,
            composition.autonomousExecutionFeedbackDeriver()
        )
    }
}
