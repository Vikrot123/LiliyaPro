package pro.liliya.core.runtime.intelligence.context

data class RuntimeContextMetadata(

    val runtimeVersion: String,

    val recoveryAvailable: Boolean,

    val diagnosticsAvailable: Boolean

)
