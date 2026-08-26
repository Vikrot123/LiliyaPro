package pro.liliya.core.runtime.intelligence.orchestration

import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrationResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrend
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

data class RuntimeIntelligenceOrchestrationResult(
    val selfModel: RuntimeSelfModel,
    val reflection: RuntimeReflectionSnapshot,
    val trend: RuntimeReflectionTrend,
    val meaning: RuntimeMeaningResult,
    val experienceKnowledge:
        RuntimeExperienceKnowledgeOrchestrationResult
)
