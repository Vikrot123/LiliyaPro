package pro.liliya.app

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import pro.liliya.interaction.LiliyaInteraction
import pro.liliya.interaction.LiliyaInteractionAuthority
import pro.liliya.interaction.LiliyaInteractionRequest
import pro.liliya.interaction.LiliyaInteractionRuntimeState

class MainActivity : Activity() {

    private lateinit var statusView: TextView
    private lateinit var conversationView: TextView
    private lateinit var inputView: EditText
    private lateinit var sendButton: Button
    private lateinit var progressView: ProgressBar
    private lateinit var scrollView: ScrollView

    @Volatile
    private var requestRunning = false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            createContentView()
        )

        startLiliya()
    }

    override fun onDestroy() {
        if (isFinishing) {
            LiliyaInteraction.stop()
        }

        super.onDestroy()
    }

    private fun createContentView(): View {
        val density =
            resources.displayMetrics.density

        fun dp(value: Int): Int =
            (value * density).toInt()

        val root =
            LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(
                    dp(20),
                    dp(20),
                    dp(20),
                    dp(20)
                )
            }

        val title =
            TextView(this).apply {
                text = "LiliyaPro"
                textSize = 28f
            }

        statusView =
            TextView(this).apply {
                text = "Запуск ядра..."
                textSize = 14f
                setPadding(
                    0,
                    dp(8),
                    0,
                    dp(12)
                )
            }

        conversationView =
            TextView(this).apply {
                text =
                    "Лилия готовится к работе.\n\n"
                textSize = 17f
                setTextIsSelectable(true)
                setPadding(
                    0,
                    0,
                    0,
                    dp(16)
                )
            }

        scrollView =
            ScrollView(this).apply {
                addView(
                    conversationView,
                    android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                )
            }

        inputView =
            EditText(this).apply {
                hint = "Напишите сообщение"
                textSize = 17f
                minLines = 1
                maxLines = 5
                inputType =
                    InputType.TYPE_CLASS_TEXT or
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE or
                        InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            }

        sendButton =
            Button(this).apply {
                text = "Отправить"
                isEnabled = false

                setOnClickListener {
                    sendMessage()
                }
            }

        progressView =
            ProgressBar(this).apply {
                visibility = View.GONE
            }

        val controls =
            LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL

                addView(
                    sendButton,
                    LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                )

                addView(
                    progressView,
                    LinearLayout.LayoutParams(
                        dp(48),
                        dp(48)
                    )
                )
            }

        root.addView(
            title,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            statusView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            scrollView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(
            inputView,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(
            controls,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        return root
    }

    private fun startLiliya() {
        setBusy(true)
        statusView.text = "Запуск ядра..."

        Thread {
            try {
                LiliyaInteraction.start()

                val state =
                    LiliyaInteraction.state()

                runOnUiThread {
                    statusView.text =
                        when (state) {
                            LiliyaInteractionRuntimeState.RUNNING ->
                                "Лилия: готова"

                            LiliyaInteractionRuntimeState.STARTING ->
                                "Лилия: запускается"

                            LiliyaInteractionRuntimeState.FAILED ->
                                "Лилия: ошибка ядра"

                            LiliyaInteractionRuntimeState.STOPPED ->
                                "Лилия: остановлена"
                        }

                    setBusy(
                        state !=
                            LiliyaInteractionRuntimeState.RUNNING
                    )
                }
            } catch (error: Throwable) {
                runOnUiThread {
                    statusView.text =
                        "Ошибка запуска: ${error.message ?: error::class.java.simpleName}"

                    appendConversation(
                        "Система",
                        "Не удалось запустить LiliyaPro."
                    )

                    setBusy(false)
                }
            }
        }.start()
    }

    private fun sendMessage() {
        if (requestRunning) {
            return
        }

        val source =
            inputView.text
                .toString()
                .trim()

        if (source.isEmpty()) {
            return
        }

        inputView.setText("")

        hideKeyboard()

        appendConversation(
            "Вы",
            source
        )

        setBusy(true)
        statusView.text = "Лилия думает..."

        Thread {
            try {
                val result =
                    LiliyaInteraction.process(
                        LiliyaInteractionRequest(
                            source = source,
                            authority =
                                LiliyaInteractionAuthority.USER
                        )
                    )

                runOnUiThread {
                    appendConversation(
                        "Лилия",
                        result.interpretation
                    )

                    statusView.text = buildString {
                        append("Лилия: готова")

                        if (result.experienceCommitted) {
                            append(" • опыт сохранён")
                        }

                        if (result.knowledgeProduced) {
                            append(" • знание создано")
                        }
                    }

                    setBusy(false)
                }
            } catch (error: Throwable) {
                runOnUiThread {
                    appendConversation(
                        "Система",
                        error.message
                            ?: "Не удалось обработать сообщение."
                    )

                    statusView.text =
                        "Лилия: ошибка обработки"

                    setBusy(false)
                }
            }
        }.start()
    }

    private fun appendConversation(
        speaker: String,
        message: String
    ) {
        conversationView.append(
            "$speaker:\n$message\n\n"
        )

        scrollView.post {
            scrollView.fullScroll(
                View.FOCUS_DOWN
            )
        }
    }

    private fun setBusy(
        busy: Boolean
    ) {
        requestRunning = busy

        progressView.visibility =
            if (busy) {
                View.VISIBLE
            } else {
                View.GONE
            }

        sendButton.isEnabled =
            !busy &&
                LiliyaInteraction.state() ==
                    LiliyaInteractionRuntimeState.RUNNING

        inputView.isEnabled =
            !busy
    }

    private fun hideKeyboard() {
        val manager =
            getSystemService(
                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager

        manager.hideSoftInputFromWindow(
            inputView.windowToken,
            0
        )
    }
}
