package pro.liliya.core.runtime.control

import pro.liliya.core.runtime.action.RuntimeActionRequest

interface RuntimeControl {

    fun execute(
        request: RuntimeActionRequest
    ): RuntimeControlResult
}
