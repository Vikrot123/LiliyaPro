package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource

class RuntimeCompositionKnowledgeSupersessionIntegrityContractTest {

    @Test
    fun composition_owns_stable_integrity_checker() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        assertSame(
            lifecycle.supersessionIntegrityChecker(),
            lifecycle.supersessionIntegrityChecker()
        )
    }

    @Test
    fun real_multi_step_supersession_remains_integrity_valid() {
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
                "runtime integrity lineage knowledge",
                0.60,
                1L
            )

        val second =
            knowledge(
                "Runtime integrity lineage knowledge.",
                0.80,
                2L
            )

        val third =
            knowledge(
                "RUNTIME integrity lineage knowledge!",
                0.95,
                3L
            )

        lifecycle.create(first)
        lifecycle.create(second)
        lifecycle.create(third)

        val report =
            lifecycleComposition
                .supersessionIntegrityChecker()
                .check()

        assertTrue(
            report.valid
        )

        assertEquals(
            0,
            report.issueCount
        )

        assertEquals(
            third,
            lifecycleComposition
                .supersessionQuery()
                .currentKnowledge(first)
        )
    }

    @Test
    fun reset_returns_integrity_to_empty_valid_state() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycleComposition =
            composition
                .knowledgeLifecycleComposition()

        val lifecycle =
            lifecycleComposition
                .lifecycleMemory()

        lifecycle.create(
            knowledge(
                "runtime reset integrity knowledge",
                0.60,
                1L
            )
        )

        lifecycle.create(
            knowledge(
                "Runtime reset integrity knowledge.",
                0.95,
                2L
            )
        )

        assertEquals(
            1,
            lifecycleComposition
                .supersessionHistory()
                .records()
                .size
        )

        lifecycleComposition.reset()

        val report =
            lifecycleComposition
                .supersessionIntegrityChecker()
                .check()

        assertTrue(report.valid)
        assertEquals(0, report.issueCount)
    }

    private fun knowledge(
        statement: String,
        confidence: Double,
        createdAt: Long
    ) =
        RuntimeKnowledge(
            statement = statement,
            confidence = confidence,
            source =
                RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = createdAt
        )
}
