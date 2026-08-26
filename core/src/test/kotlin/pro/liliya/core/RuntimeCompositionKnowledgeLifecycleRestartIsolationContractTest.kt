package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

class RuntimeCompositionKnowledgeLifecycleRestartIsolationContractTest {

    @Test
    fun stop_runtime_keeps_knowledge_lifecycle_composition_owner() {
        val composition =
            DefaultRuntimeComposition()

        val before =
            composition.knowledgeLifecycleComposition()

        composition.stopRuntime()

        val after =
            composition.knowledgeLifecycleComposition()

        assertSame(
            before,
            after
        )
    }

    @Test
    fun separate_runtime_compositions_do_not_share_lifecycle_owner() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first.knowledgeLifecycleComposition(),
            second.knowledgeLifecycleComposition()
        )
    }
    @Test
    fun restart_preserves_knowledge_lifecycle_state_and_history() {
        val composition = DefaultRuntimeComposition()
        val lifecycle = composition.knowledgeLifecycleComposition()

        val knowledge = RuntimeKnowledge(
            statement = "restart knowledge continuity",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = 1L
        )

        val memory = lifecycle.lifecycleMemory()

        memory.create(knowledge)
        memory.revise(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            memory.memory().getLifecycleState(knowledge)
        )

        assertEquals(
            2,
            lifecycle.lifecycleHistoryQuery()
                .transitionCount(knowledge)
        )

        composition.startRuntime()
        composition.stopRuntime()
        composition.startRuntime()

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            lifecycle.lifecycleMemory()
                .memory()
                .getLifecycleState(knowledge)
        )

        assertEquals(
            2,
            lifecycle.lifecycleHistoryQuery()
                .transitionCount(knowledge)
        )

        composition.stopRuntime()
    }

    @Test
    fun prepare_then_start_preserves_knowledge_lifecycle_state_and_history() {
        val composition = DefaultRuntimeComposition()
        val lifecycle = composition.knowledgeLifecycleComposition()

        val memory = lifecycle.lifecycleMemory()
        val history = lifecycle.lifecycleHistoryQuery()

        val knowledge = RuntimeKnowledge(
            statement = "prepare start lifecycle continuity",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = 1L
        )

        memory.create(knowledge)
        memory.revise(knowledge)

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            memory.memory().getLifecycleState(knowledge)
        )

        assertEquals(
            2,
            history.transitionCount(knowledge)
        )

        composition.prepareRuntime()

        assertSame(
            lifecycle,
            composition.knowledgeLifecycleComposition()
        )

        assertSame(
            memory,
            lifecycle.lifecycleMemory()
        )

        assertSame(
            history,
            lifecycle.lifecycleHistoryQuery()
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            memory.memory().getLifecycleState(knowledge)
        )

        assertEquals(
            2,
            history.transitionCount(knowledge)
        )

        composition.startRuntime()

        assertSame(
            lifecycle,
            composition.knowledgeLifecycleComposition()
        )

        assertSame(
            memory,
            lifecycle.lifecycleMemory()
        )

        assertSame(
            history,
            lifecycle.lifecycleHistoryQuery()
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            lifecycle.lifecycleMemory()
                .memory()
                .getLifecycleState(knowledge)
        )

        assertEquals(
            2,
            lifecycle.lifecycleHistoryQuery()
                .transitionCount(knowledge)
        )

        composition.stopRuntime()
    }

}
