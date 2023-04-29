import random

def generate_maze(width, height):
    maze = [[0] * width + [1] for _ in range(height)] + [[1] * (width + 1)]
    visited = set()
    stack = [(0, 0)]

    while stack:
        x, y = stack.pop()
        if (x, y) in visited:
            continue
        visited.add((x, y))

        neighbors = [(x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)]
        random.shuffle(neighbors)

        for neighbor_x, neighbor_y in neighbors:
            if neighbor_x < 0 or neighbor_x >= width or neighbor_y < 0 or neighbor_y >= height:
                continue
            if (neighbor_x, neighbor_y) in visited:
                continue
            if neighbor_x == x:
                maze[max(y, neighbor_y)][x] = 0
            else:
                maze[y][max(x, neighbor_x)] = 0
            stack.append((neighbor_x, neighbor_y))

    return maze

if __name__ == '__main__':
    maze = generate_maze(20, 20)
    for row in maze:
        print(''.join('X' if cell else ' ' for cell in row))
