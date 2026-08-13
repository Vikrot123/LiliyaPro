package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

class RuntimeCapabilityResolver {

    fun resolve(
        level: RuntimeAuthorityLevel,
        command: RuntimeCommand
    ): RuntimeCapability {

        return when (level) {

            RuntimeAuthorityLevel.INTERNAL ->
                RuntimeCapability(
                    command = command,
                    allowed = true,
                    description = "Internal runtime authority"
                )

            RuntimeAuthorityLevel.SYSTEM ->
                RuntimeCapability(
                    command = command,
                    allowed = true,
                    description = "System runtime authority"
                )

            RuntimeAuthorityLevel.USER ->
                when (command) {

                    RuntimeCommand.HEALTH_CHECK ->
                        RuntimeCapability(
                            command = command,
                            allowed = true,
                            description = "User health capability"
                        )

                    else ->
                        RuntimeCapability(
                            command = command,
                            allowed = false,
                            description = "User capability denied"
                        )
                }

            RuntimeAuthorityLevel.UNKNOWN ->
                RuntimeCapability(
                    command = command,
                    allowed = false,
                    description = "Unknown authority has no capabilities"
                )
        }
    }
}
