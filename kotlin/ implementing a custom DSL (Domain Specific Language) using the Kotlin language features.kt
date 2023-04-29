class Person {
    var name: String = ""
    var age: Int = 0
    var address: String = ""
    var phone: String = ""
    
    fun print() {
        println("Name: $name")
        println("Age: $age")
        println("Address: $address")
        println("Phone: $phone")
    }
}

fun person(block: Person.() -> Unit): Person {
    val person = Person()
    person.block()
    return person
}

fun main() {
    val p = person {
        name = "John Doe"
        age = 30
        address = "123 Main St."
        phone = "555-1234"
    }
    
    p.print()
}
