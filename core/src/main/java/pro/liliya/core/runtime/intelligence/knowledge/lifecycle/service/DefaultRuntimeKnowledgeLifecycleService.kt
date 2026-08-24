package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline.RuntimeKnowledgeLifecyclePipeline

class DefaultRuntimeKnowledgeLifecycleService(
    private val pipeline: RuntimeKnowledgeLifecyclePipeline
) : RuntimeKnowledgeLifecycleService {

    override fun processKnowledge(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleServiceResult {

        return try {
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

            RuntimeKnowledgeLifecycleServiceResult(
                status = status,
                pipelineResult = pipelineResult,
                error = null
            )

        } catch (error: Throwable) {

            RuntimeKnowledgeLifecycleServiceResult(
                status = RuntimeKnowledgeLifecycleProcessingStatus.FAILED,
                pipelineResult = null,
                error = error
            )
        }
    }
}
