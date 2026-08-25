package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.orchestrator.RuntimeKnowledgeLifecycleOrchestrationResult
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.orchestrator.RuntimeKnowledgeLifecycleOrchestrator
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.executor.RuntimeKnowledgeLifecycleExecutionResult
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.pipeline.DefaultRuntimeKnowledgeLifecyclePipeline

class DefaultRuntimeKnowledgeLifecyclePipelineContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "pipeline knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun pipeline_delegates_processing_to_orchestrator() {

        val pipeline =
            DefaultRuntimeKnowledgeLifecyclePipeline(
                object : RuntimeKnowledgeLifecycleOrchestrator {

                    override fun process(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleOrchestrationResult {

                        return RuntimeKnowledgeLifecycleOrchestrationResult(
                            RuntimeKnowledgeLifecycleExecutionResult(
                                executed = true
                            )
                        )
                    }
                }
            )

        val result =
            pipeline.process(knowledge())

        assertTrue(
            result.orchestrationResult
                .executionResult
                .executed
        )
    }
    @Test
    fun pipeline_preserves_successful_orchestration_result() {
        val pipeline =
            DefaultRuntimeKnowledgeLifecyclePipeline(
                object : RuntimeKnowledgeLifecycleOrchestrator {
                    override fun process(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleOrchestrationResult {
                        return RuntimeKnowledgeLifecycleOrchestrationResult(
                            RuntimeKnowledgeLifecycleExecutionResult(
                                executed = true
                            )
                        )
                    }
                }
            )

        val result = pipeline.process(knowledge())

        assertTrue(
            result.orchestrationResult
                .executionResult
                .executed
        )
    }

    @Test
    fun pipeline_preserves_rejected_orchestration_result() {
        val pipeline =
            DefaultRuntimeKnowledgeLifecyclePipeline(
                object : RuntimeKnowledgeLifecycleOrchestrator {
                    override fun process(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleOrchestrationResult {
                        return RuntimeKnowledgeLifecycleOrchestrationResult(
                            RuntimeKnowledgeLifecycleExecutionResult(
                                executed = false
                            )
                        )
                    }
                }
            )

        val result = pipeline.process(knowledge())

        assertTrue(
            !result.orchestrationResult
                .executionResult
                .executed
        )
    }

}
