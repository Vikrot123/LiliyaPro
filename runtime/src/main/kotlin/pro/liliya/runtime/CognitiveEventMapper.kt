package pro.liliya.runtime

import pro.liliya.domain.models.CognitiveEvent

/**
 * Преобразует внутренние события Cognitive Engine
 * в текстовый поток для UI.
 */
object CognitiveEventMapper {

    fun map(
        event: CognitiveEvent
    ): String {

        return when (event) {

            is CognitiveEvent.ThinkingStarted ->
                "🧠 Думаю..."

            is CognitiveEvent.MemorySearching ->
                "🔎 Поиск в памяти..."

            is CognitiveEvent.MemoryFound ->
                "📚 Найдено воспоминаний: ${event.count}"

            is CognitiveEvent.ReasoningCompleted ->
                "💭 ${event.summary}"

            is CognitiveEvent.PlanningCompleted ->
                "📋 План создан"

            CognitiveEvent.RespondingStarted ->
                "💬 Формирую ответ..."

            is CognitiveEvent.ResponseChunk ->
                event.text

            CognitiveEvent.Completed ->
                "✓ Завершено"

            is CognitiveEvent.Error ->
                "❌ Ошибка: ${event.message}"
        }
    }
}

