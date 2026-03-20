import java.util.*;

public class AutocompleteSystem {
    class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        // Optimization: Store top 10 suggestions at each prefix node
        List<String> topSuggestions = new ArrayList<>();
        int frequency = 0;
        boolean isEndOfWord = false;
    }

    private final TrieNode root = new TrieNode();
    private final Map<String, Integer> globalFrequencies = new HashMap<>();

    /**
     * Requirement: Update frequencies and rebuild prefix caches
     */
    public void updateSearch(String query) {
        globalFrequencies.put(query, globalFrequencies.getOrDefault(query, 0) + 1);
        insertIntoTrie(query);
    }

    private void insertIntoTrie(String query) {
        TrieNode current = root;
        for (char c : query.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
            updateTopSuggestions(current, query);
        }
        current.isEndOfWord = true;
    }

    /**
     * Requirement: Return Top 10 in <50ms (O(L) where L is prefix length)
     */
    public List<String> getSuggestions(String prefix) {
        TrieNode current = root;
        for (char c : prefix.toCharArray()) {
            if (!current.children.containsKey(c)) return new ArrayList<>();
            current = current.children.get(c);
        }
        return current.topSuggestions;
    }

    private void updateTopSuggestions(TrieNode node, String query) {
        if (!node.topSuggestions.contains(query)) {
            node.topSuggestions.add(query);
        }
        // Sort by global frequency and keep only top 10
        node.topSuggestions.sort((a, b) ->
                globalFrequencies.get(b) - globalFrequencies.get(a));

        if (node.topSuggestions.size() > 10) {
            node.topSuggestions.remove(10);
        }
    }
}