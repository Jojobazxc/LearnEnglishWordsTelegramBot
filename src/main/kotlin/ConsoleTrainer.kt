import java.io.File

fun main() {

    val wordsFile = File("words.txt")

    wordsFile.createNewFile()

    val lines = wordsFile.readLines()

    val dictionary: MutableList<Word> = mutableListOf()
    for (line in lines) {
        val line = line.split("|")
        val word = Word(line[0], line[1], line[2].toInt())
        dictionary.add(word)
    }
    println(dictionary)

    while (true) {
        println("Меню: 1 – Учить слова, 2 – Статистика, 0 – Выход")
        val choice = readln().toIntOrNull() ?: continue
        when (choice) {
            1 -> {
                while (true) {
                    val listOfUnlearnedWords =
                        dictionary.filter { it.countOfCorrectAnswer <= BOUNDARY_FOR_LEARNED_WORD }
                    if (listOfUnlearnedWords.isEmpty()) {
                        println("Вы выучили все слова")
                        break
                    }
                    val answers: List<Word> = listOfUnlearnedWords.shuffled().take(NUMBER_OF_ANSWER_OPTIONS)
                    if (answers.size < NUMBER_OF_ANSWER_OPTIONS) {
                        val listOfLearnedWords =
                            dictionary.filter { it.countOfCorrectAnswer > BOUNDARY_FOR_LEARNED_WORD }
                        answers.toMutableList()
                            .addAll(listOfLearnedWords.shuffled().take(NUMBER_OF_ANSWER_OPTIONS - answers.size))
                    }
                    val wordForLearning = answers.random()
                    println(wordForLearning.translate)
                    answers.forEachIndexed { index, word -> println("${index + 1}. ${word.original}") }
                    println("0. Выход в меню")
                    when (val answerOfUser = readln().toInt()) {
                        0 -> break
                        in 1..4 -> {
                            if (answerOfUser == (answers.indexOf(wordForLearning) + 1)) {
                                wordForLearning.countOfCorrectAnswer++
                                println("Правильно!")
                                saveDictionary(dictionary)
                            }
                        }
                    }
                }
            }

            2 -> {
                val quantityOfLearnedWords = dictionary.filter { it.countOfCorrectAnswer >= BOUNDARY_FOR_LEARNED_WORD }
                val percentsOfCorrectAnswers =
                    ((quantityOfLearnedWords.size.toDouble() / dictionary.size) * 100).toInt()
                println("Выучено ${quantityOfLearnedWords.size} из ${dictionary.size} слов | ${percentsOfCorrectAnswers}%")
            }

            0 -> return
            else -> println("Введите существующий вариант ответа")
        }
    }

}

fun saveDictionary(dictionary: List<Word>) {
    val wordsFIle = File("words.txt")
    wordsFIle.readLines()

    val writer = wordsFIle.bufferedWriter()
    for (i in dictionary) {
        val stringForWrite = "${i.original}|${i.translate}|${i.countOfCorrectAnswer}\n"
        writer.write(stringForWrite)
    }
    writer.close()
}

const val BOUNDARY_FOR_LEARNED_WORD = 3
const val NUMBER_OF_ANSWER_OPTIONS = 4