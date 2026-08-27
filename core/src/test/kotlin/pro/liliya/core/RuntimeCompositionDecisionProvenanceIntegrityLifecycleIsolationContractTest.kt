package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionResult

class RuntimeCompositionDecisionProvenanceIntegrityLifecycleIsolationContractTest {

    @Test
    fun separate_compositions_own_independent_provenance_integrity_queries() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        assertNotSame(
            first
                .knowledgeLifecycleComposition()
                .provenanceIntegrityQuery(),
            second
                .knowledgeLifecycleComposition()
                .provenanceIntegrityQuery()
        )

        assertNotSame(
            first
                .knowledgeLifecycleComposition()
                .supersessionHistory(),
            second
                .knowledgeLifecycleComposition()
                .supersessionHistory()
        )
    }

    @Test
    fun supersession_lineage_does_not_leak_between_compositions() {
        val first =
            DefaultRuntimeComposition()

        val second =
            DefaultRuntimeComposition()

        val weak =
            knowledge(
                "runtime composition-local integrity knowledge",
                0.60,
                1L
            )

        val strong =
            knowledge(
                "Runtime composition-local integrity knowledge.",
                0.95,
                2L
            )

        first
            .knowledgeLifecycleComposition()
            .lifecycleMemory()
            .create(weak)

        first
            .knowledgeLifecycleComposition()
            .lifecycleMemory()
            .create(strong)

        assertEquals(
            1,
            first
                .knowledgeLifecycleComposition()
                .supersessionHistory()
                .records()
                .size
        )

        assertTrue(
            second
                .knowledgeLifecycleComposition()
                .supersessionHistory()
                .records()
                .isEmpty()
        )

        val secondIntegrity =
            second
                .knowledgeLifecycleComposition()
                .provenanceIntegrityQuery()
                .check(strong)

        assertTrue(
            secondIntegrity.valid
        )

        assertEquals(
            0,
            secondIntegrity.issueCount
        )
    }

    @Test
    fun prepare_runtime_keeps_integrity_query_owner_but_resets_lineage_state() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val queryBefore =
            lifecycle
                .provenanceIntegrityQuery()

        val supersessionQueryBefore =
            lifecycle
                .supersessionQuery()

        val provenanceQueryBefore =
            lifecycle
                .provenanceQuery()

        val weak =
            knowledge(
                "runtime prepare lineage knowledge",
                0.60,
                1L
            )

        val strong =
            knowledge(
                "Runtime prepare lineage knowledge.",
                0.95,
                2L
            )

        lifecycle
            .lifecycleMemory()
            .create(weak)

        lifecycle
            .lifecycleMemory()
            .create(strong)

        assertEquals(
            1,
            lifecycle
                .supersessionHistory()
                .records()
                .size
        )

        composition.prepareRuntime()

        assertSame(
            queryBefore,
            lifecycle.provenanceIntegrityQuery()
        )

        assertSame(
            supersessionQueryBefore,
            lifecycle.supersessionQuery()
        )

        assertSame(
            provenanceQueryBefore,
            lifecycle.provenanceQuery()
        )

        assertTrue(
            lifecycle
                .supersessionHistory()
                .records()
                .isEmpty()
        )
    }

    @Test
    fun prepare_runtime_clears_decision_explanation_history() {
        val composition =
            DefaultRuntimeComposition()

        val knowledge =
            knowledge(
                "runtime explanation prepare knowledge",
                0.90,
                1L
            )

        composition
            .knowledgeLifecycleComposition()
            .lifecycleMemory()
            .create(knowledge)

        composition
            .decisionExplanationRecorder()
            .explainAndRecord(
                decisionFor(knowledge)
            )

        assertEquals(
            1,
            composition
                .decisionExplanationHistory()
                .records()
                .size
        )

        composition.prepareRuntime()

        assertTrue(
            composition
                .decisionExplanationHistory()
                .records()
                .isEmpty()
        )
    }

    @Test
    fun previously_created_integrity_snapshot_remains_independent_value_after_prepare() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val weak =
            knowledge(
                "runtime historical integrity value",
                0.60,
                1L
            )

        val strong =
            knowledge(
                "Runtime historical integrity value.",
                0.95,
                2L
            )

        lifecycle
            .lifecycleMemory()
            .create(weak)

        lifecycle
            .lifecycleMemory()
            .create(strong)

        val explanation =
            composition
                .decisionExplainer()
                .explain(
                    decisionFor(strong)
                )

        val provenanceBefore =
            assertNotNull(
                explanation.knowledgeProvenance
            )

        val integrityBefore =
            assertNotNull(
                explanation.knowledgeProvenanceIntegrity
            )

        assertEquals(
            listOf(
                weak,
                strong
            ),
            provenanceBefore.chain
        )

        assertTrue(
            integrityBefore.valid
        )

        composition.prepareRuntime()

        assertEquals(
            listOf(
                weak,
                strong
            ),
            provenanceBefore.chain
        )

        assertTrue(
            integrityBefore.valid
        )

        assertEquals(
            0,
            integrityBefore.issueCount
        )
    }

    @Test
    fun new_explanation_after_prepare_uses_reset_lineage_not_old_supersession_chain() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()

        val weak =
            knowledge(
                "runtime reset explanation lineage",
                0.60,
                1L
            )

        val strong =
            knowledge(
                "Runtime reset explanation lineage.",
                0.95,
                2L
            )

        lifecycle
            .lifecycleMemory()
            .create(weak)

        lifecycle
            .lifecycleMemory()
            .create(strong)

        val before =
            composition
                .decisionExplainer()
                .explain(
                    decisionFor(strong)
                )

        assertEquals(
            listOf(
                weak,
                strong
            ),
            assertNotNull(
                before.knowledgeProvenance
            ).chain
        )

        composition.prepareRuntime()

        val after =
            composition
                .decisionExplainer()
                .explain(
                    decisionFor(strong)
                )

        assertEquals(
            listOf(strong),
            assertNotNull(
                after.knowledgeProvenance
            ).chain
        )

        assertTrue(
            assertNotNull(
                after.knowledgeProvenanceIntegrity
            ).valid
        )
    }

    @Test
    fun decision_without_knowledge_remains_null_after_prepare_cycle() {
        val composition =
            DefaultRuntimeComposition()

        val before =
            composition
                .decisionExplainer()
                .explain(
                    decisionWithoutKnowledge()
                )

        assertNull(
            before.knowledgeProvenance
        )

        assertNull(
            before.knowledgeProvenanceIntegrity
        )

        composition.prepareRuntime()

        val after =
            composition
                .decisionExplainer()
                .explain(
                    decisionWithoutKnowledge()
                )

        assertNull(
            after.knowledgeProvenance
        )

        assertNull(
            after.knowledgeProvenanceIntegrity
        )
    }

    private fun decisionFor(
        knowledge: RuntimeKnowledge
    ) =
        RuntimeDecision(
            command =
                RuntimeCommand.HEALTH_CHECK,
            reason =
                "provenance integrity lifecycle verification",
            confidence =
                knowledge.confidence,
            knowledgeSelection =
                RuntimeKnowledgeSelectionResult(
                    knowledge = knowledge,
                    relevantPoolUsed = true,
                    reason =
                        "selected lifecycle knowledge",
                    selectionReason =
                        RuntimeKnowledgeSelectionReason
                            .RELEVANT_POOL,
                    relevanceScore = 1.0
                )
        )

    private fun decisionWithoutKnowledge() =
        RuntimeDecision(
            command = null,
            reason =
                "no knowledge required",
            confidence = 0.95,
            knowledgeSelection =
                RuntimeKnowledgeSelectionResult(
                    knowledge = null,
                    relevantPoolUsed = false,
                    reason =
                        "no selected knowledge",
                    selectionReason =
                        RuntimeKnowledgeSelectionReason.EMPTY,
                    relevanceScore = 0.0
                )
        )

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
