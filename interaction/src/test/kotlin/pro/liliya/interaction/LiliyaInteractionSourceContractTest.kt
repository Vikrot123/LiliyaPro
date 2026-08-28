package pro.liliya.interaction

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LiliyaInteractionSourceContractTest {

    @Test
    fun source_is_trimmed_before_runtime_processing() {
        assertEquals(
            "hello Liliya",
            LiliyaInteractionSource.normalize(
                "   hello Liliya   "
            )
        )
    }

    @Test
    fun blank_source_is_rejected() {
        assertFailsWith<IllegalArgumentException> {
            LiliyaInteractionSource.normalize(
                "     "
            )
        }
    }
}
