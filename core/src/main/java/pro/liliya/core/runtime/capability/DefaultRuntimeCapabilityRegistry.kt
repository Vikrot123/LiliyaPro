package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

class DefaultRuntimeCapabilityRegistry : RuntimeCapabilityRegistry {

    private val definitions = listOf(
        RuntimeCapabilityDefinition(
            command = RuntimeCommand.HEALTH_CHECK,
            minimumAuthority = RuntimeAuthorityLevel.USER,
            description = "User health capability"
        ),
        RuntimeCapabilityDefinition(
            command = RuntimeCommand.START,
            minimumAuthority = RuntimeAuthorityLevel.SYSTEM,
            description = "Runtime start capability"
        ),
        RuntimeCapabilityDefinition(
            command = RuntimeCommand.STOP,
            minimumAuthority = RuntimeAuthorityLevel.SYSTEM,
            description = "Runtime stop capability"
        ),
        RuntimeCapabilityDefinition(
            command = RuntimeCommand.RESTART,
            minimumAuthority = RuntimeAuthorityLevel.SYSTEM,
            description = "Runtime restart capability"
        ),
        RuntimeCapabilityDefinition(
            command = RuntimeCommand.RECOVER,
            minimumAuthority = RuntimeAuthorityLevel.SYSTEM,
            description = "Runtime recovery capability"
        )
    )

    override fun find(
        command: RuntimeCommand
    ): RuntimeCapabilityDefinition? {
        return definitions.find {
            it.command == command
        }
    }
}
