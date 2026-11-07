import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Graph {

    private Map<String, ArrayList<String>> adjacencyList;

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    // Add a vertex
    public void addVertex(String vertex) {

        if (!adjacencyList.containsKey(vertex)) {
            adjacencyList.put(vertex, new ArrayList<>());
        }
    }

    // Add an undirected edge
    public void addEdge(String vertex1, String vertex2) {

        addVertex(vertex1);
        addVertex(vertex2);

        adjacencyList.get(vertex1).add(vertex2);
        adjacencyList.get(vertex2).add(vertex1);
    }

    // BFS traversal
    public String[] bfs(String startVertex) {

        if (!adjacencyList.containsKey(startVertex)) {
            return new String[0];
        }

        ArrayList<String> result = new ArrayList<>();

        java.util.Queue<String> queue =
                new LinkedList<>();

        ArrayList<String> visited =
                new ArrayList<>();

        queue.add(startVertex);
        visited.add(startVertex);

        while (!queue.isEmpty()) {

            String current = queue.poll();

            result.add(current);

            for (String neighbor :
                    adjacencyList.get(current)) {

                if (!visited.contains(neighbor)) {

                    visited.add(neighbor);

                    queue.add(neighbor);
                }
            }
        }

        return result.toArray(new String[0]);
    }

    // Get adjacency list
    public Map<String, ArrayList<String>> getAdjacencyList() {
        return adjacencyList;
    }

    // Get all vertices
    public String[] getVertices() {

        return adjacencyList.keySet()
                .toArray(new String[0]);
    }
}