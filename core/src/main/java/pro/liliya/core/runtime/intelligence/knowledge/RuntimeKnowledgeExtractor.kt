package pro.liliya.core.runtime.intelligence.knowledge

import pro.liliya.core.runtime.intelligence.experience.consolidation.RuntimeExperienceConsolidation

interface RuntimeKnowledgeExtractor {

    fun extract(
        consolidation: RuntimeExperienceConsolidation
    ): RuntimeKnowledge
}
