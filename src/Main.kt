import dictionary.getSoup
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun main() {
    val url = "https://www.britannica.com/dictionary/"
    val doc = getSoup(url)
    println(doc)
}
