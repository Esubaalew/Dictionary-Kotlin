import dictionary.getEntries
import dictionary.getSoup
import dictionary.getTotalEntries
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun main() {
    val url = "https://www.britannica.com/dictionary/"
    val entries = getEntries("head")
    for (entry in entries) {
        println(entry["text"])
        println(entry["link"])
    }
    println(getTotalEntries("head"))
}
