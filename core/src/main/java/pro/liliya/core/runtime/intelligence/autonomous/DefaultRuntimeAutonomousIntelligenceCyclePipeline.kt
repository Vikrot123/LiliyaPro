package pro.liliya.core.runtime.intelligence.autonomous

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.cycle.RuntimeAutonomousExecutionCyclePipeline
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrator

class DefaultRuntimeAutonomousIntelligenceCyclePipeline(
    private val intelligenceOrchestrator:
        RuntimeIntelligenceOrchestrator,
    private val executionCyclePipeline:
        RuntimeAutonomousExecutionCyclePipeline
) : RuntimeAutonomousIntelligenceCyclePipeline {

    override fun process(
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeAutonomousIntelligenceCyclePipelineResult {

        val intelligence =
            intelligenceOrchestrator.process()

        val executionCycle =
            executionCyclePipeline.process(
                intelligence = intelligence,
                source = source,
                authority = authority
            )

        return RuntimeAutonomousIntelligenceCyclePipelineResult(
            intelligence =
                intelligence,
            executionCycle =
                executionCycle
        )
    }
}
