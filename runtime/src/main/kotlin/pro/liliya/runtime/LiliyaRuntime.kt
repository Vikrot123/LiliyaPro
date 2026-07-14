package pro.liliya.runtime

import kotlinx.coroutines.flow.Flow
import pro.liliya.core.cognition.CognitiveEngineImpl
import pro.liliya.core.event.EventBusImpl
import pro.liliya.core.event.EventProcessor
import pro.liliya.core.event.InMemoryEventStoreImpl
import pro.liliya.core.goal.GoalManagerImpl
import pro.liliya.core.memory.EpisodeBuilder
import pro.liliya.core.memory.InMemoryMemoryProvider
import pro.liliya.core.memory.InMemoryMemoryRepository
import pro.liliya.core.memory.MemoryCoordinator
import pro.liliya.core.memory.MemoryConsolidator
import pro.liliya.core.planning.PlanningEngineImpl
import pro.liliya.core.reasoning.ReasoningEngineImpl
import pro.liliya.core.reflection.ReflectionEngineImpl
import pro.liliya.core.orchestration.ExecutiveControllerImpl
import pro.liliya.core.runtime.RuntimeStateMachineImpl
import pro.liliya.domain.api.CognitiveEngine
import pro.liliya.domain.api.ExecutiveController
import pro.liliya.domain.api.RuntimeStateMachine
import pro.liliya.domain.models.Episode
import pro.liliya.domain.models.RuntimeState
import pro.liliya.model.mock.MockModelEngine

class LiliyaRuntime {

    private val modelEngine = MockModelEngine()

    private val eventBus = EventBusImpl()

    private val eventStore = InMemoryEventStoreImpl()

    private val eventProcessor = EventProcessor(
        eventBus = eventBus,
        eventStore = eventStore
    )


    private val memoryProvider = InMemoryMemoryProvider()

    private val memoryRepository = InMemoryMemoryRepository()

    private val memoryCoordinator =
        MemoryCoordinator(
            memoryRepository
        )


    private val memoryConsolidator =
        MemoryConsolidator(
            eventStore = eventStore,
            memoryProvider = memoryProvider
        )


    private val episodeBuilder =
        EpisodeBuilder()


    private val reasoningEngine =
        ReasoningEngineImpl()


    private val planningEngine =
        PlanningEngineImpl()


    private val reflectionEngine =
        ReflectionEngineImpl()


    private val goalManager =
        GoalManagerImpl()


    private val executiveController: ExecutiveController =
        ExecutiveControllerImpl(
            modelEngine = modelEngine
        )


    private val stateMachine: RuntimeStateMachine =
        RuntimeStateMachineImpl()


    private val cognitiveEngine: CognitiveEngine =
        CognitiveEngineImpl(
            controller = executiveController,
            eventProcessor = eventProcessor,
            reasoningEngine = reasoningEngine,
            planningEngine = planningEngine,
            reflectionEngine = reflectionEngine,
            memoryCoordinator = memoryCoordinator
        )


    suspend fun start() {

        stateMachine.transitionTo(
            RuntimeState.INITIALIZING
        )

        modelEngine.loadModel()

        stateMachine.transitionTo(
            RuntimeState.READY
        )
    }


    suspend fun stop() {

        stateMachine.transitionTo(
            RuntimeState.STOPPED
        )

        modelEngine.unloadModel()
    }


    suspend fun process(
        input: String
    ): Flow<String> {

        stateMachine.transitionTo(
            RuntimeState.THINKING
        )

        return cognitiveEngine.process(
            input
        )
    }


    suspend fun createEpisode(): Episode {

        val events = eventProcessor.history()

        val episode =
            episodeBuilder.createEpisode(
                events = events
            )

        memoryProvider.saveEpisode(
            episode
        )

        return episode
    }


    suspend fun consolidateMemory() {

        memoryConsolidator.consolidate()
    }


    suspend fun memoryHistory(): List<Episode> {

        return memoryProvider.loadEpisodes()
    }


    suspend fun searchMemory(
        query: String
    ): List<Episode> {

        val memories =
            memoryCoordinator.recall(
                query
            )

        return memories.map { memory ->

            Episode(
                id = memory.id,
                events = emptyList()
            )
        }
    }


    fun controller(): ExecutiveController {

        return executiveController
    }


    fun stateMachine(): RuntimeStateMachine {

        return stateMachine
    }
}
