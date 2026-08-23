package pro.liliya.core.runtime.intelligence.experience

import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

data class RuntimeExperienceContext(
    val selfModel: RuntimeSelfModel,
    val meaning: RuntimeMeaningResult
)
