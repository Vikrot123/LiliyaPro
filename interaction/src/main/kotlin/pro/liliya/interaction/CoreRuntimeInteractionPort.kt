package pro.liliya.interaction

import pro.liliya.core.CoreRuntime
import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.intelligence.autonomous.RuntimeAutonomousIntelligenceCyclePipelineResult

class CoreRuntimeInteractionPort(
    private val runtime: CoreRuntime
) : LiliyaInteractionPort {

    override fun process(
        source: String,
        authority: RuntimeActionAuthorityContext
    ): RuntimeAutonomousIntelligenceCyclePipelineResult {
        return runtime.processAutonomousIntelligenceCycle(
            source = source,
            authority = authority
        )
    }
}
