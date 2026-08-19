package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import pro.liliya.core.runtime.action.RuntimeActionRequest
import pro.liliya.core.runtime.action.RuntimeActionResult
import pro.liliya.core.runtime.dispatcher.RuntimeActionHandler
import pro.liliya.core.runtime.dispatcher.RuntimeActionHandlerRegistry

class RuntimeActionHandlerRegistryDuplicateContractTest {

    @Test
    fun duplicate_handler_registration_is_not_allowed() {
        val registry = RuntimeActionHandlerRegistry()

        val handler = object : RuntimeActionHandler {

            override fun supports(
                request: RuntimeActionRequest
            ): Boolean {
                return true
            }

            override fun handle(
                request: RuntimeActionRequest
            ): RuntimeActionResult {
                throw UnsupportedOperationException()
            }
        }

        registry.register(handler)
        registry.register(handler)

        assertEquals(
            1,
            registry.snapshot().size
        )
    }
}
