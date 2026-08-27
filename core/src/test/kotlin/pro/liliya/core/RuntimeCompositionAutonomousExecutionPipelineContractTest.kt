package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionAutonomousExecutionPipelineContractTest {

    @Test
    fun composition_owns_stable_autonomous_execution_pipeline() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition.autonomousExecutionPipeline(),
            composition.autonomousExecutionPipeline()
        )
    }

    @Test
    fun separate_compositions_own_independent_autonomous_execution_pipelines() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.autonomousExecutionPipeline(),
            second.autonomousExecutionPipeline()
        )
    }

    @Test
    fun prepare_runtime_preserves_autonomous_execution_pipeline_owner() {
        val composition =
            DefaultRuntimeComposition()

        val pipeline =
            composition.autonomousExecutionPipeline()

        composition.prepareRuntime()

        assertSame(
            pipeline,
            composition.autonomousExecutionPipeline()
        )
    }
}
