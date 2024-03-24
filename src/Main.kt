import dictionary.getEntries
import dictionary.getParts
import dictionary.getTotalEntries
import dictionary.getWordOfTheDay

fun main() {

    for ( part in getParts("focused")!!) {
        println(part)
    }

}
