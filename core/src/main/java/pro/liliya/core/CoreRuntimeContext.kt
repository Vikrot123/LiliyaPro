package pro.liliya.core

class CoreRuntimeContext {

    val diagnosticEventBus =
        CoreDiagnosticEventBus()

    val diagnosticService =
        CoreRuntimeDiagnosticService()
}
