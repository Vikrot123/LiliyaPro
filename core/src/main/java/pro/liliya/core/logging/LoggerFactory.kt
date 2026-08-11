package pro.liliya.core.logging

object LoggerFactory {

    fun create(
        module: String,
        component: String,
        method: String
    ): Logger {

        return try {
            LoggerProvider.get(
                LoggerContext(
                    module = module,
                    component = component,
                    method = method
                )
            )
        } catch (e: IllegalStateException) {
            NoOpLogger()
        }
    }
}
