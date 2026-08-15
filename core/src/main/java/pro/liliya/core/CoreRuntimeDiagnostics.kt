package pro.liliya.core

class CoreRuntimeDiagnostics(
    private val source: CoreDiagnosticSource
) {

    private val diagnostics =
        CoreDiagnostics(source)

    fun snapshot(): CoreDiagnosticSnapshot {
        return diagnostics.snapshot()
    }
}
