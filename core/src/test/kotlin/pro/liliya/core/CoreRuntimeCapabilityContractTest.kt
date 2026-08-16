package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.capability.RuntimeCapabilityResolver
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityRegistry
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityAuthorityEvaluator
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.control.RuntimeCommand

class CoreRuntimeCapabilityContractTest {

    private val resolver = RuntimeCapabilityResolver(
        registry = DefaultRuntimeCapabilityRegistry(),
        authorityEvaluator = DefaultRuntimeCapabilityAuthorityEvaluator()
    )

    @Test
    fun `user authority allows health check capability`() {

        val capability = resolver.resolve(
            RuntimeAuthorityLevel.USER,
            RuntimeCommand.HEALTH_CHECK
        )

        assertTrue(capability.allowed)
    }


    @Test
    fun `user authority denies unsupported capability`() {

        val capability = resolver.resolve(
            RuntimeAuthorityLevel.USER,
            RuntimeCommand.START
        )

        assertFalse(capability.allowed)
    }


    @Test
    fun `unknown authority has no capability`() {

        val capability = resolver.resolve(
            RuntimeAuthorityLevel.UNKNOWN,
            RuntimeCommand.HEALTH_CHECK
        )

        assertFalse(capability.allowed)
    }


    @Test
    fun `system authority has runtime capability`() {

        val capability = resolver.resolve(
            RuntimeAuthorityLevel.SYSTEM,
            RuntimeCommand.START
        )

        assertTrue(capability.allowed)
    }
}
