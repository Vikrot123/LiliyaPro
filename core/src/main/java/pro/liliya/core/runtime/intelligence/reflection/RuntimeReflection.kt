package pro.liliya.core.runtime.intelligence.reflection

import pro.liliya.core.runtime.intelligence.selfmodel.RuntimeSelfModel

interface RuntimeReflection {

    fun analyze(
        selfModel: RuntimeSelfModel
    ): RuntimeReflectionSnapshot

}
