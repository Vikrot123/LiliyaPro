package pro.liliya.core.runtime.intelligence.selfmodel

import pro.liliya.core.runtime.intelligence.context.RuntimeContextMetadata
import pro.liliya.core.runtime.intelligence.context.RuntimeContextSnapshot

data class RuntimeSelfModel(

    val snapshot: RuntimeContextSnapshot,

    val metadata: RuntimeContextMetadata

)
