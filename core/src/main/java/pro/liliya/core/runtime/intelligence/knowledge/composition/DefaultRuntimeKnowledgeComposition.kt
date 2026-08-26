package pro.liliya.core.runtime.intelligence.knowledge.composition

import pro.liliya.core.runtime.intelligence.knowledge.DefaultRuntimeKnowledgeExtractor
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeExtractor
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.DefaultRuntimeKnowledgePipeline
import pro.liliya.core.runtime.intelligence.knowledge.pipeline.RuntimeKnowledgePipeline

class DefaultRuntimeKnowledgeComposition : RuntimeKnowledgeComposition {

    private val knowledgeExtractor: RuntimeKnowledgeExtractor =
        DefaultRuntimeKnowledgeExtractor()

    private val knowledgePipeline: RuntimeKnowledgePipeline =
        DefaultRuntimeKnowledgePipeline(
            knowledgeExtractor = knowledgeExtractor
        )

    override fun knowledgeExtractor(): RuntimeKnowledgeExtractor {
        return knowledgeExtractor
    }

    override fun knowledgePipeline(): RuntimeKnowledgePipeline {
        return knowledgePipeline
    }
}
