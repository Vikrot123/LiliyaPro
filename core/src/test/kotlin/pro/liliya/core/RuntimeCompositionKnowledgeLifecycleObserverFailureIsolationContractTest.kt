package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.RuntimeKnowledgeLifecycleObserver

class RuntimeCompositionKnowledgeLifecycleObserverFailureIsolationContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "observer failure isolation test",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = System.currentTimeMillis()
        )
    }

    @Test
    fun failing_observer_should_not_block_other_observers() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val failingObserver =
            object : RuntimeKnowledgeLifecycleObserver {

                override fun onProcessed(
                    result: pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult
                ) {
                    throw IllegalStateException(
                        "observer failure"
                    )
                }
            }

        val healthyObserver =
            CountingObserver()

        lifecycle.registerLifecycleObserver(
            failingObserver
        )

        lifecycle.registerLifecycleObserver(
            healthyObserver
        )

        lifecycle
            .lifecycleService()
            .processKnowledge(
                knowledge()
            )

        assertEquals(
            1,
            healthyObserver.deliveryCount
        )
    }

    @Test
    fun multiple_failing_observers_should_not_block_delivery() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val firstFailing =
            FailingObserver()

        val secondFailing =
            FailingObserver()

        val healthy =
            CountingObserver()

        lifecycle.registerLifecycleObserver(firstFailing)
        lifecycle.registerLifecycleObserver(secondFailing)
        lifecycle.registerLifecycleObserver(healthy)

        lifecycle
            .lifecycleService()
            .processKnowledge(
                knowledge()
            )

        assertEquals(
            1,
            healthy.deliveryCount
        )
    }


    private class FailingObserver :
        RuntimeKnowledgeLifecycleObserver {

        override fun onProcessed(
            result: pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult
        ) {
            throw IllegalStateException(
                "observer failure"
            )
        }
    }

    private class CountingObserver :
        RuntimeKnowledgeLifecycleObserver {

        var deliveryCount = 0

        override fun onProcessed(
            result: pro.liliya.core.runtime.intelligence.knowledge.lifecycle.service.RuntimeKnowledgeLifecycleServiceResult
        ) {
            deliveryCount++
        }
    }
}
