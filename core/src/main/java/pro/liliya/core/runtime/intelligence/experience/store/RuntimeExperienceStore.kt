package pro.liliya.core.runtime.intelligence.experience.store

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience

interface RuntimeExperienceStore {

    fun append(
        experience: RuntimeExperience
    )

    fun remove(
        experience: RuntimeExperience
    ): Boolean

    fun experiences(): List<RuntimeExperience>
}
