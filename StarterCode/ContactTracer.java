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

import javax.sound.midi.SysexMessage;

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
                System.out.println("DEBUG: Node " + i + ": ID=" + id);

                // You will want to store this ID. 
                // Using a Hashmap, I would map ID to i, call it the id number.
                table.put(id,i);
            }
            System.out.println(table.toString());

            // You will probably want to create an undirected graph G with n nodes
            // Initially with no edges but add a method to add an edge between two nodes
            List<List<Integer>> graph = new ArrayList<>(n);
            for (int i = 0; i < n; i++) graph.add(new ArrayList<>());

            // Get the various connections
            int m = Integer.parseInt(sc.nextLine());

            for (int e = 0; e < m; e++) {
                line = sc.nextLine().split(" ");
                String idA = line[0];
                String idB = line[1];

                System.out.println("DEBUG: Contact between " + idA + " and " + idB);
                // You might want to get the id number of idA and idB from the Hashmap
                // Then add the edge between idA and idB to the graph
                graph.get(table.get(idA)).add(table.get(idB));
                graph.get(table.get(idB)).add(table.get(idA));
            }
            System.out.println(graph.toString());

            // Get how many contacts have been infected and how far to report exposure from
            // an infected individual
            line = sc.nextLine().split(" ");
            int numInfected = Integer.parseInt(line[0]);
            int distance = Integer.parseInt(line[1]);

            ArrayList<String> infected = new ArrayList<String>(numInfected);
            for (int c = 0; c < numInfected; c++) {
                String idA = sc.nextLine();
                System.out.println("DEBUG: Infected: " + idA);
                infected.add(idA);
            }

            // Now process the information to get the results...
            // Use the Graph, infected list, and distance to get the result and print the number of
            // exposed individuals.

            int exposedIncrement = 0;

            for (int i = 0; i < numInfected; i++){
                visited[table.get(infected.get(i))] = true;
            }
            for (int i = 0; i < numInfected; i++){
                exposedIncrement += BFS(distance, table.get(infected.get(i)), graph, n, numInfected);
                // System.out.println("Current exposed: " + BFS(distance, table.get(infected.get(i)), graph, n, numInfected));
            }

            System.out.println("TOTAL EXPOSED: " + (exposedIncrement));

        } catch (IOException e) {
            System.err.println("Error reading in the graph: " + e.getMessage());
        }
    }

    // Breadth first search method
    public static int BFS(int distance, int startIdNum, List<List<Integer>> graph, int n, int numInfected){
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startIdNum, 0}); 

        int currentId = startIdNum; // Sets the current id to the starting id 
        int numOfNeighbors = graph.get(currentId).size(); // Gets number of neighbors for each id
        //System.out.println("Number of neighbors for node " + currentId + " = " + numOfNeighbors);
        int exposed = 0; // Number of people exposed, starts with only those infected

        visited[startIdNum] = true;
        // queue.add(startIdNum);

        // Iterate over the queue
        while(!queue.isEmpty()){

            int curr[] = queue.poll();
            //System.out.print(curr + " ");
            currentId = curr[0];
            int currentDistance = curr[1];

            exposed ++;

            // Stop exploring further if the distance is reached
            if (currentDistance >= distance) {
                continue;
            }

            // Explore neighbors
            for (int neighbor : graph.get(currentId)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(new int[]{neighbor, currentDistance + 1});
                }
            }
        }

       /*  for (int i = 0; i < numOfNeighbors; i++){ // For all of the current node's neighbors:
            if(!visited[graph.get(currentId).get(i)]){
                queue.add(graph.get(currentId).get(i)); // Add neighbor to the queue
                exposed++; // Adds person to the list of exposed
                System.out.println("Node " + graph.get(currentId).get(i) + " not visited, visiting. ");
                System.out.println("Current exposed (for loop): " + exposed);
            } else {
                System.out.println("Node " + graph.get(currentId).get(i) + " already visited, skipping.");
                System.out.println("Current exposed (for loop): " + exposed);
            }
            visited[graph.get(currentId).get(i)] = true;
        } */
        
        return exposed;
    }

}
