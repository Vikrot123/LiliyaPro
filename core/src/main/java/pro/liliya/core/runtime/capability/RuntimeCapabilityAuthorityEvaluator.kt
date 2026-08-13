package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

interface RuntimeCapabilityAuthorityEvaluator {

    fun isAllowed(
        actualAuthority: RuntimeAuthorityLevel,
        requiredAuthority: RuntimeAuthorityLevel
    ): Boolean
}
