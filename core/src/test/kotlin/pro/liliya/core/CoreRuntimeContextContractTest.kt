package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CoreRuntimeContextContractTest {

    @Test
    fun contextProvidesRuntimeDiagnostics() {

        val context = CoreRuntimeContext()

        assertNotNull(
            context.diagnosticEventBus
        )

        assertNotNull(
            context.diagnosticService
        )
    }
}
