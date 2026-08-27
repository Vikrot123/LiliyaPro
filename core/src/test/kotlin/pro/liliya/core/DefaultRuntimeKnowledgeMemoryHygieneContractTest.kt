package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.hygiene.RuntimeKnowledgeHygieneAction
import pro.liliya.core.runtime.intelligence.knowledge.integration.DefaultRuntimeKnowledgeMemory

class DefaultRuntimeKnowledgeMemoryHygieneContractTest {

    @Test
    fun exact_duplicate_is_not_appended_twice() {
        val memory =
            DefaultRuntimeKnowledgeMemory()

        val first =
            knowledge(
                "runtime stable knowledge",
                0.8,
                1L
            )

        val second =
            first.copy(
                createdAt = 2L
            )

        memory.remember(first)

        val result =
            memory.rememberWithHygiene(
                second
            )

        assertEquals(
            RuntimeKnowledgeHygieneAction.DUPLICATE_SUPPRESSED,
            result.action
        )

        assertEquals(
            first,
            result.retainedKnowledge
        )

        assertEquals(
            1,
            memory.availableKnowledge().size
        )
    }

    @Test
    fun case_and_punctuation_variants_are_normalized_as_duplicates() {
        val memory =
            DefaultRuntimeKnowledgeMemory()

        val first =
            knowledge(
                "Runtime stable operational knowledge.",
                0.8,
                1L
            )

        val second =
            knowledge(
                "runtime STABLE operational knowledge",
                0.8,
                2L
            )

        memory.remember(first)

        val result =
            memory.rememberWithHygiene(
                second
            )

        assertEquals(
            RuntimeKnowledgeHygieneAction.DUPLICATE_SUPPRESSED,
            result.action
        )

        assertEquals(
            1,
            memory.availableKnowledge().size
        )
    }

    @Test
    fun confidence_difference_is_resolved_but_does_not_mutate_existing_lifecycle_entry() {
        val memory =
            DefaultRuntimeKnowledgeMemory()

        val existing =
            knowledge(
                "runtime stable operational knowledge",
                0.7,
                1L
            )

        val candidate =
            knowledge(
                "Runtime stable operational knowledge.",
                0.9,
                2L
            )

        memory.remember(existing)

        val result =
            memory.rememberWithHygiene(
                candidate
            )

        assertEquals(
            RuntimeKnowledgeHygieneAction.CONFLICT_SUPPRESSED,
            result.action
        )

        assertTrue(
            result.conflictResolution?.resolved == true
        )

        assertEquals(
            candidate,
            result.conflictResolution?.selectedKnowledge
        )

        assertEquals(
            existing,
            result.retainedKnowledge
        )

        assertEquals(
            listOf(existing),
            memory.availableKnowledge()
        )
    }

    @Test
    fun lower_confidence_candidate_cannot_replace_existing_knowledge() {
        val memory =
            DefaultRuntimeKnowledgeMemory()

        val existing =
            knowledge(
                "runtime stable operational knowledge",
                0.9,
                1L
            )

        val weaker =
            knowledge(
                "Runtime stable operational knowledge!",
                0.5,
                2L
            )

        memory.remember(existing)

        val result =
            memory.rememberWithHygiene(
                weaker
            )

        assertTrue(
            result.conflictResolution?.resolved == true
        )

        assertEquals(
            existing,
            result.conflictResolution?.selectedKnowledge
        )

        assertEquals(
            listOf(existing),
            memory.availableKnowledge()
        )
    }

    @Test
    fun unrelated_knowledge_is_still_added_normally() {
        val memory =
            DefaultRuntimeKnowledgeMemory()

        memory.remember(
            knowledge(
                "runtime operational knowledge",
                0.8,
                1L
            )
        )

        val result =
            memory.rememberWithHygiene(
                knowledge(
                    "network recovery knowledge",
                    0.8,
                    2L
                )
            )

        assertEquals(
            RuntimeKnowledgeHygieneAction.ADDED,
            result.action
        )

        assertEquals(
            2,
            memory.availableKnowledge().size
        )

        assertFalse(
            result.conflictResolution?.resolved == true
        )
    }

    private fun knowledge(
        statement: String,
        confidence: Double,
        createdAt: Long
    ) =
        RuntimeKnowledge(
            statement = statement,
            confidence = confidence,
            source = RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = createdAt
        )
}
