package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.source.KnowledgeCognitiveContextSource
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledge
import pro.liliya.core.runtime.intelligence.knowledge.RuntimeKnowledgeSource
import pro.liliya.core.runtime.intelligence.knowledge.selection.RuntimeKnowledgeSelectionReason
import pro.liliya.core.runtime.intelligence.meaning.DefaultRuntimeMeaningEngine
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningContext
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionStability
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrend
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class DefaultRuntimeMeaningEngineKnowledgeSelectionObservabilityContractTest {

    @Test
    fun meaning_result_exposes_selector_diagnostics() {
        val knowledge =
            RuntimeKnowledge(
                statement =
                    "Runtime maintains stable operational state",
                confidence = 0.90,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 1L
            )

        val result =
            DefaultRuntimeMeaningEngine()
                .interpret(
                    RuntimeMeaningContext(
                        selfModel =
                            RuntimeSelfModel(
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
                            ),
                        reflection =
                            RuntimeReflectionSnapshot(
                                summary = "healthy",
                                healthy = true,
                                analyzedAt = 1L
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
                                            listOf(knowledge)
                                    ),
                                timestamp = 2L
                            )
                    )
                )

        val selection =
            assertNotNull(result.knowledgeSelection)

        assertEquals(
            knowledge,
            selection.knowledge
        )

        assertEquals(
            RuntimeKnowledgeSelectionReason.RELEVANT_POOL,
            selection.selectionReason
        )

        assertEquals(
            1.0,
            selection.relevanceScore
        )
    }
    @Test
    fun meaning_result_exposes_empty_selection_diagnostics() {
        val result =
            DefaultRuntimeMeaningEngine()
                .interpret(
                    RuntimeMeaningContext(
                        selfModel =
                            RuntimeSelfModel(
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
                            ),
                        reflection =
                            RuntimeReflectionSnapshot(
                                summary = "healthy",
                                healthy = true,
                                analyzedAt = 1L
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
                                            emptyList<RuntimeKnowledge>()
                                    ),
                                timestamp = 2L
                            )
                    )
                )

        val selection =
            assertNotNull(result.knowledgeSelection)

        assertEquals(
            null,
            selection.knowledge
        )

        assertEquals(
            RuntimeKnowledgeSelectionReason.EMPTY,
            selection.selectionReason
        )

        assertEquals(
            0.0,
            selection.relevanceScore
        )
    }


}
