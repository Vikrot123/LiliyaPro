package pro.liliya.core.runtime.orchestration

import pro.liliya.core.CoreDiagnosticEventBus
import pro.liliya.core.CoreDiagnosticSnapshot
import pro.liliya.core.runtime.health.RuntimeFailureTracker

interface RuntimeEventComposition {

    fun diagnosticEventBus(): CoreDiagnosticEventBus

    fun createDiagnosticSnapshot(): CoreDiagnosticSnapshot

    fun failureTracker(): RuntimeFailureTracker
}
