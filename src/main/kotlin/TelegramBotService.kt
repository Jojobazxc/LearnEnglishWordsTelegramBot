import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class TelegramBotService(private val botToken: String) {

    private val client: HttpClient = HttpClient.newBuilder().build()
    private val json = Json { ignoreUnknownKeys = true }

    private fun sendQuestion(chatId: Long, question: Question): String? {
        val urlSendMessage = "$TELEGRAM_BOT_API_BASE_URL$botToken/sendMessage"
        val inlineKeyboard = question.answers.mapIndexed { index, word ->
            InlineKeyboard(
                text = word.translate,
                callbackData = "$CALLBACK_DATA_ANSWER_PREFIX$index"
            )
        }
        val keyboardColumns = inlineKeyboard.map { listOf(it) }
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = question.wordForLearning.original,
            replyMarkup = ReplyMarkup(keyboardColumns)
        )
        val requestBodyString = json.encodeToString(requestBody)
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun getUpdate(updateId: Long): Response {
        val urlGetUpdates = "$TELEGRAM_BOT_API_BASE_URL$botToken/getUpdates?offset=$updateId"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return json.decodeFromString(response.body())
    }

    fun sendMessage(chatId: Long, textMessage: String): Result<String> {
        val urlSendMessage = "$TELEGRAM_BOT_API_BASE_URL$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = textMessage,
        )
        val requestBodyString = json.encodeToString(requestBody)
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        return runCatching {
            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            response.body()
            throw Exception("${response.statusCode()}")
        }
    }

    fun sendMenu(chatId: Long): String {
        val urlSendMessage = "$TELEGRAM_BOT_API_BASE_URL$botToken/sendMessage"
        val requestBody = SendMessageRequest(
            chatId = chatId,
            text = "Основное меню",
            replyMarkup = ReplyMarkup(
                listOf(
                    listOf(
                        InlineKeyboard(text = "Изучать слова", callbackData = CALLBACK_DATA_LEARN_BUTTON),
                        InlineKeyboard(text = "Статистика", callbackData = CALLBACK_DATA_STATISTICS_BUTTON),
                    ),
                    listOf(
                        InlineKeyboard(text = "Сбросить прогресс", callbackData = RESET_CLICKED)
                    )
                )
            )
        )
        val requestBodyString = json.encodeToString(requestBody)
        val request = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, chatId: Long) {
        trainer.getNextQuestion()?.let { sendQuestion(chatId, it) } ?: sendMessage(
            chatId,
            "Вы выучили все слова!"
        )
    }

}


