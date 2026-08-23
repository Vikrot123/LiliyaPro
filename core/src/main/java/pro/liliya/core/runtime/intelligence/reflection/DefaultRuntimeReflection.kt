package pro.liliya.core.runtime.intelligence.reflection

import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

class DefaultRuntimeReflection : RuntimeReflection {

    override fun analyze(
        selfModel: RuntimeSelfModel
    ): RuntimeReflectionSnapshot {

        val healthy =
            selfModel.snapshot.runtimeState == "RUNNING" &&
            selfModel.snapshot.activeServices.isNotEmpty()

        val summary =
            if (healthy) {
                "Runtime is healthy with active services"
            } else {
                "Runtime requires attention"
            }

        return RuntimeReflectionSnapshot(
            summary = summary,
            healthy = healthy,
            analyzedAt = System.currentTimeMillis()
        )
    }
}
