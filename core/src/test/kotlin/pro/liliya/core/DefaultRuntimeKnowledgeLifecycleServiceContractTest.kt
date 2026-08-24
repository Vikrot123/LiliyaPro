package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline.RuntimeKnowledgeLifecyclePipeline
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline.RuntimeKnowledgeLifecyclePipelineResult
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.orchestrator.RuntimeKnowledgeLifecycleOrchestrationResult
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.executor.RuntimeKnowledgeLifecycleExecutionResult
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.DefaultRuntimeKnowledgeLifecycleService
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleProcessingStatus

class DefaultRuntimeKnowledgeLifecycleServiceContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "service knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun service_delegates_processing_to_pipeline() {

        val service =
            DefaultRuntimeKnowledgeLifecycleService(
                object : RuntimeKnowledgeLifecyclePipeline {

                    override fun process(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecyclePipelineResult {

                        return RuntimeKnowledgeLifecyclePipelineResult(
                            RuntimeKnowledgeLifecycleOrchestrationResult(
                                RuntimeKnowledgeLifecycleExecutionResult(
                                    executed = true
                                )
                            )
                        )
                    }
                }
            )

        val result =
            service.processKnowledge(knowledge())

        assertTrue(
            result.status ==
                RuntimeKnowledgeLifecycleProcessingStatus.EXECUTED
        )
    }

    @Test
    fun service_reports_failed_processing() {

        val service =
            DefaultRuntimeKnowledgeLifecycleService(
                object : RuntimeKnowledgeLifecyclePipeline {

                    override fun process(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecyclePipelineResult {

                        throw IllegalStateException("pipeline failed")
                    }
                }
            )

        val result =
            service.processKnowledge(knowledge())

        assertTrue(
            result.status ==
                RuntimeKnowledgeLifecycleProcessingStatus.FAILED
        )

        assertTrue(
            result.error != null
        )
    }

}
