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
