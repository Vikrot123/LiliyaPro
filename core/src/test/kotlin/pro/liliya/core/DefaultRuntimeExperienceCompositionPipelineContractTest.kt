package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.experience.composition.DefaultRuntimeExperienceComposition
import pro.liliya.core.runtime.intelligence.experience.pipeline.RuntimeExperiencePipeline

class DefaultRuntimeExperienceCompositionPipelineContractTest {

    @Test
    fun composition_owns_stable_experience_pipeline() {
        val composition = DefaultRuntimeExperienceComposition()

        val pipeline = composition.experiencePipeline()

        assertSame(
            pipeline,
            composition.experiencePipeline()
        )
    }

    @Test
    fun composition_exposes_expected_pipeline_contract_type() {
        val composition = DefaultRuntimeExperienceComposition()

        val pipeline: RuntimeExperiencePipeline =
            composition.experiencePipeline()

        assertSame(
            pipeline,
            composition.experiencePipeline()
        )
    }
}
