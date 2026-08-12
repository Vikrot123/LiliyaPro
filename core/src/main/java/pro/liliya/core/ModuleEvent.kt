package pro.liliya.core

sealed class ModuleEvent {

    data class Loaded(
        val moduleName: String
    ) : ModuleEvent()

    data class Initialized(
        val moduleName: String
    ) : ModuleEvent()

    data class Started(
        val moduleName: String
    ) : ModuleEvent()

    data class Stopped(
        val moduleName: String
    ) : ModuleEvent()

    data class Failed(
        val moduleName: String,
        val phase: String,
        val reason: String
    ) : ModuleEvent()
}
