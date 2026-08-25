package pro.liliya.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContext
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSnapshot
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextSource
import pro.liliya.core.runtime.intelligence.context.cognitive.CognitiveContextType

class CognitiveContextFoundationContractTest {

    @Test
    fun context_type_should_define_required_context_levels() {
        assertTrue(CognitiveContextType.values().contains(CognitiveContextType.GLOBAL))
        assertTrue(CognitiveContextType.values().contains(CognitiveContextType.SESSION))
        assertTrue(CognitiveContextType.values().contains(CognitiveContextType.TASK))
        assertTrue(CognitiveContextType.values().contains(CognitiveContextType.WORKING))
        assertTrue(CognitiveContextType.values().contains(CognitiveContextType.PROCESSOR))
        assertTrue(CognitiveContextType.values().contains(CognitiveContextType.TEMPORARY))
    }

    @Test
    fun snapshot_should_be_created() {
        val snapshot = CognitiveContextSnapshot(
            type = CognitiveContextType.TASK
        )

        assertNotNull(snapshot)
        assertEquals(CognitiveContextType.TASK, snapshot.type)
    }

    @Test
    fun source_should_be_able_to_provide_snapshot() {
        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot? {
                return CognitiveContextSnapshot(type)
            }
        }

        assertNotNull(source.snapshot(CognitiveContextType.WORKING))
    }

    @Test
    fun context_contract_should_expose_snapshot_and_sources() {
        val source = object : CognitiveContextSource {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot? {
                return CognitiveContextSnapshot(type)
            }
        }

        val context = object : CognitiveContext {
            override fun snapshot(
                type: CognitiveContextType
            ): CognitiveContextSnapshot {
                return source.snapshot(type)!!
            }

            override fun sources(): List<CognitiveContextSource> {
                return listOf(source)
            }
        }

        assertEquals(
            CognitiveContextType.TASK,
            context.snapshot(CognitiveContextType.TASK).type
        )

        assertEquals(1, context.sources().size)
    }
}
