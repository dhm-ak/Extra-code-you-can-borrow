class Person {
    private name: string;
    private age: number;
  
    constructor(name: string, age: number) {
      this.name = name;
      this.age = age;
    }
  
    public getName(): string {
      return this.name;
    }
  
    public getAge(): number {
      return this.age;
    }
  }
  
  const person = new Person("John", 30);
  console.log(person.getName()); // Output: "John"
  console.log(person.getAge()); // Output: 30
  