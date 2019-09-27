import java.util.ArrayList;
import java.util.LinkedList;

/*
    СЛОЖНОСТЬ АЛГОРИТМА:
    Bfs вносит O(|V|+|E|), тут это O(n^2), для каждый двух вершин - дает O(n^4)
*/

/*
    Здесь на всякий случай реализовано 2 разных алгоритма: Дейкстра и bfs, оба выдают одинаковый результат.

    Что можно упростить: так как циклов нет, то в bfs можно не проверять, посетили мы вершину в первый раз или нет,
    хотя тут это на всякий случай написано.
 */

public class Task1 {
    public static void main(String[] args) {

        //input data
        // Here I've replaced "[" by "{" like it is in Java
        int[][] myNet = {{7, 39}, {2, 74}, {83, 20}, {55, 46}, {39, 26}, {0, 44}, {50, 16}, {46, 75}, {57, 4},
                {87, 17}, {43, 62}, {61, 1}, {78, 63}, {64, 42}, {88, 83}, {28, 97}, {66, 99}, {60, 58}, {67, 34},
                {32, 31}, {15, 79}, {92, 0}, {82, 36}, {56, 59}, {21, 75}, {79, 60}, {52, 93}, {39, 30}, {74, 0},
                {56, 61}, {27, 51}, {78, 23}, {55, 96}, {41, 76}, {18, 13}, {33, 54}, {18, 74}, {16, 63}, {85, 89},
                {36, 37}, {3, 8}, {66, 4}, {53, 10}, {91, 62}, {80, 3}, {69, 40}, {4, 94}, {45, 77}, {24, 77},
                {88, 59}, {86, 32}, {48, 32}, {45, 38}, {53, 14}, {70, 47}, {92, 72}, {7, 81}, {28, 42}, {4, 11},
                {19, 98}, {13, 71}, {17, 85}, {39, 12}, {19, 48}, {73, 6}, {35, 52}, {84, 53}, {14, 90}, {21, 79},
                {76, 2}, {86, 9}, {83, 16}, {91, 87}, {61, 66}, {53, 85}, {29, 99}, {54, 65}, {54, 68}, {82, 22},
                {25, 91}, {56, 22}, {49, 47}, {27, 5}, {77, 67}, {2, 64}, {47, 81}, {68, 81}, {51, 13}, {38, 35},
                {58, 95}, {14, 79}, {3, 72}, {40, 52}, {73, 68}, {98, 92}, {48, 26}, {78, 90}, {6, 77}, {87, 32}};


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
