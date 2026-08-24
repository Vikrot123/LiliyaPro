package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry.RuntimeKnowledgeLifecycleObserverRegistry
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline.RuntimeKnowledgeLifecyclePipeline

class DefaultRuntimeKnowledgeLifecycleService(
    private val pipeline: RuntimeKnowledgeLifecyclePipeline,
    private val observerRegistry: RuntimeKnowledgeLifecycleObserverRegistry? = null
) : RuntimeKnowledgeLifecycleService {

    override fun processKnowledge(
        knowledge: RuntimeKnowledge
    ): RuntimeKnowledgeLifecycleServiceResult {

        return try {
            val pipelineResult =
                pipeline.process(knowledge)

            val status =
                if (
                    pipelineResult.orchestrationResult
                        .executionResult
                        .executed
                ) {
                    RuntimeKnowledgeLifecycleProcessingStatus.EXECUTED
                } else {
                    RuntimeKnowledgeLifecycleProcessingStatus.SKIPPED
                }

            val result =
                RuntimeKnowledgeLifecycleServiceResult(
                    status = status,
                    pipelineResult = pipelineResult,
                    error = null
                )

            observerRegistry?.notify(result)

            result

        } catch (error: Throwable) {

            val result =
                RuntimeKnowledgeLifecycleServiceResult(
                    status =
                        RuntimeKnowledgeLifecycleProcessingStatus.FAILED,
                    pipelineResult = null,
                    error = error
                )

            observerRegistry?.notify(result)

            result
        }
    }
}
