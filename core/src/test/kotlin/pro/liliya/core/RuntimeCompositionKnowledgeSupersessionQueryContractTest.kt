package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource

class RuntimeCompositionKnowledgeSupersessionQueryContractTest {

    @Test
    fun composition_owns_stable_supersession_query() {
        val composition =
            DefaultRuntimeComposition()

        assertSame(
            composition
                .knowledgeLifecycleComposition()
                .supersessionQuery(),
            composition
                .knowledgeLifecycleComposition()
                .supersessionQuery()
        )
    }

    @Test
    fun composition_query_resolves_full_real_supersession_chain() {
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
                "runtime evolving lineage knowledge",
                0.60,
                1L
            )

        val second =
            knowledge(
                "Runtime evolving lineage knowledge.",
                0.80,
                2L
            )

        val third =
            knowledge(
                "RUNTIME evolving lineage knowledge!",
                0.95,
                3L
            )

        lifecycle.create(first)
        lifecycle.create(second)
        lifecycle.create(third)

        val trace =
            lifecycleComposition
                .supersessionQuery()
                .trace(first)

        assertEquals(
            listOf(
                first,
                second,
                third
            ),
            trace.chain
        )

        assertEquals(
            third,
            trace.currentKnowledge
        )

        assertFalse(
            trace.cycleDetected
        )

        assertEquals(
            listOf(third),
            lifecycle
                .memory()
                .availableKnowledge()
        )
    }

    @Test
    fun reset_removes_observable_supersession_chain() {
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
                "runtime reset query knowledge",
                0.60,
                1L
            )

        val second =
            knowledge(
                "Runtime reset query knowledge.",
                0.95,
                2L
            )

        lifecycle.create(first)
        lifecycle.create(second)

        assertEquals(
            second,
            lifecycleComposition
                .supersessionQuery()
                .currentKnowledge(first)
        )

        lifecycleComposition.reset()

        val trace =
            lifecycleComposition
                .supersessionQuery()
                .trace(first)

        assertEquals(
            listOf(first),
            trace.chain
        )

        assertEquals(
            first,
            trace.currentKnowledge
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
