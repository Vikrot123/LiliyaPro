package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.experience.pipeline.RuntimeExperiencePipeline

class RuntimeCompositionExperiencePipelineOwnershipContractTest {

    @Test
    fun root_composition_owns_stable_experience_pipeline() {
        val composition = DefaultRuntimeComposition()

        val pipeline = composition.experienceComposition()
            .experiencePipeline()

        assertSame(
            pipeline,
            composition.experienceComposition()
                .experiencePipeline()
        )
    }

    @Test
    fun separate_root_compositions_do_not_share_experience_pipeline() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        assertNotSame(
            first.experienceComposition().experiencePipeline(),
            second.experienceComposition().experiencePipeline()
        )
    }

    @Test
    fun root_exposes_pipeline_as_expected_contract_type() {
        val composition = DefaultRuntimeComposition()

        val pipeline: RuntimeExperiencePipeline =
            composition.experienceComposition()
                .experiencePipeline()

        assertSame(
            pipeline,
            composition.experienceComposition()
                .experiencePipeline()
        )
    }
}
