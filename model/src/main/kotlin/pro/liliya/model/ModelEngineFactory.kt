package pro.liliya.model

import pro.liliya.domain.api.ModelEngine
import pro.liliya.model.mock.MockModelEngine

object ModelEngineFactory {

    /**
     * Диагностический режим.
     *
     * Используем MockModelEngine,
     * чтобы проверить Runtime и UI
     * без загрузки GGUF модели.
     *
     * После проверки вернём Qwen.
     */

    fun create(): ModelEngine {
        return MockModelEngine()
    }
}
