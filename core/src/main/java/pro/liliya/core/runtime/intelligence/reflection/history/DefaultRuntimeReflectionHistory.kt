package pro.liliya.core.runtime.intelligence.reflection.history

import pro.liliya.core.runtime.intelligence.reflection.RuntimeReflectionSnapshot

class DefaultRuntimeReflectionHistory : RuntimeReflectionHistory {

    private val history =
        mutableListOf<RuntimeReflectionSnapshot>()

    override fun record(
        snapshot: RuntimeReflectionSnapshot
    ) {
        history += snapshot
    }

    override fun snapshots(): List<RuntimeReflectionSnapshot> {
        return history.toList()
    }
}
