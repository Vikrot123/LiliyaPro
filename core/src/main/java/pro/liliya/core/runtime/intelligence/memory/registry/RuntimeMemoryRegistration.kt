package pro.liliya.core.runtime.intelligence.memory.registry

import pro.liliya.core.runtime.intelligence.memory.api.RuntimeMemoryProvider
import pro.liliya.core.runtime.intelligence.memory.capability.RuntimeMemoryCapability

data class RuntimeMemoryRegistration(
    val provider: RuntimeMemoryProvider,
    val capabilities: Set<RuntimeMemoryCapability>
)
