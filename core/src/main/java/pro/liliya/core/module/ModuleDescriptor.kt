package pro.liliya.core.module

data class ModuleDescriptor(
    val name: String,
    val version: String,
    val critical: Boolean,
    val dependencies: List<String> = emptyList()
)
