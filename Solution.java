import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class Solution {
  private static final int MAX_PENALTY = 1023;
  private static int numberOfEdges;
  private static int numberOfNodes;
  private static List<Edge> nodesAndEdges[];

  public static void main(String[] args) {

    Scanner scanner = new Scanner(System.in);
    numberOfNodes = scanner.nextInt();
    numberOfEdges = scanner.nextInt();
    initialize_nodesAndEdges();

    for (int i = 0; i < numberOfEdges; i++) {
      int nodeOne = scanner.nextInt();
      int nodeTwo = scanner.nextInt();
      int penalty = scanner.nextInt();

      if (nodesAndEdges[nodeOne].contains(nodeTwo) == false) {
        nodesAndEdges[nodeOne].add(new Edge(nodeTwo, penalty));
        nodesAndEdges[nodeTwo].add(new Edge(nodeOne, penalty));
      }
    }
    int start = scanner.nextInt();
    int goal = scanner.nextInt();
    scanner.close();
    int result = searchForPath_forEachBit(start, goal);
    System.out.println(result);
  }

  /**
   * Searches for the bits that are essentail to reach the goal by consequtively
   * excluding each bit from position 9 to position 1, i.e. edges with leftmost bit 
   * of the penalty value at these positions.
   *
   * Since the minimum penalty is calculated by applying the bitwise 'OR' for the value of the edges,
   * if by excluding edges with a certain bit there is no path from start to goal, this bit has to
   * be included in the final result.
   *
   * Example:
   * There is no path from start to goal when excluding edges with leftmost bits in 
   * the binary representation at the following positions: 
   * 1 (binary: 1, decimal value: 1) 
   * 4 (binary: 1000, decimal value: 8) 
   * 6 (binary: 100000, decimal value: 32)
   *
   * Therefore, the minimum penalty path will include only the value of these bits that are
   * essential to reach the goal from the start, namely: 1 + 8 + 32 = 41.
   *
   * @return A non-negative integer, representing the minimum penalty, 
   *         if the goal is reachable. Otherwise, it returns '-1'.
   */
  private static int searchForPath_forEachBit(int start, int goal) {
    
    // Checks if the goal is reachable without excluding any bits.
    if (breadthFirstSearch(start, goal, MAX_PENALTY + 1) == false) {
      return -1;
    }

    int minimumPenalty = 0;
    for (int i = 9; i >= 0; i--) {

      int currentBit = (int) Math.pow(2, i);
      if (breadthFirstSearch(start, goal, currentBit) == false) {
        minimumPenalty = minimumPenalty + currentBit;
        updateGraph(currentBit);
      }
    }
    return minimumPenalty;
  }

  private static boolean breadthFirstSearch(int start, int goal, int currentBit) {

    boolean[] visited = new boolean[numberOfNodes + 1];
    LinkedList<Integer> queue = new LinkedList<Integer>();
    queue.add(start);

    while (!queue.isEmpty()) {

      int current = queue.removeFirst();
      if (current == goal) {
        return true;
      }

      if (visited[current] == false) {
        visited[current] = true;

        List<Edge> edges = nodesAndEdges[current];
        for (int i = 0; i < edges.size(); i++) {

          int toNode = edges.get(i).toNode;
          int penalty = edges.get(i).penalty;

          if (visited[toNode] == false && penalty < currentBit) {
            queue.add(toNode);
          }
        }
      }
    }

    return false;
  }

  @SuppressWarnings("unchecked")
  private static void initialize_nodesAndEdges() {

    // Values at index '0' are not applied in the solution, so that each index corresponds to a node value.
    nodesAndEdges = new List[numberOfNodes + 1];
    for (int i = 1; i <= numberOfNodes; i++) {
      nodesAndEdges[i] = new ArrayList<Edge>();
    }
  }

  /**
   * Updates the graph, if there is no path from start to goal when excluding edges with the current
   * leftmost bit, i.e. this bit is essential for the path from start to goal. This is done so that
   * the edges with the current leftmost bit do not influence the search through the edges with
   * leftmost bits that is less than the current leftmost bit.
   *
   * Example:
   * 1. There is no path from start to goal when excluding edges with leftmost bit at position 4
   *    (binary: 1000, decimal value: 8). Therefore, this bit is essential for the path from start to goal.
   *
   * 2. We subtract the decimal value of 8 from all edges with leftmost bit at position 4.
   *    Thus, edges with penalty value of 8, 9, 10, 11, 12, 13, 14, 15 
   *    become edges with penalty value of 0, 1, 2, 3, 4, 5, 6, 7.
   */
  private static void updateGraph(int currentBit) {

    for (int i_node = 1; i_node <= numberOfNodes; i_node++) {

      List<Edge> list = nodesAndEdges[i_node];
      for (int i_edge = 0; i_edge < list.size(); i_edge++) {

        if (list.get(i_edge).penalty >= currentBit && list.get(i_edge).penalty < 2 * currentBit) {
          list.get(i_edge).penalty = list.get(i_edge).penalty - currentBit;
        }
      }
    }
  }

  static class Edge {
    int toNode;
    int penalty;

    public Edge(int toNode, int penalty) {
      this.toNode = toNode;
      this.penalty = penalty;
    }

    /**
     * Mutiple edges between two nodes are possible. To avoid going more than once through edges
     * between two of the same nodes that have also the same penalty value, we override the 'equals'
     * method so that such edges are not included more than once in the graph.
     */
    @Override
    public boolean equals(Object obj) {
      if ((obj instanceof Edge) == false) {
        return false;
      }
      Edge toCompare = (Edge) obj;
      return this.toNode == toCompare.toNode && this.penalty == toCompare.penalty;
    }

    @Override
    public int hashCode() {
      return 1;
    }
  }
}
