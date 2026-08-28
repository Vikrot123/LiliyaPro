package pro.liliya.interaction

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.intelligence.autonomous.RuntimeAutonomousIntelligenceCyclePipelineResult

interface LiliyaInteractionPort {

    fun process(
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeAutonomousIntelligenceCyclePipelineResult
}
