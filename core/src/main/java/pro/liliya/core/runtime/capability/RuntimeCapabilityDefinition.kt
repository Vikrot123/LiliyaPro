package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

data class RuntimeCapabilityDefinition(
    val command: RuntimeCommand,
    val minimumAuthority: RuntimeAuthorityLevel,
    val description: String
)
