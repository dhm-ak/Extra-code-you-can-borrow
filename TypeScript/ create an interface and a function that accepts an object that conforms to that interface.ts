interface Car {
    make: string;
    model: string;
    year: number;
    color?: string;
  }
  
  function printCarInfo(car: Car) {
    console.log(`Make: ${car.make}`);
    console.log(`Model: ${car.model}`);
    console.log(`Year: ${car.year}`);
    if (car.color) {
      console.log(`Color: ${car.color}`);
    }
  }
  
  const car1 = { make: "Toyota", model: "Corolla", year: 2019 };
  const car2 = { make: "Honda", model: "Civic", year: 2020, color: "red" };
  
  printCarInfo(car1);
  /*
  Output:
  Make: Toyota
  Model: Corolla
  Year: 2019
  */
  
  printCarInfo(car2);
  /*
  Output:
  Make: Honda
  Model: Civic
  Year: 2020
  Color: red
  */
  