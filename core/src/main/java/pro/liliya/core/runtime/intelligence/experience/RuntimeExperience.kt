package pro.liliya.core.runtime.intelligence.experience

import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult

data class RuntimeExperience(
    val description: String,
    val meaning: RuntimeMeaningResult,
    val importance: RuntimeExperienceImportance,
    val createdAt: Long
)
