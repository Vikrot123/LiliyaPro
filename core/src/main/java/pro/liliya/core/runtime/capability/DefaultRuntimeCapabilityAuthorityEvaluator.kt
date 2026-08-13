package pro.liliya.core.runtime.capability

import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel

class DefaultRuntimeCapabilityAuthorityEvaluator :
    RuntimeCapabilityAuthorityEvaluator {

    override fun isAllowed(
        actualAuthority: RuntimeAuthorityLevel,
        requiredAuthority: RuntimeAuthorityLevel
    ): Boolean {

        return when (actualAuthority) {
            RuntimeAuthorityLevel.INTERNAL -> true

            RuntimeAuthorityLevel.SYSTEM ->
                requiredAuthority != RuntimeAuthorityLevel.INTERNAL

            RuntimeAuthorityLevel.USER ->
                requiredAuthority == RuntimeAuthorityLevel.USER

            RuntimeAuthorityLevel.UNKNOWN ->
                false
        }
    }
}
