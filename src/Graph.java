import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

/*
Authors: Alex Harry, Cory Johns, Justin Keeling
Date: April 2, 2018
Overview: Graph stores a two-dimensional array representation of the graph in the input file and
contains all the functions for running Prim’s, Kruuskal’s, and Floyd-Warshall's Algorithms as well as
printing the graph.
*/
public class Graph {
    private int graph_size = 0;
    private ArrayList<ArrayList<Integer>> graph = new ArrayList<ArrayList<Integer>>();
    private ArrayList<String> vertexes;
    private PriorityQueue<Edge> edges;

    public Graph() {

    }

    /**
     * Sets the graph size and initializes the array list
     *
     * @param size of the graph
     */
    public void set_graph_size(int size) {
        graph_size = size;
        // Initialize the graph
        for (int i = 0; i < graph_size; i++) {
            graph.add(new ArrayList<Integer>());
        }
        // instantiates PriorityQueue when size is applicable.
        edges = new PriorityQueue<Edge>((graph_size * graph_size), new Edge_Comparator());
    }

    /**
     * Adds all the vertex names given by the input to this graph's vertex name list
     *
     * @param names of each vertex
     */
    public void set_vertexes(String[] names) {
        // Initialize the name list
        vertexes = new ArrayList<String>();
        // append each name
        for (String name : names) {
            vertexes.add(name);
        }
    }

    /**
     * Inserts the given number into the graph at the next available position
     *
     * @param inNumber
     */
    public void insert(int inNumber) {
        boolean success = false;

        for (int i = 0; i < graph_size; i++) {
            // append inNumber to the last empty location in the graph
            // REQUIRES graph_size to be accurate!
            if (graph.get(i).size() < graph_size) {
                graph.get(i).add(inNumber);
                success = true;
                break;
            }
        }
        if (!success) {
            // should not happen if the input is formated correctly
            System.out.println("Insert failed on: " + inNumber);
        }
    }
    /**
     * Runs Prims Algorithm to create a Minimum Search Tree.
     * Does not modify graph, only makes references from it.
     *
     */
    public void prim() {
        //starting vertex
        Vertex current = null;

        //used to randomly choose starting vertex
        int random_start = 0;//(int) (Math.random() * graph_size);

        //add all vertices to an ArrayList
        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Edge> mst = new ArrayList<>();

        for (int i = 0; i < graph_size; i++) {
            // create new vertex for each vertex.
            Vertex vertex = new Vertex(false, i, vertexes.get(i));
            //chooses 'start' equal to the'vertex' when 'i' = 'rand_start'.
            if (random_start == i) {
                //set the 'current' vertex's visited to true, and its length to 0: Requirements for start vertex.
                current = vertex;
                // change status of current vertex.
                current.visited = true;
                // adds all vertices to ArrayList for future reference.
                vertices.add(current);
            } else {
                // adds all vertices to ArrayList for future reference.
                vertices.add(vertex);
            }
        }
        // add elements to queue before while loop
        add_to_queue(vertices, current);
        while (edges.peek() != null) {
            // obtains the minimum edge.
            Edge min_edge = edges.poll();
            // obtains the new current vertex.
            current = min_edge.tail;
            mst.add(min_edge);
            if (!current.visited) {
                current.visited = true;
            }
            // remove invalid edges.
            check_queue_invalid_edges();
            // add more edges to queue from a new vertex.
            add_to_queue(vertices, current);
        }
        print_mst(mst);
    }
    /**
     * This method adds every possible edge to the queue from a given vertex
     *
     * @param vertices
     * @param current
     */
    private void add_to_queue(ArrayList<Vertex> vertices, Vertex current) {
        // checks each vertex.
        for (int i = 0; i < graph_size; i++) {
            //if the edge in the graph does not equal infinity from the current vertex to the i'th vertex, set i'th vertex edge-weight.
            if (graph.get(current.index).get(i) != Integer.MAX_VALUE && !vertices.get(i).visited) {
                // create a new edge with its predecessor as current.
                Edge edge = new Edge(current);
                // assign the tail vertex
                edge.tail = vertices.get(i);
                //assign the weight of the edge.
                edge.weight = graph.get(current.index).get(i);
                // adds the new edge to PriorityQueue.
                edges.add(edge);
            }
        }
    }
    /**
     * This method checks an edge's validity PriorityQueue.
     * If invalid, then its removes from queue.
     */
    public void check_queue_invalid_edges(){
       // creates an array of the PriorityQueue.
       Object[] array = edges.toArray();
       // variable to check the edges validity.
       Edge e;
       // checks each edge in queue.
       for(int i = 0; i < edges.size(); i++){
           e = (Edge)array[i];
           // if the tail has been visited, then it is not a valid edge anymore,remove it.
           if(e.tail.visited){
               edges.remove(e);
           }
       }
    }

    /**
     * Prints the MST for Prim's Algorithm.
     *
     * @param mst
     */
    public void print_mst(ArrayList<Edge> mst){
        String v;
        // prints each edges head and tail names.
        for(int i = 0; i < mst.size(); i++){
            v = mst.get(i).head.vertex_name;
            System.out.print(v);
            v = mst.get(i).tail.vertex_name;
            System.out.print(v + " ");
        }
        System.out.println("\n");
    }

    public void kruuskal() {
        // copy graph for use here
        ArrayList<ArrayList<Integer>> d = duplicate_matrix(graph);
    }

    /**
     * Runs Floyd-Warshall's algorithm on the instance variable graph
     * does not modify graph but makes a copy and then prints each step of the algorithm
     */
    public void floyd_warshall() {
        // copy graph for use here
        ArrayList<ArrayList<Integer>> d = duplicate_matrix(graph);

        // find the shortest path
        for (int k = 0; k < graph_size; k++) {
            for (int i = 0; i < graph_size; i++) {
                for (int j = 0; j < graph_size; j++) {
                    // cannot perform arithmetic on infinity
                    if (!is_max_value(d.get(i).get(k).intValue()) && !is_max_value(d.get(k).get(j).intValue())) {
                        // test if path is shorter then current one
                        if (d.get(i).get(j).intValue() > d.get(i).get(k).intValue() + d.get(k).get(j).intValue()) {
                            // path is shorter, replace current
                            d.get(i).set(j, d.get(i).get(k).intValue() + d.get(k).get(j).intValue());
                            // print this step
                            print_graph(d, vertexes);
                        }
                    }
                }
            }
        }
    }

    /**
     * Makes a copy of the given matrix that is independent of the original
     *
     * @param matrix ,the input matrix
     * @return a new matrix with the same content as the input
     */
    private ArrayList<ArrayList<Integer>> duplicate_matrix(ArrayList<ArrayList<Integer>> matrix) {
        // Initialize the min weight graph
        ArrayList<ArrayList<Integer>> d = new ArrayList<ArrayList<Integer>>();

        // Initialize the graph
        for (int i = 0; i < matrix.size(); i++) {
            d.add(new ArrayList<Integer>());
        }

        // add initial values
        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < matrix.get(i).size(); j++) {
                // graph is already initialized
                d.get(i).add(matrix.get(i).get(j).intValue());
            }
        }
        return d;
    }

    /**
     * Tests if the input is the designated infinity for the adjacency matrix as determined by Main.java
     *
     * @param test
     * @return true if test is equal to the infinity value of Main
     */
    private boolean is_max_value(int test) {
        return test == Main.infinity;
    }

    /**
     * Prints the current version of the instance variable graph
     */
    public void print_graph() {
        print_graph(graph, vertexes);
    }

    /**
     * Prints the given matrix with the given vertex names
     *
     * @param matrix
     * @param vertex_names
     */
    public void print_graph(ArrayList<ArrayList<Integer>> matrix, ArrayList<String> vertex_names) {
        // print a spacer
        System.out.print("  ");
        // print vertex names along the top of the matrix
        for (int i = 0; i < vertex_names.size() - 1; i++) {
            System.out.print(vertex_names.get(i) + ",");
        }
        // print last vertex name with a newline instead of a ","
        System.out.print(vertex_names.get(vertex_names.size() - 1) + "\n");

        // print each row of the matrix
        for (int i = 0; i < matrix.size(); i++) {
            // print vertex name
            System.out.print(vertex_names.get(i) + " ");

            // print the values
            for (int j = 0; j < matrix.get(i).size() - 1; j++) {
                System.out.print(get_print_value(matrix, i, j) + ",");
            }
            // print the last value with a newline instead of a ","
            System.out.print(get_print_value(matrix, i, matrix.get(i).size() - 1) + "\n");
        }
        // print a space at the end of the matrix
        System.out.println();
    }

    /**
     * Gets the String that represents the value at the given row & col of the matrix
     *
     * @param matrix the source matrix
     * @param row    of the value
     * @param col    of the value
     * @return the representative String
     */
    private String get_print_value(ArrayList<ArrayList<Integer>> matrix, int row, int col) {
        int value = matrix.get(row).get(col).intValue();
        if (is_max_value(value)) {
            return "\u221E";
        } else {
            return "" + value;
        }
    }

    /**
     * @Class Vertex creates objects to store data in regards to the adjacency matrix.
     */
    public static class Vertex {
        // variable to hold visited if it has been visited or not
        boolean visited;

        // index from graph
        int index;

        // name of the vertex
        String vertex_name;

        public Vertex(boolean in_status, int in_index, String in_name) {
            visited = in_status;
            index = in_index;
            vertex_name = in_name;
        }
    }

    /**
     * @Class Edge connects vertices.
     * 'head' is the predecessor, 'tail' is the vertex that head connects to.
     */
    public static class Edge {
        // weight of edge
        int weight = 0;
        // The parent,essentially, : The vertex in which the edge comes from,
        // the edge's vertex is the vertex the edge connects to
        Vertex head;
        Vertex tail = null;

        public Edge(Vertex in_head) {
            head = in_head;
        }
    }

    /**
     * @Class Edge_Comparator sorts PriorityQueue from least to greatest.
     * An element with the same priority as a previously inserted element
     * will have less priority.
     */
    public class Edge_Comparator implements Comparator<Edge> {
        // used to compare weights of edges.
        @Override
        public int compare(Edge e1, Edge e2) {
            if (e1.weight < e2.weight) {
                return -1;
            } else if (e1.weight >= e2.weight) {
                return 1;
            }
            return 0;
        }
    }
}

