function reverseArray<T>(arr: T[]): T[] {
    return arr.reverse();
  }
  
  const numbers = [1, 2, 3, 4, 5];
  const letters = ["a", "b", "c", "d", "e"];
  
  console.log(reverseArray(numbers)); // Output: [5, 4, 3, 2, 1]
  console.log(reverseArray(letters)); // Output: ["e", "d", "c", "b", "a"]
  