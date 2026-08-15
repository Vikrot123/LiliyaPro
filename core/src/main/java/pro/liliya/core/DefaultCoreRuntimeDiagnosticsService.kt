package pro.liliya.core

class DefaultCoreRuntimeDiagnosticsService(
    private val diagnostics: CoreRuntimeDiagnostics
) : CoreRuntimeDiagnosticsService {

    override fun snapshot(): CoreRuntimeDiagnosticsSnapshot {
        return diagnostics.snapshot()
            .toRuntimeDiagnosticsSnapshot()
    }
}
