import java.util.*;

public class Boggle {
    
    // File path of dictionary file
    static String dictPath = "trivial_words.txt";
    static private TrieNode root;
    static private int[][] dirs = {{-1, 0}, {0, -1}, {1, 0},
            {0, 1}, {-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
    /**
     * Solves a Boggle puzzle.
     * 
     * @param k The maximum number of words to return.
     * @param boardFilePath The file path to Boggle board file.
     * @return a list of words found in given Boggle board.
     *         The Strings are sorted in descending order of length.
     *         If multiple words have the same length,
     *         have them in ascending alphabetical order.
     */
    public static List<String> solve(int k, String boardFilePath) {
        // YOUR CODE HERE
        if (k <= 0) {
            throw new IllegalArgumentException();
        }
        root = new TrieNode();
        In wordFile = new In(dictPath);
        if (!wordFile.exists()) {
            throw new IllegalArgumentException();
        }
        while(wordFile.hasNextLine()) {
            root.insert(wordFile.readLine());
        }

        In boardFile = new In(boardFilePath);
        String[] boardLines = boardFile.readAllLines();
        int m = boardLines.length;
        if (m == 0) {
            throw new IllegalArgumentException();
        }
        int n = boardLines[0].length();
        char[][] board = new char[m][n];
        for (int i = 0; i < m; i++) {
            if (boardLines[i].length() != n) {
                throw new IllegalArgumentException();
            }
            for (int j = 0; j < n; j++) {
                board[i][j] = boardLines[i].charAt(j);
            }
        }

        Set<String> set = new TreeSet<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dfs(root, i, j, board, set);
            }
        }
        String[] arr = set.toArray(new String[set.size()]);
        Arrays.sort(arr, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s2.length() - s1.length();
            }
        });
        List<String> ans = new ArrayList<>();
        k = Math.min(k, arr.length);
        for (int i = 0; i < k; i++) {
            ans.add(arr[i]);
        }
        return new ArrayList<>(ans);
    }

    private static void dfs(TrieNode node, int i, int j, char[][] board, Set<String> ans) {
        if (!node.children.containsKey(board[i][j])) {
            return;
        }
        char ch = board[i][j];
        node = node.children.get(ch);
        if (!"".equals(node.word)) {
            ans.add(node.word);
        }
        board[i][j] = '#';
        for (int[] d : dirs) {
            int r = i + d[0];
            int c = j + d[1];
            if (0 <= r && 0 <= c && r < board.length && c < board[0].length) {
                dfs(node, r, c, board, ans);
            }
        }
        board[i][j] = ch;
    }

//    public static void main(String[] args) {
//        List<String> ans = solve(20, "exampleBoard2.txt");
//        System.out.println(ans);
//    }
}
