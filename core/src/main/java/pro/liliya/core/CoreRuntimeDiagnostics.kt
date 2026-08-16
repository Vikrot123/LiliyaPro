package pro.liliya.core

class CoreRuntimeDiagnostics(
    private val source: CoreDiagnosticSource = CoreDiagnosticProvider()
) {

    private val diagnostics =
        CoreDiagnostics(source)

    fun snapshot(): CoreDiagnosticSnapshot {
        return diagnostics.snapshot()
    }
}
