package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.RuntimeDecision
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionResult

class RuntimeDecisionProvenanceSnapshotStabilityContractTest {

    @Test
    fun recorded_explanation_keeps_original_provenance_after_later_supersession() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        val first =
            knowledge(
                "runtime evolving decision knowledge",
                0.60,
                RuntimeKnowledgeSource.EXPERIENCE,
                1L
            )

        val second =
            knowledge(
                "Runtime evolving decision knowledge.",
                0.80,
                RuntimeKnowledgeSource.REFLECTION,
                2L
            )

        val third =
            knowledge(
                "RUNTIME evolving decision knowledge!",
                0.95,
                RuntimeKnowledgeSource.CONSOLIDATION,
                3L
            )

        lifecycle.create(first)
        lifecycle.create(second)

        val firstDecision =
            decisionFor(
                second
            )

        val firstRecord =
            composition
                .decisionExplanationRecorder()
                .explainAndRecord(
                    firstDecision
                )

        val firstSnapshot =
            assertNotNull(
                firstRecord
                    .explanation
                    .knowledgeProvenance
            )

        assertEquals(
            listOf(
                first,
                second
            ),
            firstSnapshot.chain
        )

        assertEquals(
            second,
            firstSnapshot.currentKnowledge
        )

        lifecycle.create(third)

        val secondDecision =
            decisionFor(
                third
            )

        val secondRecord =
            composition
                .decisionExplanationRecorder()
                .explainAndRecord(
                    secondDecision
                )

        val secondSnapshot =
            assertNotNull(
                secondRecord
                    .explanation
                    .knowledgeProvenance
            )

        assertEquals(
            listOf(
                first,
                second,
                third
            ),
            secondSnapshot.chain
        )

        assertEquals(
            third,
            secondSnapshot.currentKnowledge
        )

        assertEquals(
            listOf(
                first,
                second
            ),
            firstSnapshot.chain,
            "historical explanation must not expand after later supersession"
        )

        assertEquals(
            second,
            firstSnapshot.currentKnowledge,
            "historical explanation must preserve its original current winner"
        )
    }

    @Test
    fun explanation_history_preserves_independent_provenance_snapshots() {
        val composition =
            DefaultRuntimeComposition()

        val lifecycle =
            composition
                .knowledgeLifecycleComposition()
                .lifecycleMemory()

        val first =
            knowledge(
                "runtime independent snapshot knowledge",
                0.60,
                RuntimeKnowledgeSource.EXPERIENCE,
                1L
            )

        val second =
            knowledge(
                "Runtime independent snapshot knowledge.",
                0.80,
                RuntimeKnowledgeSource.REFLECTION,
                2L
            )

        val third =
            knowledge(
                "RUNTIME independent snapshot knowledge!",
                0.95,
                RuntimeKnowledgeSource.CONSOLIDATION,
                3L
            )

        lifecycle.create(first)

        composition
            .decisionExplanationRecorder()
            .explainAndRecord(
                decisionFor(first)
            )

        lifecycle.create(second)

        composition
            .decisionExplanationRecorder()
            .explainAndRecord(
                decisionFor(second)
            )

        lifecycle.create(third)

        composition
            .decisionExplanationRecorder()
            .explainAndRecord(
                decisionFor(third)
            )

        val records =
            composition
                .decisionExplanationHistory()
                .records()

        assertEquals(
            3,
            records.size
        )

        val chains =
            records.map { record ->
                assertNotNull(
                    record
                        .explanation
                        .knowledgeProvenance
                ).chain
            }

        assertEquals(
            listOf(first),
            chains[0]
        )

        assertEquals(
            listOf(
                first,
                second
            ),
            chains[1]
        )

        assertEquals(
            listOf(
                first,
                second,
                third
            ),
            chains[2]
        )
    }

    @Test
    fun provenance_snapshot_survives_lineage_reset_as_historical_value() {
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
                "runtime reset snapshot knowledge",
                0.60,
                RuntimeKnowledgeSource.EXPERIENCE,
                1L
            )

        val second =
            knowledge(
                "Runtime reset snapshot knowledge.",
                0.95,
                RuntimeKnowledgeSource.CONSOLIDATION,
                2L
            )

        lifecycle.create(first)
        lifecycle.create(second)

        val record =
            composition
                .decisionExplanationRecorder()
                .explainAndRecord(
                    decisionFor(second)
                )

        val snapshot =
            assertNotNull(
                record
                    .explanation
                    .knowledgeProvenance
            )

        assertEquals(
            listOf(
                first,
                second
            ),
            snapshot.chain
        )

        lifecycleComposition.reset()

        assertTrue(
            lifecycleComposition
                .supersessionHistory()
                .records()
                .isEmpty()
        )

        assertEquals(
            listOf(
                first,
                second
            ),
            snapshot.chain,
            "already-created explanation is a historical snapshot and must survive lineage reset"
        )

        assertEquals(
            second,
            snapshot.currentKnowledge
        )
    }

    private fun decisionFor(
        knowledge: RuntimeKnowledge
    ): RuntimeDecision {

        return RuntimeDecision(
            command =
                RuntimeCommand.HEALTH_CHECK,
            reason =
                "knowledge-backed diagnostic verification",
            confidence =
                knowledge.confidence,
            knowledgeSelection =
                RuntimeKnowledgeSelectionResult(
                    knowledge = knowledge,
                    relevantPoolUsed = true,
                    reason =
                        "selected current knowledge",
                    selectionReason =
                        RuntimeKnowledgeSelectionReason
                            .RELEVANT_POOL,
                    relevanceScore = 1.0
                )
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
