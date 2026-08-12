package pro.liliya.core

sealed class RuntimeEvent {

    data object SystemStart : RuntimeEvent()

    data object RuntimeStarting : RuntimeEvent()

    data object RuntimeReady : RuntimeEvent()

    data class ModuleFailed(
        val moduleName: String,
        val reason: String
    ) : RuntimeEvent()

    data class RuntimeFailed(
        val reason: String
    ) : RuntimeEvent()

    data object SystemStop : RuntimeEvent()
}
