package pro.liliya.core.cognition

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertTrue

import pro.liliya.core.event.EventProcessor
import pro.liliya.core.memory.InMemoryMemoryRepository
import pro.liliya.core.memory.MemoryCoordinator

import pro.liliya.domain.api.EventBus
import pro.liliya.domain.api.EventStore
import pro.liliya.domain.api.ExecutiveController
import pro.liliya.domain.api.PlanningEngine
import pro.liliya.domain.api.ReasoningEngine
import pro.liliya.domain.api.ReflectionEngine

import pro.liliya.domain.models.Episode
import pro.liliya.domain.models.Plan
import pro.liliya.domain.models.ReasoningResult
import pro.liliya.domain.models.SystemEvent


class CognitiveEngineTest {

    @Test
    fun `cognitive engine should process input`() = runBlocking {

        val eventProcessor = EventProcessor(
            eventBus = FakeEventBus(),
            eventStore = FakeEventStore()
        )

        val memoryCoordinator = MemoryCoordinator(
            repository = InMemoryMemoryRepository()
        )

        val engine = CognitiveEngineImpl(
            controller = FakeController(),
            eventProcessor = eventProcessor,
            reasoningEngine = FakeReasoningEngine(),
            planningEngine = FakePlanningEngine(),
            reflectionEngine = FakeReflectionEngine(),
            memoryCoordinator = memoryCoordinator
        )

        val result = engine
            .process("Hello LiliyaPro")
            .toList()

        assertTrue(result.isNotEmpty())
    }
}


class FakeController : ExecutiveController {

    override suspend fun processInput(input: String) =
        flow {
            emit("Response: $input")
        }

    override suspend fun pause() {
    }

    override suspend fun resume() {
    }
}


class FakeEventBus : EventBus {

    private val eventsList = mutableListOf<SystemEvent>()

    override suspend fun publish(event: SystemEvent) {
        eventsList.add(event)
    }

    override fun events() =
        flow {
            eventsList.forEach {
                emit(it)
            }
        }
}


class FakeEventStore : EventStore {

    private val events = mutableListOf<SystemEvent>()

    override suspend fun save(event: SystemEvent) {
        events.add(event)
    }

    override suspend fun getEvents(): List<SystemEvent> {
        return events
    }

    override suspend fun clear() {
        events.clear()
    }
}


class FakeReasoningEngine : ReasoningEngine {

    override suspend fun reason(
        input: String
    ): ReasoningResult {

        return ReasoningResult(
            summary = "Reasoning completed",
            confidence = 1.0f
        )
    }
}


class FakePlanningEngine : PlanningEngine {

    override suspend fun createPlan(
        reasoning: ReasoningResult
    ): Plan {

        return Plan(
            steps = listOf("Test step")
        )
    }
}


class FakeReflectionEngine : ReflectionEngine {

    override suspend fun reflect(
        episode: Episode
    ): String {

        return "Reflection completed"
    }
}
