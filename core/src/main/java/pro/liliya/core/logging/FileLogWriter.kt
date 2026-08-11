package pro.liliya.core.logging


import java.io.File


class FileLogWriter(

    private val file: File

) {


    fun write(

        event: LogEvent

    ) {


        val text =
            LogFormatter.format(event)


        file.appendText(text)

    }

}
