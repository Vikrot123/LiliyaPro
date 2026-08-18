package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.CoreRuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceProvider
import pro.liliya.core.runtime.RuntimeServiceBootstrap
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition
import pro.liliya.core.runtime.service.composition.DefaultRuntimeServiceComposition

class RuntimeServiceCompositionContractTest {

    @Test
    fun composition_createsRuntimeServiceBootstrapWithRecoveryOwnership() {

        val runtimeComposition = DefaultRuntimeComposition()

        val serviceComposition =
            DefaultRuntimeServiceComposition()

        val bootstrap = serviceComposition.serviceBootstrap()

        assertNotNull(bootstrap)
        assertNotNull(serviceComposition.runtimeSupervisor())
        assertNotNull(serviceComposition.runtimeRecoveryManager())
    }
}
