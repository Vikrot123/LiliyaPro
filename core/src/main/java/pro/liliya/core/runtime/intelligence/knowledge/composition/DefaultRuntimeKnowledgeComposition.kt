package pro.liliya.core.runtime.intelligence.knowledge.composition

import pro.liliya.core.runtime.intelligence.knowledge.DefaultRuntimeKnowledgeExtractor
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeExtractor

class DefaultRuntimeKnowledgeComposition : RuntimeKnowledgeComposition {

    private val knowledgeExtractor: RuntimeKnowledgeExtractor =
        DefaultRuntimeKnowledgeExtractor()

    override fun knowledgeExtractor(): RuntimeKnowledgeExtractor {
        return knowledgeExtractor
    }
}
