package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
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

class DefaultRuntimeMeaningEngineCognitiveKnowledgeContractTest {

    @Test
    fun available_knowledge_must_enrich_interpretation_without_changing_runtime_significance() {
        val knowledge =
            RuntimeKnowledge(
                statement = "runtime recovered successfully before",
                confidence = 0.9,
                source = RuntimeKnowledgeSource.EXPERIENCE,
                createdAt = 1L
            )

        val result =
            DefaultRuntimeMeaningEngine()
                .interpret(
                    RuntimeMeaningContext(
                        selfModel = selfModel(),
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

        assertEquals(
            RuntimeMeaningSignificance.STABLE,
            result.significance,
            "knowledge context must not override runtime significance"
        )

        assertEquals(
            0.95,
            result.confidence,
            "knowledge context must not change established runtime confidence"
        )

        assertTrue(
            result.interpretation.contains(
                "available knowledge",
                ignoreCase = true
            ),
            "meaning interpretation must acknowledge available cognitive knowledge"
        )
    }

    @Test
    fun empty_knowledge_must_preserve_base_interpretation() {
        val result =
            DefaultRuntimeMeaningEngine()
                .interpret(
                    RuntimeMeaningContext(
                        selfModel = selfModel(),
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

        assertEquals(
            "Runtime maintains stable operational state",
            result.interpretation,
            "empty cognitive knowledge must preserve existing meaning interpretation"
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


    private fun selfModel(): RuntimeSelfModel {
        return RuntimeSelfModel(
            snapshot =
                RuntimeContextSnapshot(
                    runtimeState = "RUNNING",
                    activeServices = listOf("runtime"),
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
