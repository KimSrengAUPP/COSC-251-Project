import java.util.*;

// COSC 251 Project
// Team Members:
//   Sreng KIM
//   Ty NGEN
//   Sothea UM

// Vertex is equivalent to an airport
class Vertex {
    private String name;

    Vertex(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

// Edge is equivalent to a flight between airports
class Edge {
    private Vertex source;
    private Vertex destination;
    private double price;
    private int duration;

    Edge(Vertex source, Vertex destination, double price, int duration) {
        this.source = source;
        this.destination = destination;
        this.price = price;
        this.duration = duration;
    }

    public Vertex getSource() {
        return source;
    }

    public Vertex getDestination() {
        return destination;
    }

    public double getPrice() {
        return price;
    }
    
    public int getDuration() {
    	return duration;
    }
}


// Graph shows the network of available flights between the many airports
class Graph {
    private Map<Vertex, List<Edge>> adjacencyList = new HashMap<>();

    public void addVertex(Vertex vertex) {
        adjacencyList.put(vertex, new ArrayList<>());
    }

    public void addEdge(Vertex source, Vertex destination, double price, int duration) {
        Edge edge = new Edge(source, destination, price, duration);
        adjacencyList.get(source).add(edge);
        // if graph is undirected, uncomment the below. Since airports flights are one-way, this is a directed graph.
        // adjacencyList.get(destination).add(edge);
    }

    public List<Edge> getEdges(Vertex vertex) {
        return adjacencyList.get(vertex);
    }

    public Set<Vertex> getVertices() {
        return adjacencyList.keySet();
    }
}

public class FlightRecommender {
    public static void main(String[] args) {
    	Vertex JFK = new Vertex("JFK");
        Vertex LAX = new Vertex("LAX");
        Vertex ORD = new Vertex("ORD");
        Vertex SFO = new Vertex("SFO");
        Vertex ATL = new Vertex("ATL");
        Vertex DFW = new Vertex("DFW");
        Vertex MIA = new Vertex("MIA");
        Vertex DEN = new Vertex("DEN");
        Vertex SEA = new Vertex("SEA");
        Vertex BOS = new Vertex("BOS");

        Graph graph = new Graph();
        graph.addVertex(JFK);
        graph.addVertex(LAX);
        graph.addVertex(ORD);
        graph.addVertex(SFO);
        graph.addVertex(ATL);
        graph.addVertex(DFW);
        graph.addVertex(MIA);
        graph.addVertex(DEN);
        graph.addVertex(SEA);
        graph.addVertex(BOS);

        graph.addEdge(JFK, LAX, 400, 5);
        graph.addEdge(ORD, LAX, 300, 3);
        graph.addEdge(LAX, ORD, 700, 8);
        graph.addEdge(JFK, ORD, 600, 6);
        graph.addEdge(LAX, JFK, 200, 2);
        graph.addEdge(JFK, SFO, 600, 6);
        graph.addEdge(SFO, ATL, 800, 7);
        graph.addEdge(ATL, DFW, 400, 4);
        graph.addEdge(DFW, LAX, 500, 5);
        graph.addEdge(ATL, LAX, 600, 6);
        graph.addEdge(SFO, DFW, 700, 7);
        graph.addEdge(SFO, ORD, 750, 8);
        graph.addEdge(DFW, ORD, 450, 4);
        graph.addEdge(SFO, JFK, 550, 5);
        graph.addEdge(MIA, ATL, 600, 3);
        graph.addEdge(DEN, JFK, 550, 4);
        graph.addEdge(SEA, DEN, 250, 2);
        graph.addEdge(BOS, JFK, 200, 1);
        graph.addEdge(JFK, SEA, 700, 8);
        graph.addEdge(BOS, LAX, 550, 6);
        graph.addEdge(SEA, MIA, 900, 10);

        System.out.print("Available airports: ");
        for (Vertex vertex : graph.getVertices()) {
            System.out.print(vertex.getName() + " ");
        }
        System.out.println();
        
        Scanner scanner = new Scanner(System.in);
        
        // Gets airport source and destination:
        System.out.print("Enter source airport: ");
        String sourceAirportName = scanner.nextLine().toUpperCase();
        Vertex sourceAirport = getAirport(graph, sourceAirportName);
        
        System.out.print("Enter destination airport: ");
        String destinationAirportName = scanner.nextLine().toUpperCase();
        Vertex destinationAirport = getAirport(graph, destinationAirportName);
        
        scanner.close();

        // Validate user input
        if (sourceAirport == null) {
            System.out.println("ERROR: Invalid source airport.");
            return;
        }
        if (destinationAirport == null) {
            System.out.println("ERROR: Invalid destination airport.");
            return;
        }
        	
        // Find the best paths from source to destination
        PathInfo pathInfo = dijkstra(graph, sourceAirport, destinationAirport);

        // Display results
        if (pathInfo.weightedValue < Double.POSITIVE_INFINITY) {
            displayShortestPath(graph, sourceAirport, destinationAirport, pathInfo);
        } else {
            System.out.println("No path found from " + sourceAirport.getName() + " to " + destinationAirport.getName());
        }
    }
    
    
    private static Vertex getAirport(Graph graph, String name) {
    	// Java Stream to navigate existing vertices
    	// filter it based on the vectors name using a lambda function 
    	// compare it to input using .equalsIgnoreCase()
    	// if one is found, define sourceAirport as that vertex, otherwise null
        return graph.getVertices().stream()
                .filter(vertex -> vertex.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    private static void displayShortestPath(Graph graph, Vertex source, Vertex destination, PathInfo pathInfo) {
    	// Display results about best path
    	System.out.println("\n--------------------------------------------------\n");
        System.out.println(" Best path from " + source.getName() + " to " + destination.getName() + ":");
        System.out.println(" Weighted Value = 70% of price and 30% of duration");
        System.out.println();
        System.out.println("    Weighted Value: " + pathInfo.weightedValue);
        System.out.println("    Path: " + pathInfo.path);
        System.out.println("\n--------------------------------------------------\n");
        
        // Split the path to get individual airports
        String[] airports = pathInfo.path.split(" -> ");
        
        // Initialize the totals
        double totalPrice = 0;
        int totalDuration = 0;

        // Iterate through each pair of consecutive airports in the path
        for (int i = 0; i < airports.length - 1; i++) {
            String sourceAirportName = airports[i];
            String destinationAirportName = airports[i + 1];

            // Find corresponding vertices for the source and destination airports
            // use same method as seen in user input
            Vertex sourceAirport = getAirport(graph, sourceAirportName);
            Vertex destinationAirport = getAirport(graph, destinationAirportName);

            // Check if vertices are found
            if (sourceAirport != null && destinationAirport != null) {
                // Find the edge (flight) between the source and destination airports
                Edge edge = graph.getEdges(sourceAirport).stream()
                        .filter(e -> e.getDestination().equals(destinationAirport))
                        .findFirst()
                        .orElse(null);

                // Check if the edge is found
                if (edge != null) {
                    // Display information about the flight segment
                    System.out.println(" Flight from " + sourceAirport.getName() + " to " + destinationAirport.getName() + ":");
                    System.out.println("    Price: $" + edge.getPrice());
                    System.out.println("    Duration: " + edge.getDuration() + " hours");
                    System.out.println();

                    // Add total price and duration for the entire trip
                    totalPrice += edge.getPrice();
                    totalDuration += edge.getDuration();
                }
                else {
                	System.out.println("ERROR: Flight not found.");
                }
            }
            else {
            	System.out.println("ERROR: Airports not found.");
            }
        }

        // Display total price and duration
        System.out.println("--------------------------------------------------\n");
        System.out.println(" Total Price: $" + totalPrice);
        System.out.println(" Total Duration: " + totalDuration + " hours");
        System.out.println("\n--------------------------------------------------\n");
    }

    private static PathInfo dijkstra(Graph graph, Vertex source, Vertex destination) {
        // Validate source and destination (this should never run because of validation before it is called in main method)
        if (source == null || destination == null) {
            System.out.println("ERROR: Invalid source or destination airport.");
            return new PathInfo(Double.POSITIVE_INFINITY, "");
        }

        // Priority queue to store vertices based on their weightedValue
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(node -> node.weightedValue));

        // Map to store best paths and their associated weightedValue
        Map<Vertex, PathInfo> paths = new HashMap<>();

        // Set initial weightedValue to infinity for all vertices, but 0 for the source
        // Add them to the priorityQueue
        for (Vertex vertex : graph.getVertices()) {
            paths.put(vertex, new PathInfo(Double.POSITIVE_INFINITY, ""));
            if (vertex.equals(source)) {
                paths.put(vertex, new PathInfo(0, source.getName()));
            }
            priorityQueue.add(new Node(vertex, paths.get(vertex).weightedValue));
        }

        // Dijkstra's algorithm main loop
        while (!priorityQueue.isEmpty()) {
            // Extract vertex with the smallest weightedValue from the priority queue
            Node current = priorityQueue.poll();
            
            // Break the loop once the destination vertex is processed
            if (current.vertex.equals(destination)) {
                break;
            }

            // Explore neighbors of the current vertex
            for (Edge edge : graph.getEdges(current.vertex)) {
                // Calculate the new weighted value for the neighbor (add current weightedValue + new edge weightedValue)
                double newWeightedValue = paths.get(current.vertex).weightedValue + calculateWeightedValue(edge);

                // Update the path and weighted value if a shorter path is found, update it in the priority queue
                if (newWeightedValue < paths.get(edge.getDestination()).weightedValue) {
                    paths.get(edge.getDestination()).weightedValue = newWeightedValue;
                    
                    paths.get(edge.getDestination()).path = paths.get(current.vertex).path + " -> " + edge.getDestination().getName();
                    
                    // Update the priority queue with the new weighted value for the neighbor
                    priorityQueue.add(new Node(edge.getDestination(), newWeightedValue));
                }
            }
        }

        return paths.get(destination);
    }

    private static double calculateWeightedValue(Edge edge) {
        // Weighted combination of price (70%) and duration (30%)
        return 0.7 * edge.getPrice() + 0.3 * edge.getDuration();
    }

    // Node object for priorityQueue variable, represent vertex with weightedValue
    private static class Node {
        Vertex vertex;
        double weightedValue;

        Node(Vertex vertex, double weightedValue) {
            this.vertex = vertex;
            this.weightedValue = weightedValue;
        }
    }

    // PathInfo object for paths variable, represent best path with weightedValue
    private static class PathInfo {
        double weightedValue;
        String path;

        PathInfo(double weightedValue, String path) {
            this.weightedValue = weightedValue;
            this.path = path;
        }
    }
}
