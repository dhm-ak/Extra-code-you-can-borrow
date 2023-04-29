class RedBlackTree<T : Comparable<T>> {
    private var root: Node<T>? = null
    
    private enum class Color {
        RED, BLACK
    }
    
    private class Node<T>(
        val value: T,
        var color: Color,
        var left: Node<T>? = null,
        var right: Node<T>? = null
    ) {
        fun rotateLeft(): Node<T> {
            val newRoot = right
            right = newRoot?.left
            newRoot?.left = this
            return newRoot ?: this
        }
        
        fun rotateRight(): Node<T> {
            val newRoot = left
            left = newRoot?.right
            newRoot?.right = this
            return newRoot ?: this
        }
    }
    
    fun insert(value: T) {
        root = insert(root, value)
        root?.color = Color.BLACK
    }
    
    private fun insert(node: Node<T>?, value: T): Node<T> {
        if (node == null) {
            return Node(value, Color.RED)
        }
        
        if (value < node.value) {
            node.left = insert(node.left, value)
        } else if (value > node.value) {
            node.right = insert(node.right, value)
        } else {
            // duplicate value, do nothing
            return node
        }
        
        if (isRed(node.right) && !isRed(node.left)) {
            node = node.rotateLeft()
        }
        if (isRed(node.left) && isRed(node.left?.left)) {
            node = node.rotateRight()
        }
        if (isRed(node.left) && isRed(node.right)) {
            flipColors(node)
        }
        
        return node
    }
    
    private fun isRed(node: Node<T>?): Boolean {
        return node?.color == Color.RED
    }
    
    private fun flipColors(node: Node<T>) {
        node.color = Color.RED
        node.left?.color = Color.BLACK
        node.right?.color = Color.BLACK
    }
    
    override fun toString(): String {
        return root?.toString() ?: "[]"
    }
}

fun main() {
    val tree = RedBlackTree<Int>()
    tree.insert(1)
    tree.insert(2)
    tree.insert(3)
    tree.insert(4)
    tree.insert(5)
    println(tree)
}
