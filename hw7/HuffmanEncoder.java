import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HuffmanEncoder {
    public static Map<Character, Integer> buildFrequencyTable(char[] inputSymbols) {
        Map<Character, Integer> result = new HashMap<>();
        for (char ch : inputSymbols) {
            if (!result.containsKey(ch)) {
                result.put(ch, 0);
            }
            result.put(ch, result.get(ch) + 1);
        }
        return result;
    }
    public static void main(String[] args) {
        char[] inputSymbols = FileUtils.readFile(args[0]);
        Map<Character, Integer> freqTable = buildFrequencyTable(inputSymbols);
        BinaryTrie binaryTrie = new BinaryTrie(freqTable);
        ObjectWriter ow = new ObjectWriter(args[0] + ".huf");
        ow.writeObject(binaryTrie);
        ow.writeObject(inputSymbols.length);
        Map<Character, BitSequence> lookUpTable = binaryTrie.buildLookupTable();
        List<BitSequence> list = new ArrayList<>();
        for (char ch : inputSymbols) {
            list.add(lookUpTable.get(ch));
        }
        BitSequence bitSequence = BitSequence.assemble(list);
        ow.writeObject(bitSequence);
    }
}
