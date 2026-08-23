package pro.liliya.core.runtime.intelligence.reflection.history

import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot

interface RuntimeReflectionHistory {

    fun record(
        snapshot: RuntimeReflectionSnapshot
    )

    fun snapshots(): List<RuntimeReflectionSnapshot>
}
