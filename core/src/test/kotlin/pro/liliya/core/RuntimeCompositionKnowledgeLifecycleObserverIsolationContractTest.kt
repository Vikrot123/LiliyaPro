package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.RuntimeKnowledgeLifecycleObserver
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult

class RuntimeCompositionKnowledgeLifecycleObserverIsolationContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "observer isolation test",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = System.currentTimeMillis()
        )
    }

    @Test
    fun observers_should_not_leak_between_compositions() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        val observer =
            CountingObserver()

        first
            .knowledgeLifecycleComposition()
            .registerLifecycleObserver(observer)

        second
            .knowledgeLifecycleComposition()
            .lifecycleService()
            .processKnowledge(
                knowledge()
            )

        assertEquals(
            0,
            observer.deliveryCount
        )
    }

    private class CountingObserver :
        RuntimeKnowledgeLifecycleObserver {

        var deliveryCount = 0

        override fun onProcessed(
            result: RuntimeKnowledgeLifecycleServiceResult
        ) {
            deliveryCount++
        }
    }
}
