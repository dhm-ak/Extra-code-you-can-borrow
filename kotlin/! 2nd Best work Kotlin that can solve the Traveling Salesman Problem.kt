import kotlin.random.Random

data class City(val x: Int, val y: Int)

class Chromosome(val cities: List<City>) {
    var fitness: Double = 0.0
        private set

    init {
        calculateFitness()
    }

    private fun calculateFitness() {
        var distance = 0.0
        for (i in 0 until cities.size - 1) {
            val city1 = cities[i]
            val city2 = cities[i + 1]
            distance += Math.sqrt((city1.x - city2.x).toDouble().pow(2.0) + (city1.y - city2.y).toDouble().pow(2.0))
        }
        fitness = 1.0 / distance
    }

    fun crossover(other: Chromosome): Chromosome {
        val childCities = mutableListOf<City>()
        val startPos = Random.nextInt(0, cities.size)
        val endPos = Random.nextInt(startPos, cities.size)

        for (i in startPos until endPos) {
            childCities.add(cities[i])
        }

        for (city in other.cities) {
            if (!childCities.contains(city)) {
                childCities.add(city)
            }
        }

        return Chromosome(childCities)
    }

    fun mutate() {
        val pos1 = Random.nextInt(0, cities.size)
        var pos2 = Random.nextInt(0, cities.size)
        while (pos1 == pos2) {
            pos2 = Random.nextInt(0, cities.size)
        }
        val temp = cities[pos1]
        cities[pos1] = cities[pos2]
        cities[pos2] = temp
    }
}

class GeneticAlgorithm(val cities: List<City>, val populationSize: Int, val mutationRate: Double) {
    var currentGeneration: Int = 0
        private set

    private var population = List(populationSize) { Chromosome(cities.shuffled()) }

    fun run(maxGenerations: Int): Chromosome {
        var bestChromosome = population.first()
        for (i in 0 until maxGenerations) {
            currentGeneration = i
            val parents = selectParents()
            val offspring = mutableListOf<Chromosome>()
            for (j in 0 until populationSize / 2) {
                val parent1 = parents[j * 2]
                val parent2 = parents[j * 2 + 1]
                val child1 = parent1.crossover(parent2)
                val child2 = parent2.crossover(parent1)
                child1.mutate()
                child2.mutate()
                offspring.add(child1)
                offspring.add(child2)
            }
            population = offspring
            for (chromosome in population) {
                if (chromosome.fitness > bestChromosome.fitness) {
                    bestChromosome = chromosome
                }
            }
        }
        return bestChromosome
    }

    private fun selectParents(): List<Chromosome> {
        val parents = mutableListOf<Chromosome>()
        population = population.sortedByDescending { it.fitness }
        for (i in 0 until populationSize / 2) {
            val index1 = rouletteSelection()
            val index2 = rouletteSelection()
            parents.add(population[index1])
            parents.add(population[index2])
        }
        return parents
    }

private fun rouletteSelection(): Int {
    val totalFitness = population.sumByDouble { it.fitness }
    var rouletteValue = Random.nextDouble() * totalFitness
    var index = 0
    var currentFitness = population[index].fitness
    while (rouletteValue > 0) {
        index++
        currentFitness += population[index].fitness
        rouletteValue -= population[index].fitness
    }
    return index
}
