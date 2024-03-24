package dictionary
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
// Britannica.kt

/**
 * Module for Britannica Dictionary
 */

const val DOMAIN = "https://www.britannica.com"
/**
 * Get Jsoup Document object for a URL
 *
 * @param url The URL to fetch and parse
 * @return Jsoup Document object for the URL, or null if unable to retrieve
 */
fun getSoup(url: String): Document? {
    return try {
        Jsoup.connect(url).get()
    } catch (e: Exception) {
        println("An error occurred: $e")
        null
    }
}

/**
 * Get related entries for a word in Britannica Dictionary
 *
 * @param word Word to search for
 * @return List of dictionaries containing the text and link of each entry, or an empty list if no entries are found
 */
fun getEntries(word: String): List<Map<String, String>> {
    val url = "$DOMAIN/dictionary/$word"
    val soup = getSoup(url) ?: return emptyList()
    val entries = soup.select("ul.o_list")

    return if (entries.isNotEmpty()) {
        entries[0].select("li").map { entry ->
            mapOf(
                "text" to entry.select("a").text().trim(),
                "link" to "$DOMAIN${entry.select("a").attr("href")}"
            )
        }
    } else {
        emptyList()
    }
}

/**
 * Get total number of entries for a word in Britannica Dictionary
 *
 * @param word Word to search for
 * @return Total number of entries for the word
 */
fun getTotalEntries(word: String): Int = getEntries(word).size

/**
 * Get the word of the day from Britannica Dictionary
 *
 * @return Dictionary containing the word, part of speech, image, and meaning information, or null if not found
 */
fun getWordOfTheDay(): Map<String, Any>? {
    val url = "$DOMAIN/dictionary/eb/word-of-the-day"
    val soup = getSoup(url) ?: return null

    val wordContainer = soup.select("div.hw_d.box_sizing.ld_xs_hidden").firstOrNull()
    val imageContainer = soup.select("div.wod_img_act").firstOrNull()
    val meaningContainer = soup.select("div.midbs").firstOrNull()

    val wordInfo = mutableMapOf<String, Any>()

    wordContainer?.let {
        val wordText = it.select("span.hw_txt").text().trim()
        val partOfSpeech = it.select("span.fl").text().trim()
        println(partOfSpeech)
        wordInfo["word"] = "$wordText ($partOfSpeech)"
    }

    imageContainer?.let {
        val image = it.select("img").firstOrNull()
        val src = image?.attr("src") ?: ""
        val alt = image?.attr("alt") ?: ""
        wordInfo["image"] = mapOf("src" to src, "alt" to alt)
    }

    meaningContainer?.let {
        val meanings = mutableListOf<Map<String, Any>>()
        it.select("div.midb").forEach { block ->
            val definition = block.select("div.midbt p").text()
            val examples = block.select("li.vi").map { example -> example.text() }
            meanings.add(mapOf("definition" to definition, "examples" to examples))
        }
        wordInfo["meanings"] = meanings
    }

    return wordInfo
}

/**
 * Get the parts of speech for a given word from the Britannica Dictionary.
 *
 * @param word The word to fetch parts of speech for.
 * @return List of strings, with each string containing the headword and its part of speech.
 */
fun getParts(word: String): List<String>? {
    val url = "$DOMAIN/dictionary/$word"
    val soup = getSoup(url) ?: return null

    val entries = soup.select("div.hw_d")?: return null

    if (entries.isEmpty()) {
        return emptyList()
    }

    val partsOfSpeech = mutableListOf<String>()

    for (entry in entries) {
        val headwordElement = entry.select("span.hw_txt").firstOrNull()
        val partOfSpeechElement = entry.select("span.fl").firstOrNull()

        val headword = headwordElement?.text()?.trim() ?: continue
        val partOfSpeech = partOfSpeechElement?.text()?.trim() ?: continue

        partsOfSpeech.add("$headword ($partOfSpeech)")
    }

    return partsOfSpeech
}

/**
 * Extracts the definitions and examples from the Britannica Dictionary for a given word.
 *
 * @param word The word to fetch definitions for.
 * @return List of dictionaries, where each dictionary contains a meaning and its examples.
 *         Returns null if no definitions are found or if sense/examples are not found.
 */
fun getDefinitions(word: String): List<Map<String, Any>>? {
    val url = "$DOMAIN/dictionary/$word"
    val soup = getSoup(url) ?: return null

    val sblocks = soup.select("div.sblocks")
    val definitionsWithExamples = mutableListOf<Map<String, Any>>()

    for (sblock in sblocks) {
        val definitionBlocks = sblock.select("div.sblock_c")
        for (block in definitionBlocks) {
            val senses = block.select("div.sense")
            for (sense in senses) {
                val definitions = sense.select("span.def_text")
                val examples = sense.select("li.vi")

                if (definitions.isNotEmpty() && examples.isNotEmpty()) {
                    val definitionExamplePairs = definitions.zip(examples)
                    for ((definition, example) in definitionExamplePairs) {
                        val meaning = definition.text().trim()
                        val exampleList = examples.map { it.text().trim() }
                        definitionsWithExamples.add(mapOf("meaning" to meaning, "examples" to exampleList))
                    }
                } else if (definitions.isNotEmpty()) {
                    for (definition in definitions) {
                        val meaning = definition.text().trim()
                        definitionsWithExamples.add(mapOf("meaning" to meaning, "examples" to emptyList<String>()))
                    }
                }
            }
        }
    }

    return if (definitionsWithExamples.isNotEmpty()) definitionsWithExamples else null
}