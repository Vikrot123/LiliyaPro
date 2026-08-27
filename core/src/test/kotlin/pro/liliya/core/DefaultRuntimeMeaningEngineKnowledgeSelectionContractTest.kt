package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.source.KnowledgeCognitiveContextSource
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.meaning.DefaultRuntimeMeaningEngine
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningContext
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionStability
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrend
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class DefaultRuntimeMeaningEngineKnowledgeSelectionContractTest {

    @Test
    fun higher_confidence_knowledge_must_win_over_newer_lower_confidence_knowledge() {
        val stronger =
            knowledge(
                statement = "high confidence knowledge",
                confidence = 0.95,
                createdAt = 10L
            )

        val newerButWeaker =
            knowledge(
                statement = "newer low confidence knowledge",
                confidence = 0.60,
                createdAt = 20L
            )

        val result =
            interpret(
                listOf(
                    stronger,
                    newerButWeaker
                )
            )

        assertTrue(
            result.interpretation.contains(
                stronger.statement
            ),
            "higher confidence knowledge must be selected"
        )

        assertFalse(
            result.interpretation.contains(
                newerButWeaker.statement
            ),
            "newer lower-confidence knowledge must not replace stronger knowledge"
        )

        assertEquals(
            RuntimeMeaningSignificance.STABLE,
            result.significance
        )

        assertEquals(
            0.95,
            result.confidence
        )
    }

    @Test
    fun newer_knowledge_must_break_equal_confidence_tie() {
        val older =
            knowledge(
                statement = "older equal confidence knowledge",
                confidence = 0.90,
                createdAt = 10L
            )

        val newer =
            knowledge(
                statement = "newer equal confidence knowledge",
                confidence = 0.90,
                createdAt = 20L
            )

        val result =
            interpret(
                listOf(
                    older,
                    newer
                )
            )

        assertTrue(
            result.interpretation.contains(
                newer.statement
            ),
            "newest knowledge must win when confidence is equal"
        )

        assertFalse(
            result.interpretation.contains(
                older.statement
            ),
            "older equal-confidence knowledge must remain bounded out"
        )
    }


    @Test
    fun relevant_knowledge_must_win_over_more_confident_irrelevant_knowledge() {
        val relevant =
            knowledge(
                statement =
                    "Runtime maintains stable operational state",
                confidence = 0.80,
                createdAt = 10L
            )

        val irrelevant =
            knowledge(
                statement = "unrelated recovery knowledge",
                confidence = 0.99,
                createdAt = 20L
            )

        val result =
            interpret(listOf(relevant, irrelevant))

        assertTrue(
            result.interpretation.contains(relevant.statement)
        )

        assertFalse(
            result.interpretation.contains(irrelevant.statement)
        )
    }

    private fun interpret(
        knowledge: List<RuntimeKnowledge>
    ) =
        DefaultRuntimeMeaningEngine()
            .interpret(
                RuntimeMeaningContext(
                    selfModel = selfModel(),
                    reflection =
                        RuntimeReflectionSnapshot(
                            summary = "healthy",
                            healthy = true,
                            analyzedAt = 30L
                        ),
                    trend =
                        RuntimeReflectionTrend(
                            stability =
                                RuntimeReflectionStability.STABLE,
                            healthyRatio = 1.0,
                            improving = false
                        ),
                    cognitiveContext =
                        CognitiveContextSnapshot(
                            type =
                                CognitiveContextType.WORKING,
                            values =
                                mapOf(
                                    KnowledgeCognitiveContextSource
                                        .AVAILABLE_KNOWLEDGE_KEY to
                                        knowledge
                                ),
                            timestamp = 40L
                        )
                )
            )

    private fun knowledge(
        statement: String,
        confidence: Double,
        createdAt: Long
    ): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = statement,
            confidence = confidence,
            source = RuntimeKnowledgeSource.EXPERIENCE,
            createdAt = createdAt
        )
    }

    private fun selfModel(): RuntimeSelfModel {
        return RuntimeSelfModel(
            snapshot =
                RuntimeContextSnapshot(
                    runtimeState = "RUNNING",
                    activeServices =
                        listOf("runtime"),
                    timestamp = 1L
                ),
            metadata =
                RuntimeContextMetadata(
                    runtimeVersion = "1",
                    recoveryAvailable = true,
                    diagnosticsAvailable = true
                )
        )
    }
}
