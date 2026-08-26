package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertSame

import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.observer.DefaultRuntimeKnowledgeLifecycleObserver

class RuntimeCompositionKnowledgeLifecyclePrepareIsolationContractTest {

    @Test
    fun prepare_runtime_keeps_knowledge_lifecycle_composition_owner() {
        val composition =
            DefaultRuntimeComposition()

        val before =
            composition.knowledgeLifecycleComposition()

        composition.prepareRuntime()

        val after =
            composition.knowledgeLifecycleComposition()

        assertSame(
            before,
            after
        )
    }
    @Test
    fun prepare_runtime_preserves_knowledge_lifecycle_history() {
        val composition = DefaultRuntimeComposition()
        val lifecycle = composition.knowledgeLifecycleComposition()

        val knowledge = RuntimeKnowledge(
            statement = "prepare knowledge continuity",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = 1L
        )

        val beforeService = lifecycle.lifecycleService()
        val memory = lifecycle.lifecycleMemory()
        val historyQuery = lifecycle.lifecycleHistoryQuery()

        memory.create(knowledge)
        memory.revise(knowledge)

        assertEquals(
            2,
            historyQuery.transitionCount(knowledge)
        )

        composition.prepareRuntime()

        val afterService = lifecycle.lifecycleService()

        assertNotSame(beforeService, afterService)
        assertSame(memory, lifecycle.lifecycleMemory())
        assertSame(historyQuery, lifecycle.lifecycleHistoryQuery())

        assertEquals(
            2,
            lifecycle.lifecycleHistoryQuery()
                .transitionCount(knowledge)
        )
    }

    @Test
    fun prepare_preserves_history_wiring_for_new_transition() {
        val composition = DefaultRuntimeComposition()
        val lifecycle = composition.knowledgeLifecycleComposition()

        val knowledge = RuntimeKnowledge(
            statement = "prepare history wiring",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = 1L
        )

        val memory = lifecycle.lifecycleMemory()

        memory.create(knowledge)

        assertEquals(
            1,
            lifecycle.lifecycleHistoryQuery()
                .transitionCount(knowledge)
        )

        composition.prepareRuntime()

        lifecycle.lifecycleMemory()
            .revise(knowledge)

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
    }

    @Test
    fun prepare_preserves_history_for_new_lifecycle_composition() {
        val composition = DefaultRuntimeComposition()
        val lifecycleBefore = composition.knowledgeLifecycleComposition()

        val knowledge = RuntimeKnowledge(
            statement = "prepare history continuity",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = 1L
        )

        lifecycleBefore.lifecycleMemory()
            .create(knowledge)

        lifecycleBefore.lifecycleMemory()
            .revise(knowledge)

        assertEquals(
            2,
            lifecycleBefore.lifecycleHistoryQuery()
                .transitionCount(knowledge)
        )

        composition.prepareRuntime()

        val lifecycleAfter = composition.knowledgeLifecycleComposition()

        assertEquals(
            2,
            lifecycleAfter.lifecycleHistoryQuery()
                .transitionCount(knowledge)
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            lifecycleAfter.lifecycleMemory()
                .memory()
                .getLifecycleState(knowledge)
        )
    }

    @Test
    fun prepare_preserves_lifecycle_composition_identity() {
        val composition = DefaultRuntimeComposition()

        val lifecycleBefore =
            composition.knowledgeLifecycleComposition()

        val memoryBefore =
            lifecycleBefore.lifecycleMemory()

        val historyBefore =
            lifecycleBefore.lifecycleHistoryQuery()

        composition.prepareRuntime()

        val lifecycleAfter =
            composition.knowledgeLifecycleComposition()

        assertSame(
            lifecycleBefore,
            lifecycleAfter
        )

        assertSame(
            memoryBefore,
            lifecycleAfter.lifecycleMemory()
        )

        assertSame(
            historyBefore,
            lifecycleAfter.lifecycleHistoryQuery()
        )
    }

    @Test
    fun prepare_detaches_old_lifecycle_observer_delivery() {
        val composition = DefaultRuntimeComposition()
        val lifecycle = composition.knowledgeLifecycleComposition()

        val observer = DefaultRuntimeKnowledgeLifecycleObserver()

        lifecycle.registerLifecycleObserver(observer)

        lifecycle
            .lifecycleService()
            .processKnowledge(
                RuntimeKnowledge(
                    statement = "prepare observer detachment",
                    confidence = 0.9,
                    source = RuntimeKnowledgeSource.EXPERIENCE,
                    createdAt = 1L
                )
            )

        val beforePrepare = observer.lastProcessedResult()

        assertNotNull(beforePrepare)

        composition.prepareRuntime()

        lifecycle
            .lifecycleService()
            .processKnowledge(
                RuntimeKnowledge(
                    statement = "prepare observer detachment after reset",
                    confidence = 0.8,
                    source = RuntimeKnowledgeSource.EXPERIENCE,
                    createdAt = 2L
                )
            )

        assertSame(
            beforePrepare,
            observer.lastProcessedResult()
        )
    }

    @Test
    fun prepare_preserves_full_lifecycle_boundary() {
        val composition = DefaultRuntimeComposition()
        val lifecycleBefore = composition.knowledgeLifecycleComposition()
        val memoryBefore = lifecycleBefore.lifecycleMemory()
        val historyBefore = lifecycleBefore.lifecycleHistoryQuery()
        val serviceBefore = lifecycleBefore.lifecycleService()

        val oldObserver = DefaultRuntimeKnowledgeLifecycleObserver()
        lifecycleBefore.registerLifecycleObserver(oldObserver)

        val knowledge = RuntimeKnowledge(
            statement = "full lifecycle prepare boundary",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = 1L
        )

        serviceBefore.processKnowledge(knowledge)

        assertNotNull(oldObserver.lastProcessedResult())

        lifecycleBefore.lifecycleMemory()
            .create(knowledge)

        lifecycleBefore.lifecycleMemory()
            .revise(knowledge)

        assertEquals(
            2,
            historyBefore.transitionCount(knowledge)
        )

        composition.prepareRuntime()

        val lifecycleAfter = composition.knowledgeLifecycleComposition()
        val serviceAfter = lifecycleAfter.lifecycleService()

        assertSame(lifecycleBefore, lifecycleAfter)
        assertSame(memoryBefore, lifecycleAfter.lifecycleMemory())
        assertSame(historyBefore, lifecycleAfter.lifecycleHistoryQuery())
        assertNotSame(serviceBefore, serviceAfter)

        assertEquals(
            2,
            lifecycleAfter.lifecycleHistoryQuery()
                .transitionCount(knowledge)
        )

        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            lifecycleAfter.lifecycleMemory()
                .memory()
                .getLifecycleState(knowledge)
        )

        val oldResult = oldObserver.lastProcessedResult()

        serviceAfter.processKnowledge(
            RuntimeKnowledge(
                statement = "full lifecycle prepare boundary after reset",
                confidence = 0.8,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 2L
            )
        )

        assertSame(oldResult, oldObserver.lastProcessedResult())
    }

    @Test
    fun prepare_twice_preserves_lifecycle_state_and_history_identity() {
        val composition = DefaultRuntimeComposition()
        val lifecycle = composition.knowledgeLifecycleComposition()

        val memory = lifecycle.lifecycleMemory()
        val history = lifecycle.lifecycleHistoryQuery()

        val knowledge = RuntimeKnowledge(
            statement = "repeated prepare continuity",
            confidence = 0.9,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = 1L
        )

        memory.create(knowledge)
        memory.revise(knowledge)

        assertEquals(2, history.transitionCount(knowledge))

        val serviceBeforeFirstPrepare = lifecycle.lifecycleService()

        composition.prepareRuntime()

        val serviceAfterFirstPrepare = lifecycle.lifecycleService()

        assertSame(lifecycle, composition.knowledgeLifecycleComposition())
        assertSame(memory, lifecycle.lifecycleMemory())
        assertSame(history, lifecycle.lifecycleHistoryQuery())
        assertNotSame(serviceBeforeFirstPrepare, serviceAfterFirstPrepare)

        assertEquals(2, history.transitionCount(knowledge))
        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            memory.memory().getLifecycleState(knowledge)
        )

        val serviceBeforeSecondPrepare = lifecycle.lifecycleService()

        composition.prepareRuntime()

        val serviceAfterSecondPrepare = lifecycle.lifecycleService()

        assertSame(lifecycle, composition.knowledgeLifecycleComposition())
        assertSame(memory, lifecycle.lifecycleMemory())
        assertSame(history, lifecycle.lifecycleHistoryQuery())
        assertNotSame(serviceBeforeSecondPrepare, serviceAfterSecondPrepare)
        assertNotSame(serviceAfterFirstPrepare, serviceAfterSecondPrepare)

        assertEquals(2, history.transitionCount(knowledge))
        assertEquals(
            RuntimeKnowledgeLifecycleState.REVIEW,
            memory.memory().getLifecycleState(knowledge)
        )
    }

}
