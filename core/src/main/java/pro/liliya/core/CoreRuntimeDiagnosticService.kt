package pro.liliya.core

class CoreRuntimeDiagnosticService {

    private val diagnostics =
        CoreRuntimeDiagnostics()

    fun snapshot(): CoreDiagnosticSnapshot {
        return diagnostics.snapshot()
    }
}
