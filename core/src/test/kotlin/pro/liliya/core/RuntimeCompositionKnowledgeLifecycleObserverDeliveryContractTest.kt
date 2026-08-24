package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.DefaultRuntimeKnowledgeLifecycleObserver
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleProcessingStatus

class RuntimeCompositionKnowledgeLifecycleObserverDeliveryContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "observer delivery test",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = System.currentTimeMillis()
        )
    }

    @Test
    fun lifecycle_service_delivers_processed_result_to_observer() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val observer =
            DefaultRuntimeKnowledgeLifecycleObserver()

        lifecycle.registerLifecycleObserver(
            observer
        )

        lifecycle
            .lifecycleService()
            .processKnowledge(
                knowledge()
            )

        val result =
            observer.lastProcessedResult()

        assertNotNull(
            result
        )

        assertNotNull(
            result.status
        )
    }
}
