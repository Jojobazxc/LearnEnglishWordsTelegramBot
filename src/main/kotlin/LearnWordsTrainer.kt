import java.io.File


data class Word(
    val original: String,
    val translate: String,
    var countOfCorrectAnswer: Int = 0,
)

data class Statistics(
    val quantityOfLearnedWords: List<Word>,
    val dictionarySize: Int,
    val percentsOfCorrectAnswers: Int,
)

data class Question(
    val listOfUnlearnedWords: List<Word>,
    val answers: List<Word>,
    val wordForLearning: Word
)

class LearnWordsTrainer(
    private val nameOfTextFile: String = "words.txt",
    private val boundaryForLearnedWords: Int = 3,
    val countOfAnswers: Int = 4,

) {

    private val dictionary = loadDictionary()
    var question: Question? = null

    fun getStatistics(): Statistics {
        val quantityOfLearnedWords = dictionary.filter { it.countOfCorrectAnswer >= boundaryForLearnedWords }
        val dictionarySize = dictionary.size
        val percentsOfCorrectAnswers =
            ((quantityOfLearnedWords.size.toDouble() / dictionarySize) * 100).toInt()
        return Statistics(
            quantityOfLearnedWords,
            dictionarySize,
            percentsOfCorrectAnswers
        )
    }

    fun getNextQuestion(): Question? {
        val listOfUnlearnedWords = dictionary.filter { it.countOfCorrectAnswer < boundaryForLearnedWords }
        var answers = listOfUnlearnedWords.shuffled().take(countOfAnswers)
        if (listOfUnlearnedWords.isEmpty()) return null
        if ((answers.size <= countOfAnswers)) {
            answers = answers + dictionary
                .filter { it.countOfCorrectAnswer > boundaryForLearnedWords }
                .shuffled()
                .take(countOfAnswers - answers.size)
        }
        val wordForLearning = answers.random()
        question = Question(
            listOfUnlearnedWords,
            answers,
            wordForLearning
        )
        return question
    }

    fun checkUserAnswer(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.answers.indexOf(it.wordForLearning)
            return if (correctAnswerId == (userAnswerIndex)) {
                it.wordForLearning.countOfCorrectAnswer++
                saveDictionary()
                true
            } else false
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
        val wordsFile = File(nameOfTextFile)
        if (!wordsFile.exists()) {
            File("words.txt").copyTo(wordsFile)
        }
        val lines = wordsFile.readLines()
        val dictionary: MutableList<Word> = mutableListOf()
        for (line in lines) {
            val line = line.split("|")
            val word = Word(line[0], line[1], line.getOrNull(2)?.toIntOrNull() ?: 0)
            dictionary.add(word)
        }
        return dictionary
    }

    private fun saveDictionary() {
        val dictionaryFile = File(nameOfTextFile)

        dictionaryFile.writeText("")
        for (word in dictionary) {
            val stringForWrite = "${word.original}|${word.translate}|${word.countOfCorrectAnswer}\n"
            dictionaryFile.appendText(stringForWrite)
        }

    }

    fun resetProgress() {
        dictionary.forEach { it.countOfCorrectAnswer = 0}
        saveDictionary()
    }

}
