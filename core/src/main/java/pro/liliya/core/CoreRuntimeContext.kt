package pro.liliya.core

class CoreRuntimeContext(
    val diagnosticEventBus: CoreDiagnosticEventBus = CoreDiagnosticEventBus(),
    val diagnosticService: CoreRuntimeDiagnosticsService =
        DefaultCoreRuntimeDiagnosticsService(
            CoreDiagnosticProvider()
        )
)
