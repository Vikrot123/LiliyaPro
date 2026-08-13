package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeCapabilityResolver(
    private val registry: RuntimeCapabilityRegistry =
        DefaultRuntimeCapabilityRegistry()
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
                description = "No capability registered"
            )
        }

        val allowed = when (level) {
            RuntimeAuthorityLevel.INTERNAL ->
                true

            RuntimeAuthorityLevel.SYSTEM ->
                definition.minimumAuthority != RuntimeAuthorityLevel.INTERNAL

            RuntimeAuthorityLevel.USER ->
                definition.minimumAuthority == RuntimeAuthorityLevel.USER

            RuntimeAuthorityLevel.UNKNOWN ->
                false
        }

        return RuntimeCapability(
            command = command,
            allowed = allowed,
            description =
                if (allowed) {
                    definition.description
                } else {
                    "Authority level insufficient for capability"
                }
        )
    }
}
