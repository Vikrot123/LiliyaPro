package pro.liliya.core.memory

import kotlinx.coroutines.runBlocking
import pro.liliya.core.event.InMemoryEventStoreImpl
import pro.liliya.domain.models.SystemEvent
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryConsolidatorTest {

    @Test
    fun `events should become memory episode`() = runBlocking {

        val store = InMemoryEventStoreImpl()

        val memory = InMemoryMemoryProvider()

        store.save(
            SystemEvent.UserMessageReceived(
                message = "Привет"
            )
        )

        val consolidator = MemoryConsolidator(
            eventStore = store,
            memoryProvider = memory
        )

        consolidator.consolidate()

        val episodes = memory.loadEpisodes()

        assertEquals(
            1,
            episodes.size
        )

        assertEquals(
            1,
            episodes.first().events.size
        )
    }
}
