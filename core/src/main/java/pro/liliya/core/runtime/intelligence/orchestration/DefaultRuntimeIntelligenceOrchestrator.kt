package pro.liliya.core.runtime.intelligence.orchestration

import pro.liliya.core.runtime.intelligence.experience.RuntimeExperienceContext
import pro.liliya.core.runtime.intelligence.experience.orchestration.RuntimeExperienceKnowledgeOrchestrator
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningContext
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningEngine
import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflection
import pro.liliya.core.runtime.intelligence.reflection.history.RuntimeReflectionHistory
import pro.liliya.core.runtime.intelligence.reflection.trend.RuntimeReflectionTrendAnalyzer
import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModelProvider

class DefaultRuntimeIntelligenceOrchestrator(
    private val selfModelProvider: RuntimeSelfModelProvider,
    private val reflection: RuntimeReflection,
    private val reflectionHistory: RuntimeReflectionHistory,
    private val trendAnalyzer: RuntimeReflectionTrendAnalyzer,
    private val meaningEngine: RuntimeMeaningEngine,
    private val experienceKnowledgeOrchestrator:
        RuntimeExperienceKnowledgeOrchestrator
) : RuntimeIntelligenceOrchestrator {

    override fun process(): RuntimeIntelligenceOrchestrationResult {
        val selfModel = selfModelProvider.currentSelfModel()

        val reflectionSnapshot = reflection.analyze(selfModel)

        val trendHistory =
            reflectionHistory.snapshots() +
                reflectionSnapshot

        val trend = trendAnalyzer.analyze(
            trendHistory
        )

        val meaning = meaningEngine.interpret(
            RuntimeMeaningContext(
                selfModel = selfModel,
                reflection = reflectionSnapshot,
                trend = trend
            )
        )

        val experienceKnowledge =
            experienceKnowledgeOrchestrator.process(
                RuntimeExperienceContext(
                    selfModel = selfModel,
                    meaning = meaning
                )
            )

        reflectionHistory.record(
            reflectionSnapshot
        )

        return RuntimeIntelligenceOrchestrationResult(
            selfModel = selfModel,
            reflection = reflectionSnapshot,
            trend = trend,
            meaning = meaning,
            experienceKnowledge = experienceKnowledge
        )
    }
}
