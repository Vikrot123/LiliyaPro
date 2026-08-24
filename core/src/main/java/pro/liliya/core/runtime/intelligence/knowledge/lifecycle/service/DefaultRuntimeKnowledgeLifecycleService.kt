package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline.RuntimeKnowledgeLifecyclePipeline

class DefaultRuntimeKnowledgeLifecycleService(
    private val pipeline: RuntimeKnowledgeLifecyclePipeline
) : RuntimeKnowledgeLifecycleService {

    override fun processKnowledge(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleServiceResult {

        val pipelineResult =
            pipeline.process(knowledge)

        val status =
            if (pipelineResult.orchestrationResult
                .executionResult
                .executed
            ) {
                RuntimeKnowledgeLifecycleProcessingStatus.EXECUTED
            } else {
                RuntimeKnowledgeLifecycleProcessingStatus.SKIPPED
            }

        return RuntimeKnowledgeLifecycleServiceResult(
            status = status,
            pipelineResult = pipelineResult
        )
    }
}
