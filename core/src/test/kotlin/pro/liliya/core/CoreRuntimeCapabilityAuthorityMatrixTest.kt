package pro.liliya.core

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import pro.liliya.core.runtime.authority.RuntimeAuthorityLevel
import pro.liliya.core.runtime.capability.DefaultRuntimeCapabilityAuthorityEvaluator

class CoreRuntimeCapabilityAuthorityMatrixTest {

    private val evaluator =
        DefaultRuntimeCapabilityAuthorityEvaluator()

    @Test
    fun `internal authority allows every capability`() {
        assertTrue(
            evaluator.isAllowed(
                RuntimeAuthorityLevel.INTERNAL,
                RuntimeAuthorityLevel.SYSTEM
            )
        )

        assertTrue(
            evaluator.isAllowed(
                RuntimeAuthorityLevel.INTERNAL,
                RuntimeAuthorityLevel.USER
            )
        )
    }

    @Test
    fun `system authority allows system and user capabilities`() {
        assertTrue(
            evaluator.isAllowed(
                RuntimeAuthorityLevel.SYSTEM,
                RuntimeAuthorityLevel.SYSTEM
            )
        )

        assertTrue(
            evaluator.isAllowed(
                RuntimeAuthorityLevel.SYSTEM,
                RuntimeAuthorityLevel.USER
            )
        )
    }

    @Test
    fun `user authority allows only user capabilities`() {
        assertTrue(
            evaluator.isAllowed(
                RuntimeAuthorityLevel.USER,
                RuntimeAuthorityLevel.USER
            )
        )

        assertFalse(
            evaluator.isAllowed(
                RuntimeAuthorityLevel.USER,
                RuntimeAuthorityLevel.SYSTEM
            )
        )

        assertFalse(
            evaluator.isAllowed(
                RuntimeAuthorityLevel.USER,
                RuntimeAuthorityLevel.INTERNAL
            )
        )
    }

    @Test
    fun `unknown authority has no capabilities`() {
        assertFalse(
            evaluator.isAllowed(
                RuntimeAuthorityLevel.UNKNOWN,
                RuntimeAuthorityLevel.USER
            )
        )

        assertFalse(
            evaluator.isAllowed(
                RuntimeAuthorityLevel.UNKNOWN,
                RuntimeAuthorityLevel.SYSTEM
            )
        )
    }
}
