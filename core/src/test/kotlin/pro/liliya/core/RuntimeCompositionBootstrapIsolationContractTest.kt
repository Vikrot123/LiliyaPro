package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertEquals
import pro.liliya.core.runtime.composition.DefaultRuntimeComposition

class RuntimeCompositionBootstrapIsolationContractTest {

    @Test
    fun composition_reset_creates_clean_bootstrap_instance() {
        val composition = DefaultRuntimeComposition()

        val first = composition.serviceBootstrap()

        first.start()
        first.stop()

        composition.resetRuntimeServiceConfiguration()

        val second = composition.serviceBootstrap()

        assertNotSame(first, second)

        second.start()

        assertEquals(
            emptyMap(),
            second.getRecoverySnapshot().restartCounts
        )

        second.stop()
    }
}
