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

class DefaultRuntimeMeaningEngineKnowledgeContentContractTest {

    @Test
    fun meaning_must_include_bounded_available_knowledge_content() {
        val first =
            knowledge(
                statement = "previous recovery succeeded",
                createdAt = 10L
            )

        val second =
            knowledge(
                statement = "runtime remained stable afterwards",
                createdAt = 20L
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
                                            listOf(
                                                first,
                                                second
                                            )
                                    ),
                                timestamp = 40L
                            )
                    )
                )

        assertEquals(
            RuntimeMeaningSignificance.STABLE,
            result.significance
        )

        assertEquals(
            0.95,
            result.confidence
        )

        assertTrue(
            result.interpretation.contains(
                second.statement
            ),
            "meaning must include one bounded knowledge statement"
        )

        assertTrue(
            !result.interpretation.contains(
                first.statement
            ),
            "meaning must not concatenate the entire available knowledge history"
        )
    }

    private fun knowledge(
        statement: String,
        createdAt: Long
    ): RuntimeKnowledge {
        return RuntimeKnowledge(
            statement = statement,
            confidence = 0.9,
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
