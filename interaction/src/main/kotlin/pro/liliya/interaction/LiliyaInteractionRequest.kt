package pro.liliya.interaction

import pro.liliya.core.runtime.authority.RuntimeActionAuthorityContext

data class LiliyaInteractionRequest(
    val source: String,
    val authority: RuntimeActionAuthorityContext
)
