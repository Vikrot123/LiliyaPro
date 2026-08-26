package pro.liliya.core.runtime.intelligence.knowledge.composition

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeExtractor
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipeline

interface RuntimeKnowledgeComposition {

    fun knowledgeExtractor(): RuntimeKnowledgeExtractor

    fun knowledgePipeline(): RuntimeKnowledgePipeline
}
