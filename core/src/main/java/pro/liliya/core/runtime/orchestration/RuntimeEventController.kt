package pro.liliya.core.runtime.orchestration

interface RuntimeEventController {

    fun publishSystemStart()

    fun publishRuntimeStarting()

    fun publishRuntimeReady()

    fun publishRuntimeFailed(reason: String)

    fun publishSystemStop()
}
