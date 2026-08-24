package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline.RuntimeKnowledgeLifecyclePipeline
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline.RuntimeKnowledgeLifecyclePipelineResult

class DefaultRuntimeKnowledgeLifecycleService(
    private val pipeline: RuntimeKnowledgeLifecyclePipeline
) : RuntimeKnowledgeLifecycleService {

    override fun processKnowledge(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecyclePipelineResult {

        return pipeline.process(knowledge)
    }
}
