package pro.liliya.core

class CoreDiagnosticProvider {

    fun snapshot(): CoreDiagnosticSnapshot {
        return CoreRuntime.snapshot()
    }
}
