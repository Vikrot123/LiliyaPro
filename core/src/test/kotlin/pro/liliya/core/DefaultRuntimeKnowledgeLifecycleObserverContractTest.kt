package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.DefaultRuntimeKnowledgeLifecycleObserver
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleProcessingStatus
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult

class DefaultRuntimeKnowledgeLifecycleObserverContractTest {

    @Test
    fun observer_receives_processed_result() {

        val observer =
            DefaultRuntimeKnowledgeLifecycleObserver()

        val result =
            RuntimeKnowledgeLifecycleServiceResult(
                status =
                    RuntimeKnowledgeLifecycleProcessingStatus.EXECUTED,
                pipelineResult = null,
                error = null
            )

        observer.onProcessed(result)

        assertEquals(
            result,
            observer.lastProcessedResult()
        )
    }
}
