package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource

class RuntimeCompositionKnowledgeLifecycleHistoryIsolationContractTest {

    private fun knowledge(): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = "history isolation test",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = System.currentTimeMillis()
        )
    }

    @Test
    fun separate_compositions_do_not_share_history() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        val knowledge = knowledge()

        first.knowledgeLifecycleComposition()
            .lifecycleMemory()
            .create(knowledge)

        first.knowledgeLifecycleComposition()
            .lifecycleMemory()
            .revise(knowledge)

        val firstCount =
            first.knowledgeLifecycleComposition()
                .lifecycleHistoryQuery()
                .transitionCount(knowledge)

        val secondCount =
            second.knowledgeLifecycleComposition()
                .lifecycleHistoryQuery()
                .transitionCount(knowledge)

        assertEquals(2, firstCount)
        assertEquals(0, secondCount)
    }

    @Test
    fun compositions_own_independent_history_query_instances() {
        val first = DefaultRuntimeComposition()
        val second = DefaultRuntimeComposition()

        assertNotSame(
            first.knowledgeLifecycleComposition()
                .lifecycleHistoryQuery(),

            second.knowledgeLifecycleComposition()
                .lifecycleHistoryQuery()
        )
    }
}
