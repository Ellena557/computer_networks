import java.util.ArrayList;
import java.util.LinkedList;

/*
    СЛОЖНОСТЬ АЛГОРИТМА:
    Bfs вносит O(|V|+|E|), тут это O(n^2), для каждый двух вершин - дает O(n^4)
     */

/*
Main difference from the 1st task: here we add array "visited" in BFS to look after cycles
(in the 1st task it was unnecessary)
 */


public class Task2 {
    public static void main(String[] args) {

        //input data
        // Here I've replaced "[" by "{" like it is in Java
int[][] myNet = {{39, 16}, {63, 64}, {37, 22}, {70, 12}, {21, 3}, {58, 61}, {25, 71}, {31, 56},
        {48, 36}, {63, 36}, {10, 45}, {74, 44}, {2, 35}, {68, 51}, {53, 10}, {43, 53}, {60, 72},
        {60, 1}, {11, 35}, {28, 57}, {25, 74}, {13, 60}, {66, 14}, {53, 31}, {18, 19}, {61, 24},
        {5, 2}, {30, 38}, {49, 14}, {12, 52}, {17, 25}, {21, 34}, {69, 57}, {74, 55}, {32, 52},
        {5, 23}, {5, 46}, {67, 8}, {30, 41}, {73, 62}, {15, 4}, {25, 48}, {40, 14}, {55, 65},
        {6, 37}, {40, 57}, {59, 42}, {34, 47}, {9, 72}, {67, 61}, {27, 44}, {51, 31}, {32, 16},
        {58, 16}, {57, 7}, {50, 47}, {15, 74}, {54, 30}, {7, 20}, {39, 33}, {9, 22}, {29, 50},
        {18, 39}, {35, 69}, {0, 59}, {2, 29}, {65, 62}, {40, 45}, {42, 33}, {73, 26}, {54, 0},
        {38, 63}, {54, 53}, {7, 1}, {15, 10}, {24, 6}, {12, 42}, {7, 9}, {49, 35}, {21, 0}, {54, 70},
        {73, 72}, {24, 67}, {52, 69}, {10, 16}, {44, 19}, {72, 24}, {16, 5}, {31, 25}, {9, 72}, {72, 13},
        {46, 43}, {24, 72}, {20, 69}, {5, 19}, {51, 63}, {68, 9}, {22, 74}, {1, 9}, {34, 1}, {29, 31},
        {63, 64}, {41, 33}, {19, 19}, {33, 14}, {24, 12}, {53, 15}, {23, 5}, {43, 35}, {19, 73}, {26, 43},
        {25, 56}, {70, 38}, {58, 42}, {27, 72}, {30, 65}, {41, 18}, {6, 16}, {5, 40}, {61, 16}, {21, 53},
        {36, 71}, {58, 6}, {35, 47}, {25, 50}, {36, 31}, {50, 56}, {68, 17}, {17, 27}, {30, 9}, {41, 9},
        {12, 12}, {24, 15}, {48, 16}, {44, 72}, {3, 11}, {27, 24}, {13, 62}, {44, 12}, {43, 53}, {42, 31},
        {8, 8}, {7, 56}, {57, 17}, {66, 39}, {15, 19}, {52, 34}, {69, 23}, {50, 24}, {50, 68}, {27, 52},
        {16, 66}, {28, 37}, {26, 59}, {53, 46}, {62, 12}, {43, 65}, {55, 1}, {12, 46}, {41, 49}, {47, 42},
        {15, 15}, {69, 0}, {41, 62}, {3, 34}, {43, 15}, {23, 1}, {24, 72}, {73, 16}, {29, 40}, {68, 71},
        {12, 15}, {41, 50}, {42, 66}, {9, 69}, {46, 60}, {27, 31}, {22, 14}, {22, 65}, {22, 73}, {61, 35},
        {21, 11}, {64, 62}, {12, 70}, {61, 71}, {58, 7}, {30, 54}, {15, 16}, {47, 49}, {17, 51}, {49, 65},
        {72, 18}, {18, 39}, {56, 15}, {14, 2}, {26, 20}, {4, 44}, {0, 2}, {12, 47}, {22, 68}, {6, 59}, {41, 16},
        {21, 4}, {60, 7}, {50, 56}, {30, 70}, {10, 34}, {49, 54}, {74, 17}, {24, 47}, {37, 67}, {16, 41}, {74, 21},
        {15, 3}, {8, 2}, {25, 4}, {52, 62}, {20, 58}, {60, 35}, {9, 52}, {72, 20}, {57, 71}, {23, 9}, {28, 1}};

        //int[][] myNet2 = {{1, 2}, {2, 0}, {0, 1}, {1, 0}, {0, 2}, {2, 1}};

        int compNum = 100; //number of computers


        int result = findDiam(myNet, compNum);
        System.out.println("Диаметр данной сети: " + result);
    }

    public static int findDiam(int[][] net, int numComps) {
        int diam = 0; // the minimum: when there is only 1 computer in our Net

        int distances[][] = new int[numComps][numComps];

        //initialize
        for (int i = 0; i < numComps; i++) {
            for (int j = 0; j < numComps; j++) {
                distances[i][j] = 0;
            }
        }

        // представляем нашу сеть в виде матрицы смежности: 1-соединены, 0-не соединены
        for (int i = 0; i < net.length; i++) {
            distances[net[i][0]][net[i][1]] = 1;
            // у нас неориентированный граф
            distances[net[i][1]][net[i][0]] = 1;
        }

        int paths[][] = new int[numComps][numComps];

        for (int i = 0; i < numComps; i++) {
            for (int j = 0; j < numComps; j++) {
                if (i != j) {
                    paths[i][j] = dijkstra(distances, i, j, numComps);

                    // We need second condition to avoid counting path between to computers that are NOT connected at all
                    // (even including transitive connections
                    if (paths[i][j] > diam && paths[i][j] != Integer.MAX_VALUE) {
                        diam = paths[i][j];
                        //System.out.println("iii" + i + " " + j + " d = " + diam);
                    }
                }
            }
        }

        return diam;
    }

    //realization of BFS
    public static int myBfs(int[][] net, int s, int t, int numComps) {
        int netLen = numComps;
        int d[] = new int[netLen];

        boolean[] visited = new boolean[netLen];
        for (int i = 0; i < netLen; i++) {
            if (i != s) {
                d[i] = Integer.MAX_VALUE;
                visited[i] = false;
            }
        }

        d[s] = 0;
        visited[s] = true;

        LinkedList<Integer> comps = new LinkedList<>();
        comps.add(s);
        while (comps.size() > 0) {
            int u = comps.getFirst();
            comps.pollFirst();
            visited[u] = true;
            for (int v = 0; v < netLen; v++) {
                if (net[u][v] > 0 || net[v][u] > 0) {
                    if (!visited[v]) {
                        if (d[v] > d[u] + 1) {
                            d[v] = d[u] + 1;
                            comps.add(v);
                        }
                    }
                }
            }
        }

        return d[t];
    }


    private static int dijkstra(int[][] net, int s, int t, int num) {
        int[] d = new int[num];
        for (int i = 0; i < num; i++) {
            if (i != s) {
                d[i] = Integer.MAX_VALUE;
            }
        }
        d[s] = 0;
        int visitedCount = 0;
        boolean[] visited = new boolean[num];
        for (int i = 0; i < num; i++) {
            visited[i] = false;
        }
        while (visitedCount < num) {
            int localMin = Integer.MAX_VALUE;
            int localIndex = 0;
            for (int i = 0; i < num; i++) {
                if (d[i] < localMin && !visited[i]) {
                    localMin = d[i];
                    localIndex = i;
                }
            }
            visited[localIndex] = true;
            visitedCount += 1;

            for (int i = 0; i < num; i++) {
                if (net[i][localIndex] > 0 || net[localIndex][i] > 0) {
                    if (!visited[i]) {
                        if (d[i] > d[localIndex] + 1) {
                            d[i] = d[localIndex] + 1;
                        }
                    }
                }
            }
        }
        return d[t];
    }
}
