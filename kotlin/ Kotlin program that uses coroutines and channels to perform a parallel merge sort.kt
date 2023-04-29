import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

suspend fun merge(left: Channel<Int>, right: Channel<Int>, output: Channel<Int>) {
    var leftValue = left.receiveOrNull()
    var rightValue = right.receiveOrNull()
    while (leftValue != null && rightValue != null) {
        if (leftValue < rightValue) {
            output.send(leftValue)
            leftValue = left.receiveOrNull()
        } else {
            output.send(rightValue)
            rightValue = right.receiveOrNull()
        }
    }
    while (leftValue != null) {
        output.send(leftValue)
        leftValue = left.receiveOrNull()
    }
    while (rightValue != null) {
        output.send(rightValue)
        rightValue = right.receiveOrNull()
    }
    output.close()
}

suspend fun mergeSort(values: List<Int>, output: Channel<Int>): Unit = coroutineScope {
    if (values.size <= 1) {
        values.forEach { output.send(it) }
        return@coroutineScope
    }
    val middleIndex = values.size / 2
    val leftValues = values.subList(0, middleIndex)
    val rightValues = values.subList(middleIndex, values.size)
    val leftChannel = Channel<Int>(Channel.UNLIMITED)
    val rightChannel = Channel<Int>(Channel.UNLIMITED)
    launch { mergeSort(leftValues, leftChannel) }
    launch { mergeSort(rightValues, rightChannel) }
    launch { merge(leftChannel, rightChannel, output) }
}

fun main() = runBlocking {
    val input = listOf(3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5)
    val output = Channel<Int>(Channel.UNLIMITED)
    launch { mergeSort(input, output) }
    for (value in output) {
        println(value)
    }
}
