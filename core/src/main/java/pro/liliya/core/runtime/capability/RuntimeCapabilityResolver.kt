package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeCapabilityResolver(
    private val registry: RuntimeCapabilityRegistry =
        DefaultRuntimeCapabilityRegistry(),

    private val authorityEvaluator:
        RuntimeCapabilityAuthorityEvaluator =
        DefaultRuntimeCapabilityAuthorityEvaluator()
) {

    fun resolve(
        level: RuntimeAuthorityLevel,
        command: RuntimeCommand
    ): RuntimeCapability {

        val definition = registry.find(command)

        if (definition == null) {
            return RuntimeCapability(
                command = command,
                allowed = false,
                description = "No capability registered",
                requiredAuthority = null,
                actualAuthority = level
            )
        }

        val allowed = authorityEvaluator.isAllowed(
            actualAuthority = level,
            requiredAuthority = definition.minimumAuthority
        )

        return RuntimeCapability(
            command = command,
            allowed = allowed,
            description =
                if (allowed) {
                    definition.description
                } else {
                    "Authority level insufficient for capability"
                },
            requiredAuthority = definition.minimumAuthority,
            actualAuthority = level
        )
    }
}
