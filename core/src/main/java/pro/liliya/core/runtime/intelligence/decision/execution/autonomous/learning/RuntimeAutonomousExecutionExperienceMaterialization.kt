package pro.liliya.core.runtime.intelligence.decision.execution.autonomous.learning

import pro.liliya.core.CoreRuntimeState
import pro.liliya.core.runtime.control.RuntimeCommand
import pro.liliya.core.runtime.intelligence.decision.execution.autonomous.reflection.RuntimeAutonomousExecutionReflectionAnalysisResult

data class RuntimeAutonomousExecutionExperienceMaterialization(
    val state: RuntimeAutonomousExecutionExperienceMaterializationState,
    val analysis: RuntimeAutonomousExecutionReflectionAnalysisResult,
    val novelty: RuntimeAutonomousExecutionExperienceNovelty,
    val command: RuntimeCommand?,
    val actionSucceeded: Boolean?,
    val previousRuntimeState: CoreRuntimeState?,
    val currentRuntimeState: CoreRuntimeState?,
    val message: String?,
    val description: String
)
