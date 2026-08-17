package pro.liliya.core.runtime.orchestration

import pro.liliya.core.CoreDiagnosticEvent
import pro.liliya.core.CoreDiagnosticEventType
import pro.liliya.core.RuntimeEvent
import pro.liliya.core.RuntimeEventBus

class DefaultRuntimeEventController(
    private val composition: RuntimeEventComposition
) : RuntimeEventController {

    override fun publishSystemStart() {
        RuntimeEventBus.publish(
            RuntimeEvent.SystemStart
        )
    }

    override fun publishRuntimeStarting() {
        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeStarting
        )
    }

    override fun publishRuntimeReady() {
        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeReady
        )
    }

    override fun publishRuntimeFailed(reason: String) {
        RuntimeEventBus.publish(
            RuntimeEvent.RuntimeFailed(reason)
        )
    }

    override fun publishSystemStop() {
        RuntimeEventBus.publish(
            RuntimeEvent.SystemStop
        )
    }

    override fun publishModuleFailed(
        moduleName: String,
        reason: String
    ) {
        composition.failureTracker().recordFailure(
            reason = reason,
            module = moduleName
        )

        RuntimeEventBus.publish(
            RuntimeEvent.ModuleFailed(
                moduleName = moduleName,
                reason = reason
            )
        )
    }

    override fun publishRuntimeStartedDiagnostic() {
        composition.diagnosticEventBus().publish(
            CoreDiagnosticEvent(
                type = CoreDiagnosticEventType.RUNTIME_STARTED,
                snapshot = composition.createDiagnosticSnapshot()
            )
        )
    }

    override fun publishRuntimeFailedDiagnostic() {
        composition.diagnosticEventBus().publish(
            CoreDiagnosticEvent(
                type = CoreDiagnosticEventType.RUNTIME_FAILED,
                snapshot = composition.createDiagnosticSnapshot()
            )
        )
    }

    override fun publishRuntimeStoppedDiagnostic() {
        composition.diagnosticEventBus().publish(
            CoreDiagnosticEvent(
                type = CoreDiagnosticEventType.RUNTIME_STOPPED,
                snapshot = composition.createDiagnosticSnapshot()
            )
        )
    }
}
