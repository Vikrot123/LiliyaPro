package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue
import pro.liliya.core.runtime.intelligence.cognition.DefaultRuntimeAutonomousCognitionPipeline
import pro.liliya.core.runtime.intelligence.goal.DefaultRuntimeGoalDeriver
import pro.liliya.core.runtime.intelligence.intent.DefaultRuntimeIntentDeriver
import pro.liliya.core.runtime.intelligence.intent.RuntimeIntent
import pro.liliya.core.runtime.intelligence.intent.RuntimeIntentDeriver
import pro.liliya.core.runtime.intelligence.meaning.RuntimeMeaningSignificance
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult
import pro.liliya.core.runtime.intelligence.planning.DefaultRuntimePlanner
import pro.liliya.core.runtime.intelligence.reasoning.DefaultRuntimeReasoningAnalyzer
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.DefaultRuntimeAutonomousReasoningPipeline
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.RuntimeAutonomousReasoningPipeline
import pro.liliya.core.runtime.intelligence.reasoning.orchestration.RuntimeAutonomousReasoningResult
import pro.liliya.core.runtime.intelligence.strategy.DefaultRuntimeStrategyDeriver
import pro.liliya.core.runtime.intelligence.strategy.RuntimeStrategy
import pro.liliya.core.runtime.intelligence.strategy.RuntimeStrategyDeriver

class RuntimeAutonomousCognitionSemanticHardeningContractTest {

    @Test
    fun intent_built_from_foreign_reasoning_snapshot_fails_closed() {
        val intelligence =
            intelligence()

        val reasoning =
            reasoning(intelligence)

        val foreignReasoning =
            reasoning.copy()

        val foreignIntent =
            DefaultRuntimeIntentDeriver()
                .derive(
                    foreignReasoning
                )

        val result =
            DefaultRuntimeAutonomousCognitionPipeline(
                reasoningPipeline =
                    fixedReasoningPipeline(
                        reasoning
                    ),
                intentDeriver =
                    object : RuntimeIntentDeriver {
                        override fun derive(
                            reasoningResult:
                                RuntimeAutonomousReasoningResult
                        ): RuntimeIntent {
                            return foreignIntent
                        }
                    },
                strategyDeriver =
                    DefaultRuntimeStrategyDeriver()
            )
                .process(
                    intelligence
                )

        assertSame(
            reasoning,
            result.reasoningResult
        )

        assertSame(
            foreignIntent,
            result.intent
        )

        assertFalse(
            result.coherent,
            "cognition must reject intent derived from a foreign reasoning snapshot"
        )

        assertFalse(
            result.actionable,
            "foreign reasoning provenance must fail closed"
        )
    }

    @Test
    fun strategy_built_from_foreign_intent_snapshot_fails_closed() {
        val intelligence =
            intelligence()

        val reasoning =
            reasoning(intelligence)

        val intentDeriver =
            DefaultRuntimeIntentDeriver()

        val strategyDeriver =
            object : RuntimeStrategyDeriver {

                override fun derive(
                    intent: RuntimeIntent
                ): RuntimeStrategy {

                    val foreignIntent =
                        intent.copy()

                    return DefaultRuntimeStrategyDeriver()
                        .derive(
                            foreignIntent
                        )
                }
            }

        val result =
            DefaultRuntimeAutonomousCognitionPipeline(
                reasoningPipeline =
                    fixedReasoningPipeline(
                        reasoning
                    ),
                intentDeriver =
                    intentDeriver,
                strategyDeriver =
                    strategyDeriver
            )
                .process(
                    intelligence
                )

        assertFalse(
            result.coherent,
            "cognition must reject strategy derived from a foreign intent snapshot"
        )

        assertFalse(
            result.actionable,
            "foreign intent provenance must fail closed"
        )
    }

    @Test
    fun exact_upstream_identity_chain_remains_coherent_and_actionable() {
        val intelligence =
            intelligence()

        val result =
            DefaultRuntimeAutonomousCognitionPipeline(
                reasoningPipeline =
                    DefaultRuntimeAutonomousReasoningPipeline(
                        goalDeriver =
                            DefaultRuntimeGoalDeriver(),
                        planner =
                            DefaultRuntimePlanner(),
                        reasoningAnalyzer =
                            DefaultRuntimeReasoningAnalyzer()
                    ),
                intentDeriver =
                    DefaultRuntimeIntentDeriver(),
                strategyDeriver =
                    DefaultRuntimeStrategyDeriver()
            )
                .process(
                    intelligence
                )

        assertSame(
            result.reasoningResult,
            result.intent.reasoningResult
        )

        assertSame(
            result.intent,
            result.strategy.intent
        )

        assertTrue(
            result.coherent
        )

        assertTrue(
            result.actionable
        )
    }

    @Test
    fun equal_but_distinct_upstream_snapshot_is_not_treated_as_same_identity() {
        val intelligence =
            intelligence()

        val reasoning =
            reasoning(intelligence)

        val equalButDistinct =
            reasoning.copy()

        assertTrue(
            reasoning == equalButDistinct
        )

        assertFalse(
            reasoning === equalButDistinct
        )

        val intent =
            DefaultRuntimeIntentDeriver()
                .derive(
                    equalButDistinct
                )

        val result =
            DefaultRuntimeAutonomousCognitionPipeline(
                reasoningPipeline =
                    fixedReasoningPipeline(
                        reasoning
                    ),
                intentDeriver =
                    object : RuntimeIntentDeriver {
                        override fun derive(
                            reasoningResult:
                                RuntimeAutonomousReasoningResult
                        ): RuntimeIntent =
                            intent
                    },
                strategyDeriver =
                    DefaultRuntimeStrategyDeriver()
            )
                .process(
                    intelligence
                )

        assertFalse(
            result.coherent
        )

        assertFalse(
            result.actionable
        )
    }

    private fun intelligence():
        RuntimeIntelligenceOrchestrationResult {

        return RuntimeIntelligenceFixture.result(
            significance =
                RuntimeMeaningSignificance.WARNING,
            confidence = 0.80
        )
    }

    private fun reasoning(
        intelligence:
            RuntimeIntelligenceOrchestrationResult
    ): RuntimeAutonomousReasoningResult {

        return DefaultRuntimeAutonomousReasoningPipeline(
            goalDeriver =
                DefaultRuntimeGoalDeriver(),
            planner =
                DefaultRuntimePlanner(),
            reasoningAnalyzer =
                DefaultRuntimeReasoningAnalyzer()
        )
            .process(
                intelligence
            )
    }

    private fun fixedReasoningPipeline(
        result:
            RuntimeAutonomousReasoningResult
    ): RuntimeAutonomousReasoningPipeline {

        return object :
            RuntimeAutonomousReasoningPipeline {

            override fun process(
                intelligence:
                    RuntimeIntelligenceOrchestrationResult
            ): RuntimeAutonomousReasoningResult {

                return result
            }
        }
    }
}
