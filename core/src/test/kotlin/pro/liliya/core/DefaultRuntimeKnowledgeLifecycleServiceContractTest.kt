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
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.registry.RuntimeKnowledgeLifecycleObserverRegistry
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.RuntimeKnowledgeLifecycleObserver

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


    @Test
    fun service_notifies_observer_registry_after_processing() {

        val registry =
            object : RuntimeKnowledgeLifecycleObserverRegistry {

                var received:
                    RuntimeKnowledgeLifecycleServiceResult? = null

                override fun register(
                    observer: RuntimeKnowledgeLifecycleObserver
                ) {
                }

                override fun unregister(
                    observer: RuntimeKnowledgeLifecycleObserver
                ) {
                }

                override fun notify(
                    result: RuntimeKnowledgeLifecycleServiceResult
                ) {
                    received = result
                }
            }

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
                },
                registry
            )

        val result =
            service.processKnowledge(knowledge())

        assertTrue(
            result.status ==
                RuntimeKnowledgeLifecycleProcessingStatus.EXECUTED
        )

        assertTrue(
            registry.received == result
        )
    }

    @Test
    fun service_notifies_observer_registry_after_failed_processing() {

        val registry = object : RuntimeKnowledgeLifecycleObserverRegistry {

            var received: RuntimeKnowledgeLifecycleServiceResult? = null

            override fun register(
                observer: RuntimeKnowledgeLifecycleObserver
            ) {
            }

            override fun unregister(
                observer: RuntimeKnowledgeLifecycleObserver
            ) {
            }

            override fun notify(
                result: RuntimeKnowledgeLifecycleServiceResult
            ) {
                received = result
            }
        }

        val service =
            DefaultRuntimeKnowledgeLifecycleService(
                object : RuntimeKnowledgeLifecyclePipeline {

                    override fun process(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecyclePipelineResult {

                        throw IllegalStateException("pipeline failed")
                    }
                },
                registry
            )

        val result = service.processKnowledge(knowledge())

        assertTrue(
            result.status ==
                RuntimeKnowledgeLifecycleProcessingStatus.FAILED
        )

        assertTrue(
            registry.received == result
        )
    }

    @Test
    fun service_preserves_pipeline_result_when_executed() {
        val pipelineResult =
            RuntimeKnowledgeLifecyclePipelineResult(
                RuntimeKnowledgeLifecycleOrchestrationResult(
                    RuntimeKnowledgeLifecycleExecutionResult(
                        executed = true
                    )
                )
            )

        val service =
            DefaultRuntimeKnowledgeLifecycleService(
                object : RuntimeKnowledgeLifecyclePipeline {
                    override fun process(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecyclePipelineResult {
                        return pipelineResult
                    }
                }
            )

        val result = service.processKnowledge(knowledge())

        assertTrue(
            result.status ==
                RuntimeKnowledgeLifecycleProcessingStatus.EXECUTED
        )
        assertTrue(result.pipelineResult === pipelineResult)
        assertTrue(result.error == null)
    }

    @Test
    fun service_preserves_pipeline_result_when_skipped() {
        val pipelineResult =
            RuntimeKnowledgeLifecyclePipelineResult(
                RuntimeKnowledgeLifecycleOrchestrationResult(
                    RuntimeKnowledgeLifecycleExecutionResult(
                        executed = false
                    )
                )
            )

        val service =
            DefaultRuntimeKnowledgeLifecycleService(
                object : RuntimeKnowledgeLifecyclePipeline {
                    override fun process(
                        knowledge: RuntimeKnowledge
                    ): RuntimeKnowledgeLifecyclePipelineResult {
                        return pipelineResult
                    }
                }
            )

        val result = service.processKnowledge(knowledge())

        assertTrue(
            result.status ==
                RuntimeKnowledgeLifecycleProcessingStatus.SKIPPED
        )
        assertTrue(result.pipelineResult === pipelineResult)
        assertTrue(result.error == null)
    }

    @Test
    fun service_clears_pipeline_result_when_processing_fails() {
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

        val result = service.processKnowledge(knowledge())

        assertTrue(
            result.status ==
                RuntimeKnowledgeLifecycleProcessingStatus.FAILED
        )
        assertTrue(result.pipelineResult == null)
        assertTrue(result.error != null)
    }

}
