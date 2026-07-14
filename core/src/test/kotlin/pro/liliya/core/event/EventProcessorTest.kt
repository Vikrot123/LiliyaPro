package pro.liliya.core.event

import kotlinx.coroutines.runBlocking
import pro.liliya.domain.models.SystemEvent
import kotlin.test.Test
import kotlin.test.assertEquals

class EventProcessorTest {

    @Test
    fun `event should be stored and published`() = runBlocking {

        val bus = EventBusImpl()

        val store = InMemoryEventStoreImpl()

        val processor = EventProcessor(
            eventBus = bus,
            eventStore = store
        )

        val event = SystemEvent.UserMessageReceived(
            message = "Привет, LiliyaPro"
        )

        processor.process(event)

        val history = processor.history()

        assertEquals(
            1,
            history.size
        )

        assertEquals(
            "Привет, LiliyaPro",
            (history.first() as SystemEvent.UserMessageReceived).message
        )
    }
}
