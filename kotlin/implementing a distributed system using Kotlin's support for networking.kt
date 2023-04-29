data class KeyValue(val key: String, val value: String)

class Node(private val id: Int, private val nodes: List<String>) {
    private val store = mutableMapOf<String, String>()
    
    private fun gossip() {
        val rand = Random(System.currentTimeMillis())
        val dest = nodes[rand.nextInt(nodes.size)]
        val socket = Socket(dest, 9999)
        val output = ObjectOutputStream(socket.getOutputStream())
        output.writeObject(store.map { KeyValue(it.key, it.value) })
        output.flush()
        output.close()
        socket.close()
    }
    
    private fun handleConnection(client: Socket) {
        val input = ObjectInputStream(client.getInputStream())
        val kvs = input.readObject() as List<KeyValue>
        input.close()
        client.close()
        kvs.forEach { store[it.key] = it.value }
    }
    
    fun start() {
        Thread {
            val server = ServerSocket(9999)
            while (true) {
                val client = server.accept()
                Thread { handleConnection(client) }.start()
            }
        }.start()
        
        while (true) {
            gossip()
            Thread.sleep(1000)
        }
    }
}

fun main() {
    val nodes = listOf("localhost", "192.168.1.1", "192.168.1.2", "192.168.1.3")
    nodes.forEachIndexed { i, addr -> Node(i, nodes.filterNot { it == addr }).start() }
}
