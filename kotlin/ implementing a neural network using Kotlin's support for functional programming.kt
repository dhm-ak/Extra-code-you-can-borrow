typealias Vector = List<Double>
typealias Matrix = List<Vector>

class NeuralNetwork(
    private val inputSize: Int,
    private val hiddenSize: Int,
    private val outputSize: Int,
    private val weights: Matrix
) {
    private val sigmoid = { x: Double -> 1.0 / (1.0 + exp(-x)) }
    
    fun predict(input: Vector): Vector {
        val hiddenLayer = weights[0].mapIndexed { i, _ -> sigmoid(dot(input, weights[0][i], hiddenSize)) }
        return weights[1].mapIndexed { i, _ -> sigmoid(dot(hiddenLayer, weights[1][i], outputSize)) }
    }
    
    private fun dot(v1: Vector, w: Double, size: Int): Double {
        return (0 until size).sumByDouble { i -> v1[i] * w }
    }
}

fun main() {
    val weights = listOf(
        listOf(0.1, 0.2, 0.3),
        listOf(0.4, 0.5, 0.6)
    )
    val nn = NeuralNetwork(2, 3, 1, weights)
    println(nn.predict(listOf(0.1, 0.2))) // [0.6106392335568462]
}
