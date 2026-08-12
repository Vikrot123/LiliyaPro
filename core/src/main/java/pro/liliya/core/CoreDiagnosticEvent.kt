package pro.liliya.core

enum class CoreDiagnosticEventType {
    RUNTIME_STARTED,
    RUNTIME_STOPPED,
    RUNTIME_FAILED
}

data class CoreDiagnosticEvent(
    val type: CoreDiagnosticEventType,
    val snapshot: CoreDiagnosticSnapshot
)
