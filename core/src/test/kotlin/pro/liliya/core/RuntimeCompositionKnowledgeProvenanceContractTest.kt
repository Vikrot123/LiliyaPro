package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.lifecycle.RuntimeKnowledgeLifecycleState

class RuntimeCompositionKnowledgeProvenanceContractTest {

    @Test
    fun composition_owns_stable_provenance_query() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        assertSame(
            lifecycle.provenanceQuery(),
            lifecycle.provenanceQuery()
        )
    }

    @Test
    fun real_supersession_exposes_complete_version_provenance() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycleComposition =
            composition
                .knowledgeLifecycleComposition()

        val lifecycle =
            lifecycleComposition
                .lifecycleMemory()

        val first =
            knowledge(
                "runtime provenance operational knowledge",
                0.60,
                RuntimeKnowledgeSource.EXPERIENCE,
                1L
            )

        val second =
            knowledge(
                "Runtime provenance operational knowledge.",
                0.80,
                RuntimeKnowledgeSource.REFLECTION,
                2L
            )

        val third =
            knowledge(
                "RUNTIME provenance operational knowledge!",
                0.95,
                RuntimeKnowledgeSource.CONSOLIDATION,
                3L
            )

        lifecycle.create(first)
        lifecycle.create(second)
        lifecycle.create(third)

        val provenance =
            lifecycleComposition
                .provenanceQuery()
                .provenance(first)

        assertEquals(
            listOf(
                first,
                second,
                third
            ),
            provenance.chain
        )

        assertEquals(
            third,
            provenance.currentKnowledge
        )

        assertEquals(
            listOf(
                RuntimeKnowledgeSource.EXPERIENCE,
                RuntimeKnowledgeSource.REFLECTION,
                RuntimeKnowledgeSource.CONSOLIDATION
            ),
            provenance.steps.map {
                it.source
            }
        )

        assertEquals(
            listOf(
                RuntimeKnowledgeLifecycleState.ACTIVE,
                RuntimeKnowledgeLifecycleState.REVIEW,
                RuntimeKnowledgeLifecycleState.ARCHIVED
            ),
            provenance
                .steps[0]
                .lifecycleHistory
                .map {
                    it.to
                }
        )

        assertEquals(
            listOf(
                RuntimeKnowledgeLifecycleState.ACTIVE,
                RuntimeKnowledgeLifecycleState.REVIEW,
                RuntimeKnowledgeLifecycleState.ARCHIVED
            ),
            provenance
                .steps[1]
                .lifecycleHistory
                .map {
                    it.to
                }
        )

        assertEquals(
            listOf(
                RuntimeKnowledgeLifecycleState.ACTIVE
            ),
            provenance
                .steps[2]
                .lifecycleHistory
                .map {
                    it.to
                }
        )

        assertFalse(
            provenance.cycleDetected
        )

        assertTrue(
            lifecycleComposition
                .supersessionIntegrityChecker()
                .check()
                .valid
        )
    }

    private fun knowledge(
        statement: String,
        confidence: Double,
        source: RuntimeKnowledgeSource,
        createdAt: Long
    ) =
        RuntimeKnowledge(
            statement = statement,
            confidence = confidence,
            source = source,
            createdAt = createdAt
        )
}
