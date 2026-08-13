package pro.liliya.core

class DefaultCoreRuntimeDiagnosticsService(
    private val provider: CoreDiagnosticProvider
) : CoreRuntimeDiagnosticsService {

    override fun snapshot(): CoreRuntimeDiagnosticsSnapshot {
        return provider.snapshot()
            .toRuntimeDiagnosticsSnapshot()
    }
}
