/**************************
 * Author: Christian Duncan (Starting Code)
 * Modified by: Andrew Ehlers, Grant Foody
 * 
 * Fall 2024, CSC215
 * Given a graph of contact points between individuals, a list of infected individuals, 
 * and a distance D, determine how many individuals are with D contacts of an infected individual.
 */

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class ContactTracer {
    public static final String DEFAULT_NAME = "IO/simple0.input";
    public static boolean[] visited;

    public static Hashtable<String,Integer> table;

    public static void main(String[] args) {

        String fileName = DEFAULT_NAME;

        if (args.length >= 1) {
            fileName = args[0];
        }

        processFile(fileName);
    }


    // Read in the problem and produce the output
    public static void processFile(String fileName) {
        try {

            // Hashtable<String,Integer> table = new Hashtable<>();
            table = new Hashtable<>();
    
            String[] line;

            // Open up the file for parsing
            Scanner sc = new Scanner(new FileReader(fileName));

            // Get the number of names (IDs)
            int n = Integer.parseInt(sc.nextLine());
            visited = new boolean[n];

            for (int i = 0; i < n; i++) {
                String id = sc.nextLine();
                // System.out.println("DEBUG: Node " + i + ": ID=" + id);

                // Stores ID and index in a hash table
                table.put(id,i);
            }

            // Undirected graph with n nodes 
            // For each node, stores a list of neighboring nodes
            List<List<Integer>> graph = new ArrayList<>(n);
            for (int i = 0; i < n; i++) graph.add(new ArrayList<>());

            // Get the various connections
            int m = Integer.parseInt(sc.nextLine());

            for (int e = 0; e < m; e++) {
                line = sc.nextLine().split(" ");
                String idA = line[0];
                String idB = line[1];

                // System.out.println("DEBUG: Contact between " + idA + " and " + idB);

                // Gets id of idA and idB from hash table and adds edge to the graph 
                graph.get(table.get(idA)).add(table.get(idB));
                graph.get(table.get(idB)).add(table.get(idA));
            }

            // Get how many contacts have been infected and how far to report exposure from an infected individual
            line = sc.nextLine().split(" ");
            int numInfected = Integer.parseInt(line[0]);
            int distance = Integer.parseInt(line[1]);

            ArrayList<String> infected = new ArrayList<String>(numInfected);
            for (int c = 0; c < numInfected; c++) {
                String idA = sc.nextLine();
                // System.out.println("DEBUG: Infected: " + idA);
                infected.add(idA);
            }

            // Process results to calculate number of exposed individuals:

            int exposedIncrement = 0; // Starts the amount of people exposed at 0

            // Sets all infected to visited by default
            for (int i = 0; i < numInfected; i++){
                visited[table.get(infected.get(i))] = true;
            }

            // Adds each new run of BFS to the current total of individuals exposed 
            for (int i = 0; i < numInfected; i++){
                exposedIncrement += BFS(distance, table.get(infected.get(i)), graph, n);
            }

            // Final result (Adds exposed individuals and the infected individuals)
            System.out.println("TOTAL EXPOSED: " + (exposedIncrement));

        } catch (IOException e) {
            System.err.println("Error reading in the graph: " + e.getMessage());
        }
    }

    // BFS method (to determine all exposed neighbors of a certain node within a certain distance)
    public static int BFS(int distance, int startIdNum, List<List<Integer>> graph, int n){
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startIdNum, 0}); 

        int currentId = startIdNum; // Sets the current id to the starting id 
        int exposed = 0; // Number of people exposed, starts with 0 and adds originally infected on later

        visited[startIdNum] = true;

        // While the queue is not empty (there are still neighboring nodes to search)
        while(!queue.isEmpty()){

            int curr[] = queue.poll();
            currentId = curr[0];
            int currentDistance = curr[1];

            // Increment exposed 
            exposed ++;

            // Stop exploring further if the distance is reached
            if (currentDistance >= distance) {
                continue;
            }

            // Explore neighbors
            for (int neighbor : graph.get(currentId)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(new int[]{neighbor, currentDistance + 1}); // Adds a neighbor to the queue only if it hasn't been visited
                }
            }
        }
        
        return exposed;
    }

}
