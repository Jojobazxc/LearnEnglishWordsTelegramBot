import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)


fun main(args: Array<String>) {

    var lastUpdateId = 0L

    val telegramBot = TelegramBotService(args[0])

    val json = Json{
        ignoreUnknownKeys = true
    }

    val trainer = LearnWordsTrainer(3, 4, "words.txt")
    val statistics = trainer.getStatistics()


    while (true) {
        Thread.sleep(2000)
        val responseString = telegramBot.getUpdate(lastUpdateId)
        println(responseString)

        val response: Response = json.decodeFromString(responseString)
        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        lastUpdateId = updateId + 1


        val message = firstUpdate.message?.text
        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id
        val data = firstUpdate.callbackQuery?.data

        if (message?.lowercase() == "/start" && chatId != null) {
            telegramBot.sendMenu(chatId)
        }
        if (data?.lowercase() == CALLBACK_DATA_STATISTICS_BUTTON && chatId != null) {
            telegramBot.sendMessage(
                chatId,
                "Выучено ${statistics.quantityOfLearnedWords.size} из ${statistics.dictionarySize} слов | ${statistics.percentsOfCorrectAnswers}%"
            )
        }
        if (data == CALLBACK_DATA_LEARN_BUTTON && chatId != null) {
            telegramBot.checkNextQuestionAndSend(trainer, chatId)
        }
        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true && chatId != null) {
            val userAnswerIndex = data.substringAfter(CALLBACK_DATA_ANSWER_PREFIX).toInt()
            if (trainer.checkUserAnswer(userAnswerIndex)) telegramBot.sendMessage(chatId, "Правильно!")
            else telegramBot.sendMessage(
                chatId,
                "Неправильно! ${trainer.question?.wordForLearning?.original} - это ${trainer.question?.wordForLearning?.translate}"
            )
        }

    }

}
