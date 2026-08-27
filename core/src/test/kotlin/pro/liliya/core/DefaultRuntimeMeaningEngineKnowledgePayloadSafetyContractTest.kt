package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType
import pro.liliya.core.runtime.intelligence.context.cognitive.source.KnowledgeCognitiveContextSource
import pro.liliya.core.runtime.intelligence.meaning.DefaultRuntimeMeaningEngine
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningContext
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionStability
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrend
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class DefaultRuntimeMeaningEngineKnowledgePayloadSafetyContractTest {

    @Test
    fun non_knowledge_payload_values_must_be_ignored_safely() {
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
                                            listOf(
                                                "not knowledge",
                                                42,
                                                null
                                            )
                                    ),
                                timestamp = 2L
                            )
                    )
                )

        assertEquals(
            "Runtime maintains stable operational state",
            result.interpretation
        )

        assertEquals(
            RuntimeMeaningSignificance.STABLE,
            result.significance
        )

        assertEquals(
            0.95,
            result.confidence
        )

        assertFalse(
            result.interpretation.contains(
                "available knowledge",
                ignoreCase = true
            ),
            "non-knowledge payload must not be treated as available knowledge"
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
