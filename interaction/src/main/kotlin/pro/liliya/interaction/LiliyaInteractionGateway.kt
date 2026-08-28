package pro.liliya.interaction

import pro.liliya.core.runtime.intelligence.autonomous.RuntimeAutonomousIntelligenceCyclePipelineResult

class LiliyaInteractionGateway(
    private val port: LiliyaInteractionPort
) {

    fun process(
        request: LiliyaInteractionRequest
    ): RuntimeAutonomousIntelligenceCyclePipelineResult {
        val source =
            LiliyaInteractionSource.normalize(
                request.source
            )

        val authority =
            request.authority.copy(
                source = source
            )

        return port.process(
            source = source,
            authority = authority
        )
    }
}
