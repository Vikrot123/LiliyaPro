package pro.liliya.core

class CoreDiagnostics(
    private val source: CoreDiagnosticSource
) {

    fun snapshot(): CoreDiagnosticSnapshot {
        return source.snapshot()
    }
}
