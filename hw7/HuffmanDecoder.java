public class HuffmanDecoder {
    public static void main(String[] args) {
        ObjectReader or = new ObjectReader(args[0]);
        BinaryTrie bTrie = (BinaryTrie) or.readObject();
        int numInputs = (int) or.readObject();
        BitSequence bitSequence = (BitSequence) or.readObject();

        char[] chars = new char[numInputs];

        for (int i = 0; i < numInputs; i++) {
            Match match = bTrie.longestPrefixMatch(bitSequence);
            chars[i] = match.getSymbol();
            bitSequence = bitSequence.allButFirstNBits(match.getSequence().length());
        }

        FileUtils.writeCharArray(args[1], chars);
    }
}
