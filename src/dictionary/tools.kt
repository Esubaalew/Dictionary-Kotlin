package dictionary

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

// tools.kt

/**
 * Module for Common tools
 */


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