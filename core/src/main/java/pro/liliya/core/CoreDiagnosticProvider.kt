package pro.liliya.core

class CoreDiagnosticProvider(
    private val stateProvider: () -> CoreDiagnosticSnapshot = {
        CoreRuntime.snapshot()
    }
) : CoreDiagnosticSource {

    override fun snapshot(): CoreDiagnosticSnapshot {
        return stateProvider()
    }
}
