package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.control.RuntimeCommand

data class RuntimeCapability(
    val command: RuntimeCommand,
    val allowed: Boolean,
    val description: String
)
