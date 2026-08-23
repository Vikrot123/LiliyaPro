package pro.liliya.core.runtime.intelligence.meaning

interface RuntimeMeaningEngine {

    fun interpret(
        context: RuntimeMeaningContext
    ): RuntimeMeaningResult

}
