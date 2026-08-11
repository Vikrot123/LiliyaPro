package pro.liliya.core.logging

class NoOpLogger : Logger {

    override fun debug(
        marker: String,
        message: String
    ) {
    }

    override fun info(
        marker: String,
        message: String
    ) {
    }

    override fun warn(
        marker: String,
        message: String
    ) {
    }

    override fun error(
        marker: String,
        message: String,
        throwable: Throwable?
    ) {
    }
}
