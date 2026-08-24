package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource

class RuntimeCompositionKnowledgeLifecycleServiceResetOwnershipContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "service reset ownership test",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = System.currentTimeMillis()
        )
    }

    @Test
    fun reset_recreates_only_lifecycle_service() {
        val composition = DefaultRuntimeComposition()

        val lifecycle =
            composition.knowledgeLifecycleComposition()

        val beforeService =
            lifecycle.lifecycleService()

        val beforeMemory =
            lifecycle.lifecycleMemory()

        val beforeHistoryQuery =
            lifecycle.lifecycleHistoryQuery()

        lifecycle.reset()

        val afterService =
            lifecycle.lifecycleService()

        val afterMemory =
            lifecycle.lifecycleMemory()

        val afterHistoryQuery =
            lifecycle.lifecycleHistoryQuery()

        assertNotSame(
            beforeService,
            afterService
        )

        assertSame(
            beforeMemory,
            afterMemory
        )

        assertSame(
            beforeHistoryQuery,
            afterHistoryQuery
        )
    }

    @Test
    fun reset_keeps_history_after_service_recreation() {
        val composition = DefaultRuntimeComposition()

        val lifecycle =
            composition.knowledgeLifecycleComposition()

        val knowledge = knowledge()

        lifecycle
            .lifecycleMemory()
            .create(knowledge)

        lifecycle
            .lifecycleMemory()
            .revise(knowledge)

        lifecycle.reset()

        val count =
            lifecycle
                .lifecycleHistoryQuery()
                .transitionCount(knowledge)

        assertEquals(
            2,
            count
        )
    }
}
