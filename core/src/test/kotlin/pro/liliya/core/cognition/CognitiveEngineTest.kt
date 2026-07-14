package pro.liliya.core.cognition

import kotlinx.coroutines.runBlocking
import pro.liliya.core.event.EventBusImpl
import pro.liliya.core.event.EventProcessor
import pro.liliya.core.event.InMemoryEventStoreImpl
import pro.liliya.core.orchestration.ExecutiveControllerImpl
import pro.liliya.core.reflection.ReflectionEngineImpl
import pro.liliya.model.mock.MockModelEngine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CognitiveEngineTest {

    @Test
    fun `cognitive engine should observe remember think and reflect`() = runBlocking {

        val model = MockModelEngine()

        model.loadModel()

        val controller = ExecutiveControllerImpl(
            modelEngine = model
        )

        val processor = EventProcessor(
            eventBus = EventBusImpl(),
            eventStore = InMemoryEventStoreImpl()
        )

        val reflectionEngine = ReflectionEngineImpl()

        val engine = CognitiveEngineImpl(
            controller = controller,
            eventProcessor = processor,
            reflectionEngine = reflectionEngine
        )

        val responses = mutableListOf<String>()

        engine.process("Привет")
            .collect {
                responses.add(it)
            }

        assertTrue(
            responses.isNotEmpty()
        )

        assertTrue(
            responses.any {
                it.contains("Reflection")
            }
        )

        assertEquals(
            1,
            processor.history().size
        )
    }
}
