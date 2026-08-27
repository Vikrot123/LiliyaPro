package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionAutonomousExecutionEvaluationPipelineContractTest {

    @Test
    fun composition_owns_stable_autonomous_execution_evaluation_pipeline() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.autonomousExecutionEvaluationPipeline(),
            composition.autonomousExecutionEvaluationPipeline()
        )
    }

    @Test
    fun separate_compositions_own_independent_evaluation_pipelines() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.autonomousExecutionEvaluationPipeline(),
            second.autonomousExecutionEvaluationPipeline()
        )
    }

    @Test
    fun prepare_runtime_preserves_stateless_evaluation_pipeline_owner() {
        val composition =
            DefaultRuntimeComposition()

        val pipeline =
            composition.autonomousExecutionEvaluationPipeline()

        composition.prepareRuntime()

        assertSame(
            pipeline,
            composition.autonomousExecutionEvaluationPipeline()
        )
    }
}
