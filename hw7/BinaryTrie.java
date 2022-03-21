import edu.princeton.cs.algs4.MinPQ;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BinaryTrie implements Serializable {
    private Node root;

    public BinaryTrie(Map<Character, Integer> frequencyTable) {
        root = buildTrie(frequencyTable);
    }
    public Match longestPrefixMatch(BitSequence querySequence) {
        Node node = root;
        char symbol;
        StringBuilder sb = new StringBuilder();
        int length = querySequence.length();
        for (int i = 0; i < length && !node.isLeaf(); i++) {
            int bit = querySequence.bitAt(i);
            sb.append(bit);
            if (bit == 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }
        symbol = node.ch;
        return new Match(new BitSequence(sb.toString()), symbol);
    }
    public Map<Character, BitSequence> buildLookupTable() {
        Map<Character, BitSequence> result = new HashMap<>();
        Node node = root;
        StringBuilder sb = new StringBuilder();
        dfs(node, result, sb);
        return result;
    }

    private void dfs(Node node, Map<Character, BitSequence> result, StringBuilder sb) {
        if (node.isLeaf()) {
            result.put(node.ch, new BitSequence(sb.toString()));
            return;
        }
        if (node.left != null) {
            sb.append(0);
            dfs(node.left, result, sb);
            sb.deleteCharAt(sb.length() - 1);
        }
        if (node.right != null) {
            sb.append(1);
            dfs(node.right, result, sb);
            sb.deleteCharAt(sb.length() - 1);
        }
    }

    // Huffman trie node
    private static class Node implements Comparable<Node>, Serializable {
        private final char ch;
        private final int freq;
        private final Node left, right;

        Node(char ch, int freq, Node left, Node right) {
            this.ch    = ch;
            this.freq  = freq;
            this.left  = left;
            this.right = right;
        }

        // is the node a leaf node?
        private boolean isLeaf() {
            assert ((left == null) && (right == null)) || ((left != null) && (right != null));
            return (left == null) && (right == null);
        }

        // compare, based on frequency
        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }

    // build the Huffman trie given frequencies
    private Node buildTrie(Map<Character, Integer> frequencyTable) {

        // initialze priority queue with singleton trees
        MinPQ<Node> pq = new MinPQ<Node>();
        for (char c: frequencyTable.keySet()) {
            int freq = frequencyTable.get(c);
            if (freq > 0) {
                pq.insert(new Node(c, freq, null, null));
            }
        }

        // merge two smallest trees
        while (pq.size() > 1) {
            Node left  = pq.delMin();
            Node right = pq.delMin();
            Node parent = new Node('\0', left.freq + right.freq, left, right);
            pq.insert(parent);
        }
        return pq.delMin();
    }
}
