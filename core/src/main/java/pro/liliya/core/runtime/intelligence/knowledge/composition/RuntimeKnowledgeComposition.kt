package pro.liliya.core.runtime.intelligence.knowledge.composition

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeExtractor

interface RuntimeKnowledgeComposition {

    fun knowledgeExtractor(): RuntimeKnowledgeExtractor
}
