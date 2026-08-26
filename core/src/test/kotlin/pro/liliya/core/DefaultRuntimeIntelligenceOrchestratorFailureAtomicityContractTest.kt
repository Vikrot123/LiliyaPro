package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrationResult
import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrator
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningContext
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningEngine
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.orchestration.DefaultRuntimeIntelligenceOrchestrator
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflection
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.history.RuntimeReflectionHistory
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionStability
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrend
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrendAnalyzer
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModelProvider

class DefaultRuntimeIntelligenceOrchestratorFailureAtomicityContractTest {

    @Test
    fun failed_meaning_stage_must_not_leave_partial_reflection_history_mutation() {
        val selfModel = RuntimeSelfModel(
            snapshot = RuntimeContextSnapshot(
                runtimeState = "RUNNING",
                activeServices = listOf("runtime"),
                timestamp = 1L
            ),
            metadata = RuntimeContextMetadata(
                runtimeVersion = "1",
                recoveryAvailable = true,
                diagnosticsAvailable = true
            )
        )

        val reflectionSnapshot = RuntimeReflectionSnapshot(
            summary = "candidate",
            healthy = true,
            analyzedAt = 2L
        )

        val history = RecordingHistory()

        val orchestrator = DefaultRuntimeIntelligenceOrchestrator(
            selfModelProvider = object : RuntimeSelfModelProvider {
                override fun currentSelfModel(): RuntimeSelfModel =
                    selfModel
            },
            reflection = object : RuntimeReflection {
                override fun analyze(
                    selfModel: RuntimeSelfModel
                ): RuntimeReflectionSnapshot =
                    reflectionSnapshot
            },
            reflectionHistory = history,
            trendAnalyzer = object : RuntimeReflectionTrendAnalyzer {
                override fun analyze(
                    history: List<RuntimeReflectionSnapshot>
                ): RuntimeReflectionTrend =
                    RuntimeReflectionTrend(
                        stability = RuntimeReflectionStability.STABLE,
                        healthyRatio = 1.0,
                        improving = false
                    )
            },
            meaningEngine = object : RuntimeMeaningEngine {
                override fun interpret(
                    context: RuntimeMeaningContext
                ): RuntimeMeaningResult {
                    error("forced meaning failure")
                }
            },
            experienceKnowledgeOrchestrator =
                object : RuntimeExperienceKnowledgeOrchestrator {
                    override fun process(
                        context: RuntimeExperienceContext
                    ): RuntimeExperienceKnowledgeOrchestrationResult {
                        error("must not reach experience stage")
                    }
                }
        )

        assertFailsWith<IllegalStateException> {
            orchestrator.process()
        }

        assertEquals(
            emptyList(),
            history.snapshots(),
            "failed orchestration must not commit reflection history"
        )
    }

    private class RecordingHistory :
        RuntimeReflectionHistory {

        private val values =
            mutableListOf<RuntimeReflectionSnapshot>()

        override fun record(
            snapshot: RuntimeReflectionSnapshot
        ) {
            values += snapshot
        }

        override fun snapshots():
            List<RuntimeReflectionSnapshot> =
            values.toList()
    }
    @Test
    fun failed_experience_stage_must_not_leave_partial_reflection_history_mutation() {
        val selfModel = RuntimeSelfModel(
            snapshot = RuntimeContextSnapshot(
                runtimeState = "RUNNING",
                activeServices = listOf("runtime"),
                timestamp = 10L
            ),
            metadata = RuntimeContextMetadata(
                runtimeVersion = "1",
                recoveryAvailable = true,
                diagnosticsAvailable = true
            )
        )

        val reflectionSnapshot = RuntimeReflectionSnapshot(
            summary = "final-stage candidate",
            healthy = true,
            analyzedAt = 11L
        )

        val history = RecordingHistory()

        val orchestrator =
            DefaultRuntimeIntelligenceOrchestrator(
                selfModelProvider =
                    object : RuntimeSelfModelProvider {
                        override fun currentSelfModel():
                            RuntimeSelfModel =
                            selfModel
                    },
                reflection =
                    object : RuntimeReflection {
                        override fun analyze(
                            selfModel: RuntimeSelfModel
                        ): RuntimeReflectionSnapshot =
                            reflectionSnapshot
                    },
                reflectionHistory = history,
                trendAnalyzer =
                    object : RuntimeReflectionTrendAnalyzer {
                        override fun analyze(
                            history: List<RuntimeReflectionSnapshot>
                        ): RuntimeReflectionTrend =
                            RuntimeReflectionTrend(
                                stability =
                                    RuntimeReflectionStability.STABLE,
                                healthyRatio = 1.0,
                                improving = false
                            )
                    },
                meaningEngine =
                    object : RuntimeMeaningEngine {
                        override fun interpret(
                            context: RuntimeMeaningContext
                        ): RuntimeMeaningResult =
                            RuntimeMeaningResult(
                                interpretation = "meaning succeeded",
                                confidence = 1.0,
                                significance =
                                    pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance.STABLE,
                                generatedAt = 12L
                            )
                    },
                experienceKnowledgeOrchestrator =
                    object : RuntimeExperienceKnowledgeOrchestrator {
                        override fun process(
                            context: RuntimeExperienceContext
                        ): RuntimeExperienceKnowledgeOrchestrationResult {
                            error("forced experience failure")
                        }
                    }
            )

        assertFailsWith<IllegalStateException> {
            orchestrator.process()
        }

        assertEquals(
            emptyList(),
            history.snapshots(),
            "failed final orchestration stage must not commit reflection history"
        )
    }

}
