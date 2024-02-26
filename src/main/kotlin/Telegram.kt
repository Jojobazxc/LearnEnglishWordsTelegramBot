fun main(args: Array<String>) {

    var updateId = 0

    val telegramBot = TelegramBotService(args[0])

    val updateIdRegex: Regex = "\"update_id\":(\\d+)".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates = telegramBot.getUpdate(updateId)
        println(updates)

        val matchResultUpdateId = updateIdRegex.find(updates)
        val groupsUpdateId = matchResultUpdateId?.groups
        val updateId2 = groupsUpdateId?.get(1)?.value
        updateId = updateId2?.toInt()?.plus(1) ?: 0
        if (updateId == 0) continue

        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value

        val matchResultId: MatchResult? = chatIdRegex.find(updates)
        val groupsId = matchResultId?.groups
        val id = groupsId?.get(1)?.value

        telegramBot.sendMessage(id, text)

    }

}
