package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource

class RuntimeCompositionKnowledgeSupersessionLineageContractTest {

    @Test
    fun successful_supersession_is_observable_through_composition_lineage() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycleComposition =
            composition
                .knowledgeLifecycleComposition()

        val lifecycle =
            lifecycleComposition
                .lifecycleMemory()

        val weak =
            knowledge(
                "runtime lineage operational knowledge",
                0.65,
                1L
            )

        val strong =
            knowledge(
                "Runtime lineage operational knowledge.",
                0.95,
                2L
            )

        lifecycle.create(weak)
        lifecycle.create(strong)

        val record =
            lifecycleComposition
                .supersessionHistory()
                .records()
                .single()

        assertEquals(
            weak,
            record.previousKnowledge
        )

        assertEquals(
            strong,
            record.replacementKnowledge
        )
    }

    @Test
    fun weaker_candidate_creates_no_false_supersession_lineage() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycleComposition =
            composition
                .knowledgeLifecycleComposition()

        val lifecycle =
            lifecycleComposition
                .lifecycleMemory()

        val strong =
            knowledge(
                "runtime retained lineage knowledge",
                0.95,
                1L
            )

        val weak =
            knowledge(
                "Runtime retained lineage knowledge.",
                0.60,
                2L
            )

        lifecycle.create(strong)
        lifecycle.create(weak)

        assertTrue(
            lifecycleComposition
                .supersessionHistory()
                .records()
                .isEmpty()
        )
    }

    @Test
    fun lifecycle_reset_clears_supersession_lineage() {
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
                "runtime reset lineage knowledge",
                0.65,
                1L
            )
        )

        lifecycle.create(
            knowledge(
                "Runtime reset lineage knowledge.",
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

        assertTrue(
            lifecycleComposition
                .supersessionHistory()
                .records()
                .isEmpty()
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
            source =
                RuntimeKnowledgeSource.CONSOLIDATION,
            createdAt = createdAt
        )
}
