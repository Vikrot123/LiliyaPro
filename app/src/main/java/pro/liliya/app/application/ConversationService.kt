package pro.liliya.app.application

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pro.liliya.domain.models.CognitiveEvent
import pro.liliya.runtime.LiliyaRuntime


class ConversationService(
    private val runtime: LiliyaRuntime
) {


    suspend fun start() {

        runtime.start()

    }


    suspend fun stop() {

        runtime.stop()

    }


    suspend fun sendMessage(
        message: String
    ): Flow<String> {


        return runtime.process(message)
            .map { event ->

                when (event) {


                    is CognitiveEvent.ThinkingStarted ->
                        "🧠 Анализ: ${event.input}"


                    is CognitiveEvent.MemorySearching ->
                        "📚 Поиск памяти: ${event.query}"


                    is CognitiveEvent.MemoryFound ->
                        "📚 Найдено воспоминаний: ${event.count}"


                    is CognitiveEvent.ReasoningCompleted ->
                        "💭 Рассуждение:\n${event.summary}"


                    is CognitiveEvent.PlanningCompleted ->
                        "📋 План:\n${event.steps.joinToString("\n")}"


                    CognitiveEvent.RespondingStarted ->
                        "🤖 Генерация ответа..."


                    is CognitiveEvent.ResponseChunk ->
                        event.text


                    CognitiveEvent.Completed ->
                        "✅ Готово"


                    is CognitiveEvent.Error ->
                        "❌ Ошибка: ${event.message}"

                }

            }

    }

}
