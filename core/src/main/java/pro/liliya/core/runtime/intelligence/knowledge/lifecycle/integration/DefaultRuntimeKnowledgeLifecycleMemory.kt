package pro.liliya.core.runtime.intelligence.knowledge.lifecycle.integration

import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.hygiene.RuntimeKnowledgeHygieneAction
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.integration.RuntimeKnowledgeMemory
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.DefaultRuntimeKnowledgeLifecycleManager
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.RuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.history.DefaultRuntimeKnowledgeLifecycleHistoryStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.query.DefaultRuntimeKnowledgeLifecycleStateQuery
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.DefaultRuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.state.RuntimeKnowledgeLifecycleStateStore
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.DefaultRuntimeKnowledgeLifecycleTransitionManager
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.transition.RuntimeKnowledgeLifecycleTransitionManager
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.supersession.RuntimeKnowledgeSupersessionHistory

class DefaultRuntimeKnowledgeLifecycleMemory(
    private val stateStore: RuntimeKnowledgeLifecycleStateStore =
        DefaultRuntimeKnowledgeLifecycleStateStore(),
    private val historyStore: RuntimeKnowledgeLifecycleHistoryStore =
        DefaultRuntimeKnowledgeLifecycleHistoryStore(),
    private val transitionManager: RuntimeKnowledgeLifecycleTransitionManager =
        DefaultRuntimeKnowledgeLifecycleTransitionManager(
            DefaultRuntimeKnowledgeLifecycleStateQuery(
                stateStore
            ),
            stateStore,
            historyStore
        ),
    private val knowledgeMemory: RuntimeKnowledgeMemory =
        DefaultRuntimeKnowledgeMemory(
            lifecycleStateStore = stateStore
        ),
    private val supersessionHistory:
        RuntimeKnowledgeSupersessionHistory? = null
) : RuntimeKnowledgeLifecycleMemory {

    private val memory = knowledgeMemory

    private val lifecycle =
        DefaultRuntimeKnowledgeLifecycleManager()

    private val mutationLock = Any()

    override fun create(
        knowledge: RuntimeKnowledge
    ) {
        synchronized(mutationLock) {
            createLocked(
                knowledge
            )
        }
    }

    private fun createLocked(
        knowledge: RuntimeKnowledge
    ) {
        val hygiene =
            memory.rememberWithHygiene(
                knowledge
            )

        val added =
            hygiene.action ==
                RuntimeKnowledgeHygieneAction.ADDED

        val exactExistingDuplicate =
            hygiene.action ==
                RuntimeKnowledgeHygieneAction.DUPLICATE_SUPPRESSED &&
                hygiene.retainedKnowledge == knowledge

        val strongerConflictCandidate =
            hygiene.action ==
                RuntimeKnowledgeHygieneAction.CONFLICT_SUPPRESSED &&
                hygiene.conflictResolution?.resolved == true &&
                hygiene.conflictResolution.selectedKnowledge == knowledge

        when {
            added ->
                createAddedKnowledge(
                    knowledge
                )

            exactExistingDuplicate ->
                transitionManager.transition(
                    knowledge,
                    lifecycle.create(
                        knowledge
                    ).state
                )

            strongerConflictCandidate ->
                supersede(
                    existing = hygiene.retainedKnowledge,
                    candidate = knowledge
                )

            else ->
                return
        }
    }

    private fun createAddedKnowledge(
        knowledge: RuntimeKnowledge
    ) {
        try {
            val transitioned =
                transitionManager.transition(
                    knowledge,
                    lifecycle.create(
                        knowledge
                    ).state
                )

            if (!transitioned) {
                memory.forget(
                    knowledge
                )
            }
        } catch (error: Throwable) {
            try {
                memory.forget(
                    knowledge
                )
            } catch (rollbackError: Throwable) {
                error.addSuppressed(
                    rollbackError
                )
            }

            throw error
        }
    }

    private fun supersede(
        existing: RuntimeKnowledge,
        candidate: RuntimeKnowledge
    ) {
        val originalState =
            stateStore.getState(
                existing
            ) ?: return

        if (
            originalState ==
                RuntimeKnowledgeLifecycleState.ARCHIVED
        ) {
            return
        }

        val originalHistorySize =
            historyStore
                .history(existing)
                .size

        var existingRemoved = false
        var candidateAdded = false

        try {
            when (originalState) {
                RuntimeKnowledgeLifecycleState.ACTIVE -> {
                    if (
                        !transitionManager.transition(
                            existing,
                            RuntimeKnowledgeLifecycleState.REVIEW
                        )
                    ) {
                        return
                    }

                    if (
                        !transitionManager.transition(
                            existing,
                            RuntimeKnowledgeLifecycleState.ARCHIVED
                        )
                    ) {
                        rollbackExistingLifecycle(
                            knowledge = existing,
                            state = originalState,
                            historySize = originalHistorySize
                        )
                        return
                    }
                }

                RuntimeKnowledgeLifecycleState.REVIEW -> {
                    if (
                        !transitionManager.transition(
                            existing,
                            RuntimeKnowledgeLifecycleState.ARCHIVED
                        )
                    ) {
                        return
                    }
                }

                RuntimeKnowledgeLifecycleState.DEPRECATED ->
                    return

                RuntimeKnowledgeLifecycleState.ARCHIVED ->
                    return
            }

            memory.forget(
                existing
            )
            existingRemoved = true

            val candidateHygiene =
                memory.rememberWithHygiene(
                    candidate
                )

            if (
                candidateHygiene.action !=
                    RuntimeKnowledgeHygieneAction.ADDED
            ) {
                rollbackSupersession(
                    existing = existing,
                    candidate = candidate,
                    originalState = originalState,
                    originalHistorySize = originalHistorySize,
                    existingRemoved = existingRemoved,
                    candidateAdded = false
                )
                return
            }

            candidateAdded = true

            val activated =
                transitionManager.transition(
                    candidate,
                    RuntimeKnowledgeLifecycleState.ACTIVE
                )

            if (activated) {
                supersessionHistory?.record(
                    previousKnowledge = existing,
                    replacementKnowledge = candidate
                )
            }

            if (!activated) {
                rollbackSupersession(
                    existing = existing,
                    candidate = candidate,
                    originalState = originalState,
                    originalHistorySize = originalHistorySize,
                    existingRemoved = existingRemoved,
                    candidateAdded = candidateAdded
                )
            }
        } catch (error: Throwable) {
            try {
                rollbackSupersession(
                    existing = existing,
                    candidate = candidate,
                    originalState = originalState,
                    originalHistorySize = originalHistorySize,
                    existingRemoved = existingRemoved,
                    candidateAdded = candidateAdded
                )
            } catch (rollbackError: Throwable) {
                error.addSuppressed(
                    rollbackError
                )
            }

            throw error
        }
    }

    private fun rollbackSupersession(
        existing: RuntimeKnowledge,
        candidate: RuntimeKnowledge,
        originalState: RuntimeKnowledgeLifecycleState,
        originalHistorySize: Int,
        existingRemoved: Boolean,
        candidateAdded: Boolean
    ) {
        if (candidateAdded) {
            memory.forget(
                candidate
            )
        }

        if (existingRemoved) {
            memory.remember(
                existing
            )
        }

        rollbackExistingLifecycle(
            knowledge = existing,
            state = originalState,
            historySize = originalHistorySize
        )
    }

    private fun rollbackExistingLifecycle(
        knowledge: RuntimeKnowledge,
        state: RuntimeKnowledgeLifecycleState,
        historySize: Int
    ) {
        val appended =
            historyStore
                .history(knowledge)
                .drop(historySize)
                .asReversed()

        appended.forEach { entry ->
            historyStore.removeLast(
                knowledge,
                entry
            )
        }

        stateStore.setState(
            knowledge,
            state
        )
    }

    override fun activate(
        knowledge: RuntimeKnowledge
    ) {
        transitionManager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ACTIVE
        )
    }

    override fun revise(
        knowledge: RuntimeKnowledge
    ) {
        transitionManager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.REVIEW
        )
    }

    override fun archive(
        knowledge: RuntimeKnowledge
    ) {
        transitionManager.transition(
            knowledge,
            RuntimeKnowledgeLifecycleState.ARCHIVED
        )
    }

    override fun memory(): RuntimeKnowledgeMemory {
        return memory
    }
}
