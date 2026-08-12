package pro.liliya.core

class CoreDiagnosticProvider : CoreDiagnosticSource {

    override fun snapshot(): CoreDiagnosticSnapshot {
        return CoreRuntime.snapshot()
    }
}
