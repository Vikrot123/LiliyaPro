package pro.liliya.core.runtime.intelligence.cognition

import pro.liliya.core.runtime.intelligence.intent.RuntimeIntentDeriver
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.RuntimeAutonomousReasoningPipeline
import pro.liliya.core.runtime.intelligence.strategy.RuntimeStrategyDeriver

class DefaultRuntimeAutonomousCognitionPipeline(
    private val reasoningPipeline:
        RuntimeAutonomousReasoningPipeline,
    private val intentDeriver:
        RuntimeIntentDeriver,
    private val strategyDeriver:
        RuntimeStrategyDeriver
) : RuntimeAutonomousCognitionPipeline {

    override fun process(
        intelligence: RuntimeIntelligenceOrchestrationResult
    ): RuntimeAutonomousCognitionResult {

        val reasoningResult =
            reasoningPipeline.process(
                intelligence
            )

        val intent =
            intentDeriver.derive(
                reasoningResult
            )

        val strategy =
            strategyDeriver.derive(
                intent
            )

        val chainConsistent =
            intent.reasoningResult ===
                reasoningResult &&
                strategy.intent ===
                intent

        return RuntimeAutonomousCognitionResult(
            reasoningResult = reasoningResult,
            intent = intent,
            strategy = strategy,
            coherent =
                chainConsistent &&
                    reasoningResult.coherent &&
                    intent.coherent &&
                    strategy.coherent,
            actionable =
                chainConsistent &&
                    reasoningResult.actionable &&
                    intent.actionable &&
                    strategy.actionable,
            confidence =
                minOf(
                    reasoningResult.confidence,
                    intent.confidence,
                    strategy.confidence
                )
        )
    }
}
