fun main(args: Array<String>) {

    var updateId = 0

    val telegramBot = TelegramBotService(args[0])

    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex: Regex = "\"chat\":\\{\"id\":(\\d+)".toRegex()

    while (true) {
        Thread.sleep(2000)
        val updates = telegramBot.getUpdate(updateId)
        println(updates)

        val startUpdateId = updates.lastIndexOf("update_id")
        val endUpdateId = updates.lastIndexOf(",\n\"message\"")
        if (startUpdateId == -1 || endUpdateId == -1) continue
        val updateIdString = updates.substring(startUpdateId + 11, endUpdateId)

        updateId = updateIdString.toInt() + 1


        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value
        println(text)

        val matchResultId: MatchResult? = chatIdRegex.find(updates)
        val groupsId = matchResultId?.groups
        val id = groupsId?.get(1)?.value
        println(id)

        val message = telegramBot.sendMessage(id, text)
        println(message)
    }

}
