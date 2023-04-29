use std::collections::BinaryHeap;
use std::cmp::Ordering;

#[derive(Clone, Copy, PartialEq, Eq)]
enum Terrain {
    Wall,
    Floor,
}

struct Node {
    x: i32,
    y: i32,
    cost: i32,
    heuristic: i32,
}

impl Node {
    fn new(x: i32, y: i32, cost: i32, heuristic: i32) -> Self {
        Node { x, y, cost, heuristic }
    }
}

impl Ord for Node {
    fn cmp(&self, other: &Self) -> Ordering {
        (self.cost + self.heuristic).cmp(&(other.cost + other.heuristic)).reverse()
    }
}

impl PartialOrd for Node {
    fn partial_cmp(&self, other: &Self) -> Option<Ordering> {
        Some(self.cmp(other))
    }
}

struct Grid {
    width: usize,
    height: usize,
    terrain: Vec<Terrain>,
}

impl Grid {
    fn new(width: usize, height: usize, terrain: Vec<Terrain>) -> Self {
        Grid { width, height, terrain }
    }

    fn get(&self, x: i32, y: i32) -> Option<Terrain> {
        if x >= 0 && y >= 0 && x < self.width as i32 && y < self.height as i32 {
            Some(self.terrain[(y * self.width as i32 + x) as usize])
        } else {
            None
        }
    }

    fn neighbors(&self, node: &Node) -> Vec<Node> {
        let mut neighbors = Vec::new();
        for dx in -1..=1 {
            for dy in -1..=1 {
                if dx == 0 && dy == 0 {
                    continue;
                }
                let x = node.x + dx;
                let y = node.y + dy;
                if let Some(terrain) = self.get(x, y) {
                    if terrain == Terrain::Floor {
                        let cost = if dx.abs() + dy.abs() == 2 {
                            node.cost + 14
                        } else {
                            node.cost + 10
                        };
                        let heuristic = (x - self.width as i32).abs() + (y - self.height as i32).abs();
                        neighbors.push(Node::new(x, y, cost, heuristic));
                    }
                }
            }
        }
        neighbors
    }

    fn search(&self, start: Node, goal: Node) -> Option<Vec<(i32, i32)>> {
        let mut frontier = BinaryHeap::new();
        frontier.push(start);
        let mut came_from = std::collections::HashMap::new();
        let mut cost_so_far = std::collections::HashMap::new();
        came_from.insert(start, None);
        cost_so_far.insert(start, 0);

        while let Some(current) = frontier.pop() {
            if current == goal {
                let mut path = Vec::new();
                let mut node = current;
                while let Some(&parent) = came_from.get(&node) {
                    path.push((node.x, node.y));
                    node = parent;
                }
                path.push((start.x, start.y));
                path.reverse();
                return Some(path);
            }

            for neighbor in self.neighbors(&current) {
                let new_cost = cost_so_far[&current] + neighbor.cost - current.cost;
                if !cost_so_far.contains_key(&neighbor) || new_cost < cost_so_far[&neighbor] {
cost_so_far.insert(neighbor, new_cost);
let priority = (new_cost + neighbor.heuristic) as i64;
frontier.push(neighbor);
came_from.insert(neighbor, Some(current));
}
}
}
None
}
}

fn main() {
let grid = Grid::new(5, 5, vec![
Terrain::Floor, Terrain::Wall, Terrain::Floor, Terrain::Floor, Terrain::Floor,
Terrain::Floor, Terrain::Wall, Terrain::Floor, Terrain::Wall, Terrain::Floor,
Terrain::Floor, Terrain::Floor, Terrain::Floor, Terrain::Wall, Terrain::Floor,
Terrain::Floor, Terrain::Wall, Terrain::Floor, Terrain::Wall, Terrain::Floor,
Terrain::Floor, Terrain::Floor, Terrain::Floor, Terrain::Wall, Terrain::Floor,
]);
let start = Node::new(0, 0, 0, (grid.width + grid.height) as i32);
let goal = Node::new(4, 4, 0, 0);
if let Some(path) = grid.search(start, goal) {
for (x, y) in path {
println!("({}, {})", x, y);
}
} else {
println!("No path found.");
}
}