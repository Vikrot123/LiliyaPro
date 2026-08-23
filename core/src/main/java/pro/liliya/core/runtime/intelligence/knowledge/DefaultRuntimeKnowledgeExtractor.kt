package pro.liliya.core.runtime.intelligence.knowledge

import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation

class DefaultRuntimeKnowledgeExtractor :
    RuntimeKnowledgeExtractor {

    override fun extract(
        consolidation: RuntimeExperienceConsolidation
    ): RuntimeKnowledge {

        return RuntimeKnowledge(
            statement = consolidation.summary,
            confidence = if (consolidation.processedCount > 0) {
                0.8
            } else {
                0.0
            },
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = System.currentTimeMillis()
        )
    }
}
