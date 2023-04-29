class Person private constructor(
    val name: String,
    val age: Int,
    val address: String,
    val phone: String
) {
    class Builder {
        private var name: String? = null
        private var age: Int? = null
        private var address: String? = null
        private var phone: String? = null
        
        fun name(name: String) = apply { this.name = name }
        fun age(age: Int) = apply { this.age = age }
        fun address(address: String) = apply { this.address = address }
        fun phone(phone: String) = apply { this.phone = phone }
        
        fun build(): Person {
            require(name != null) { "Name must be set" }
            require(age != null) { "Age must be set" }
            require(address != null) { "Address must be set" }
            require(phone != null) { "Phone must be set" }
            
            return Person(name!!, age!!, address!!, phone!!)
        }
    }
}
