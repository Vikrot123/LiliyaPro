package pro.liliya.core

class CoreRuntimeDiagnostics {

    private val diagnostics =
        CoreDiagnostics(
            CoreDiagnosticProvider()
        )

    fun snapshot(): CoreDiagnosticSnapshot {
        return diagnostics.snapshot()
    }
}
