package pro.liliya.core.runtime.intelligence.autonomous

import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.cycle.RuntimeAutonomousExecutionCyclePipelineResult
import pro.liliya.core.runtime.intelligence.orchestration.RuntimeIntelligenceOrchestrationResult

data class RuntimeAutonomousIntelligenceCyclePipelineResult(
    val intelligence:
        RuntimeIntelligenceOrchestrationResult,
    val executionCycle:
        RuntimeAutonomousExecutionCyclePipelineResult
)
