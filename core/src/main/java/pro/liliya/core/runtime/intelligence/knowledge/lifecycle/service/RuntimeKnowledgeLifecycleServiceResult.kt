package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service

import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline.RuntimeKnowledgeLifecyclePipelineResult

data class RuntimeKnowledgeLifecycleServiceResult(
    val status: RuntimeKnowledgeLifecycleProcessingStatus,
    val pipelineResult: RuntimeKnowledgeLifecyclePipelineResult?,
    val error: Throwable?
)
