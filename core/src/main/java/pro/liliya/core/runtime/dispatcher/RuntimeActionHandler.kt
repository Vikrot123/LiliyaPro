package pro.liliya.core.runtime.dispatcher

import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.action.RuntimeActionResult

interface RuntimeActionHandler {

    fun supports(
        request: RuntimeActionRequest
    ): Boolean

    fun handle(
        request: RuntimeActionRequest
    ): RuntimeActionResult
}
