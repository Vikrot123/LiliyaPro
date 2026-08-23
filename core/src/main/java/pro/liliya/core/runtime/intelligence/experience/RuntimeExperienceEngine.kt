package pro.liliya.core.runtime.intelligence.experience

interface RuntimeExperienceEngine {

    fun createExperience(
        context: RuntimeExperienceContext
    ): RuntimeExperience
}
