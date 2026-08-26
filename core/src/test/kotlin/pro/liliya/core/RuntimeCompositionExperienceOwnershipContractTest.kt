package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.experience.composition.RuntimeExperienceComposition

class RuntimeCompositionExperienceOwnershipContractTest {

    @Test
    fun root_composition_owns_stable_experience_composition() {
        val composition = DefaultRuntimeComposition()

        val experience = composition.experienceComposition()

        assertSame(
            experience,
            composition.experienceComposition()
        )
    }

    @Test
    fun separate_root_compositions_do_not_share_experience_composition() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        assertNotSame(
            first.experienceComposition(),
            second.experienceComposition()
        )
    }

    @Test
    fun root_experience_composition_exposes_expected_contract_type() {
        val composition = DefaultRuntimeComposition()

        val experience: RuntimeExperienceComposition =
            composition.experienceComposition()

        assertSame(
            experience,
            composition.experienceComposition()
        )
    }
}
