package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline.RuntimeKnowledgeLifecyclePipelineResult

interface RuntimeKnowledgeLifecycleService {

    fun processKnowledge(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecyclePipelineResult
}
