import akka.actor.*
import akka.routing.RoundRobinPool

class Worker : AbstractActor() {
    override fun createReceive(): Receive = receiveBuilder()
        .match(String::class.java) {
            // process the message
            println("Processing message: $it")
        }
        .build()
}

class Master(numWorkers: Int) : AbstractActor() {
    private val workerRouter = context.actorOf(RoundRobinPool(numWorkers).props(Props.create(Worker::class.java)), "workerRouter")
    
    override fun createReceive(): Receive = receiveBuilder()
        .match(String::class.java) {
            // forward the message to a worker actor
            workerRouter.tell(it, sender)
        }
        .build()
}

fun main() {
    val system = ActorSystem.create("MySystem")
    
    val master = system.actorOf(Props.create(Master::class.java, 5), "master")
    
    // send a message to the master actor
    master.tell("Hello, world!", ActorRef.noSender())
    
    system.terminate()
}
