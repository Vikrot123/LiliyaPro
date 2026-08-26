package pro.liliya.core

class CoreRuntimeContext(
    val diagnosticService: CoreRuntimeDiagnosticsService =
        DefaultCoreRuntimeDiagnosticsService(
            CoreDiagnosticProvider()
        )
)
