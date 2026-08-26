package pro.liliya.core

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperience
import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceImportance
import pro.liliya.core.runtime.intelligence.experience.decision.RuntimeExperienceDecision
import pro.liliya.core.runtime.intelligence.experience.pipeline.RuntimeExperiencePipelineResult

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.knowledge.RuntimeExperienceKnowledgePipelineResult
import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrationResult
import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrator
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningContext
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningEngine
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningResult
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflection
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot
import pro.liliya.core.runtime.intelligence.reflection.history.RuntimeReflectionHistory
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionStability
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrend
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrendAnalyzer
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModelProvider
import pro.liliya.core.runtime.intelligence.orchestration.DefaultRuntimeIntelligenceOrchestrator

class DefaultRuntimeIntelligenceOrchestratorContractTest {

    private val selfModel = RuntimeSelfModel(
        snapshot = pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot(
            runtimeState = "RUNNING",
            activeServices = listOf("runtime"),
            timestamp = 1L
        ),
        metadata = pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata(
            runtimeVersion = "1",
            recoveryAvailable = true,
            diagnosticsAvailable = true
        )
    )

    private val reflectionSnapshot =
        RuntimeReflectionSnapshot(
            summary = "healthy",
            healthy = true,
            analyzedAt = 2L
        )

    private val trend = RuntimeReflectionTrend(
        stability = RuntimeReflectionStability.STABLE,
        healthyRatio = 1.0,
        improving = false
    )

    private val meaning = RuntimeMeaningResult(
        interpretation = "stable",
        confidence = 1.0,
        significance = RuntimeMeaningSignificance.STABLE,
        generatedAt = 3L
    )

    private val experienceKnowledge =
        RuntimeExperienceKnowledgeOrchestrationResult(
            pipelineResult =
                RuntimeExperienceKnowledgePipelineResult(
                    experienceResult = RuntimeExperiencePipelineResult(
                        experience = RuntimeExperience(
                            description = "test experience",
                            meaning = meaning,
                            importance = RuntimeExperienceImportance.MEDIUM,
                            createdAt = 4L
                        ),
                        decision = RuntimeExperienceDecision(
                            shouldRemember = true,
                            reason = "test"
                        )
                    ),
                    consolidation = null,
                    knowledgeResult = null
                )
        )

    private class FakeSelfModelProvider(
        private val value: RuntimeSelfModel
    ) : RuntimeSelfModelProvider {

        override fun currentSelfModel(): RuntimeSelfModel = value
    }

    private class FakeReflection(
        private val value: RuntimeReflectionSnapshot
    ) : RuntimeReflection {

        var received: RuntimeSelfModel? = null

        override fun analyze(
            selfModel: RuntimeSelfModel
        ): RuntimeReflectionSnapshot {
            received = selfModel
            return value
        }
    }

    private class FakeHistory : RuntimeReflectionHistory {

        val values = mutableListOf<RuntimeReflectionSnapshot>()

        override fun record(
            snapshot: RuntimeReflectionSnapshot
        ) {
            values += snapshot
        }

        override fun snapshots(): List<RuntimeReflectionSnapshot> {
            return values.toList()
        }
    }

    private class FakeTrendAnalyzer(
        private val value: RuntimeReflectionTrend
    ) : RuntimeReflectionTrendAnalyzer {

        var received: List<RuntimeReflectionSnapshot>? = null

        override fun analyze(
            history: List<RuntimeReflectionSnapshot>
        ): RuntimeReflectionTrend {
            received = history
            return value
        }
    }

    private class FakeMeaningEngine(
        private val value: RuntimeMeaningResult
    ) : RuntimeMeaningEngine {

        var received: RuntimeMeaningContext? = null

        override fun interpret(
            context: RuntimeMeaningContext
        ): RuntimeMeaningResult {
            received = context
            return value
        }
    }

    private class FakeExperienceKnowledgeOrchestrator(
        private val value: RuntimeExperienceKnowledgeOrchestrationResult
    ) : RuntimeExperienceKnowledgeOrchestrator {

        var received: RuntimeExperienceContext? = null

        override fun process(
            context: RuntimeExperienceContext
        ): RuntimeExperienceKnowledgeOrchestrationResult {
            received = context
            return value
        }
    }

    @Test
    fun orchestrator_executes_intelligence_flow_in_order() {
        val selfModelProvider = FakeSelfModelProvider(selfModel)
        val reflection = FakeReflection(reflectionSnapshot)
        val history = FakeHistory()
        val trendAnalyzer = FakeTrendAnalyzer(trend)
        val meaningEngine = FakeMeaningEngine(meaning)
        val experienceOrchestrator =
            FakeExperienceKnowledgeOrchestrator(
                experienceKnowledge
            )

        val orchestrator =
            DefaultRuntimeIntelligenceOrchestrator(
                selfModelProvider = selfModelProvider,
                reflection = reflection,
                reflectionHistory = history,
                trendAnalyzer = trendAnalyzer,
                meaningEngine = meaningEngine,
                experienceKnowledgeOrchestrator =
                    experienceOrchestrator
            )

        val result = orchestrator.process()

        assertSame(selfModel, reflection.received)
        assertEquals(listOf(reflectionSnapshot), history.values)
        assertEquals(
            listOf(reflectionSnapshot),
            trendAnalyzer.received
        )

        assertSame(selfModel, meaningEngine.received?.selfModel)
        assertSame(
            reflectionSnapshot,
            meaningEngine.received?.reflection
        )
        assertSame(trend, meaningEngine.received?.trend)

        assertSame(
            selfModel,
            experienceOrchestrator.received?.selfModel
        )
        assertSame(
            meaning,
            experienceOrchestrator.received?.meaning
        )
    }

    @Test
    fun orchestrator_preserves_all_results() {
        val reflection = FakeReflection(reflectionSnapshot)
        val history = FakeHistory()
        val trendAnalyzer = FakeTrendAnalyzer(trend)
        val meaningEngine = FakeMeaningEngine(meaning)
        val experienceOrchestrator =
            FakeExperienceKnowledgeOrchestrator(
                experienceKnowledge
            )

        val orchestrator =
            DefaultRuntimeIntelligenceOrchestrator(
                selfModelProvider =
                    FakeSelfModelProvider(selfModel),
                reflection = reflection,
                reflectionHistory = history,
                trendAnalyzer = trendAnalyzer,
                meaningEngine = meaningEngine,
                experienceKnowledgeOrchestrator =
                    experienceOrchestrator
            )

        val result = orchestrator.process()

        assertSame(selfModel, result.selfModel)
        assertSame(reflectionSnapshot, result.reflection)
        assertSame(trend, result.trend)
        assertSame(meaning, result.meaning)
        assertSame(
            experienceKnowledge,
            result.experienceKnowledge
        )
    }
}
