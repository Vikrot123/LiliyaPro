package pro.liliya.core.runtime.intelligence.autonomous

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext

interface RuntimeAutonomousIntelligenceCyclePipeline {

    fun process(
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeAutonomousIntelligenceCyclePipelineResult
}
