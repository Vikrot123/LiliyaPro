package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.orchestrator.RuntimeKnowledgeLifecycleOrchestrator

class DefaultRuntimeKnowledgeLifecyclePipeline(
    private val orchestrator: RuntimeKnowledgeLifecycleOrchestrator
) : RuntimeKnowledgeLifecyclePipeline {

    override fun process(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecyclePipelineResult {

        return RuntimeKnowledgeLifecyclePipelineResult(
            orchestrationResult = orchestrator.process(knowledge)
        )
    }
}
