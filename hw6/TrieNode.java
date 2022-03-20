import java.util.HashMap;

public class TrieNode {
    public String word;
    public HashMap<Character, TrieNode> children;

    public TrieNode() {
        this.word = "";
        this.children = new HashMap<>();
    }

    public void insert(String word) {
        TrieNode node = this;
        for (char ch : word.toCharArray()) {
            if (!node.children.containsKey(ch)) {
                node.children.put(ch, new TrieNode());
            }
            node = node.children.get(ch);
        }
        node.word = word;
    }
}
