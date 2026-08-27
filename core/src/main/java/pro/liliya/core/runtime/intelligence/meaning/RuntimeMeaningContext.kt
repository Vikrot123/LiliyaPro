package pro.liliya.core.runtime.intelligence.meaning

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrend
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

data class RuntimeMeaningContext(
    val selfModel: RuntimeSelfModel,
    val reflection: RuntimeReflectionSnapshot,
    val trend: RuntimeReflectionTrend,
    val cognitiveContext: CognitiveContextSnapshot? = null
)
