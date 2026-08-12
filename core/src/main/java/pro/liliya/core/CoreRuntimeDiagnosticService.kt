package pro.liliya.core

class CoreRuntimeDiagnosticService(
    private val diagnostics: CoreRuntimeDiagnostics = CoreRuntimeDiagnostics()
) {

    fun snapshot(): CoreDiagnosticSnapshot {
        return diagnostics.snapshot()
    }
}
