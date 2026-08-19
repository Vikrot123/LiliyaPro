package pro.liliya.core.runtime.action

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

data class RuntimeActionRequest(
    val command: RuntimeCommand,
    val source: String = "unknown",
    val reason: String = "unspecified",
    val target: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val authority: RuntimeActionAuthorityContext? = null
) {
    fun resolvedAuthority(): RuntimeActionAuthorityContext {
        return authority
            ?: RuntimeActionAuthorityContext(
                source = source,
                level = RuntimeAuthorityLevel.UNKNOWN
            )
    }
}
