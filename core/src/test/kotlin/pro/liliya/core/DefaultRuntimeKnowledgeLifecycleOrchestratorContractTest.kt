package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.executor.RuntimeKnowledgeLifecycleExecutionResult
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.executor.RuntimeKnowledgeLifecycleExecutor
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.orchestrator.DefaultRuntimeKnowledgeLifecycleOrchestrator

class DefaultRuntimeKnowledgeLifecycleOrchestratorContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "orchestrator knowledge",
            confidence = 0.8,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = 1L
        )
    }

    @Test
    fun orchestrator_delegates_execution_to_executor() {

        val orchestrator =
            DefaultRuntimeKnowledgeLifecycleOrchestrator(
                object : RuntimeKnowledgeLifecycleExecutor {

                    override fun execute(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleExecutionResult {

                        return RuntimeKnowledgeLifecycleExecutionResult(
                            executed = true
                        )
                    }
                }
            )

        val result =
            orchestrator.process(knowledge())

        assertTrue(
            result.executionResult.executed
        )
    }
    @Test
    fun orchestrator_preserves_successful_execution_result() {
        val orchestrator =
            DefaultRuntimeKnowledgeLifecycleOrchestrator(
                object : RuntimeKnowledgeLifecycleExecutor {
                    override fun execute(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleExecutionResult {
                        return RuntimeKnowledgeLifecycleExecutionResult(
                            executed = true
                        )
                    }
                }
            )

        val result = orchestrator.process(knowledge())

        assertTrue(
            result.executionResult.executed
        )
    }

    @Test
    fun orchestrator_preserves_rejected_execution_result() {
        val orchestrator =
            DefaultRuntimeKnowledgeLifecycleOrchestrator(
                object : RuntimeKnowledgeLifecycleExecutor {
                    override fun execute(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecycleExecutionResult {
                        return RuntimeKnowledgeLifecycleExecutionResult(
                            executed = false
                        )
                    }
                }
            )

        val result = orchestrator.process(knowledge())

        assertTrue(
            !result.executionResult.executed
        )
    }

}
