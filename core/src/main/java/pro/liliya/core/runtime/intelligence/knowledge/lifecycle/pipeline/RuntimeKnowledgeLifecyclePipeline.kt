package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge

interface RuntimeKnowledgeLifecyclePipeline {

    fun process(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecyclePipelineResult
}
