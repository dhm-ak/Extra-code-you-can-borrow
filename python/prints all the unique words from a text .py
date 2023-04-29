import string

filename = "sample.txt"
words = set()

with open(filename, "r") as file:
    for line in file:
        # Remove punctuation and convert to lowercase
        line = line.translate(str.maketrans("", "", string.punctuation)).lower()
        # Split line into words and add to set
        words.update(line.split())

# Sort words alphabetically and print
for word in sorted(words):
    print(word)
