package pro.liliya.core.runtime.orchestration

interface RuntimeEventController {

    fun publishSystemStart()

    fun publishRuntimeStarting()

    fun publishRuntimeReady()

    fun publishRuntimeFailed(reason: String)

    fun publishSystemStop()

    fun publishModuleFailed(
        moduleName: String,
        reason: String
    )

    fun publishRuntimeStartedDiagnostic()

    fun publishRuntimeFailedDiagnostic()

    fun publishRuntimeStoppedDiagnostic()
}
