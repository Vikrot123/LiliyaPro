package pro.liliya.core

class DefaultCoreRuntimeDiagnosticsService(
    private val source: CoreDiagnosticSource
) : CoreRuntimeDiagnosticsService {

    override fun snapshot(): CoreRuntimeDiagnosticsSnapshot {
        return source
            .snapshot()
            .toRuntimeDiagnosticsSnapshot()
    }
}
