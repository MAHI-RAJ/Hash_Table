import java.util.*;

public class PlagiarismDetector {
    // Inverted Index: N-gram -> Set of Document IDs that have it
    private final Map<String, Set<String>> globalIndex = new HashMap<>();
    private final int N = 5; // Using 5-grams for balance between speed and accuracy

    /**
     * Requirement: Index a document into the system
     */
    public void indexDocument(String docId, String content) {
        List<String> nGrams = extractNGrams(content);
        for (String gram : nGrams) {
            globalIndex.computeIfAbsent(gram, k -> new HashSet<>()).add(docId);
        }
    }

    /**
     * Requirement: Find matches and calculate similarity in O(n)
     */
    public void analyzeSubmission(String content) {
        List<String> submissionGrams = extractNGrams(content);
        Map<String, Integer> matchCounts = new HashMap<>();

        // O(n) lookup: Check each n-gram of the submission against the index
        for (String gram : submissionGrams) {
            if (globalIndex.containsKey(gram)) {
                for (String existingDocId : globalIndex.get(gram)) {
                    matchCounts.put(existingDocId, matchCounts.getOrDefault(existingDocId, 0) + 1);
                }
            }
        }

        // Calculate and report similarity
        System.out.println("Analysis Results for Submission:");
        for (Map.Entry<String, Integer> entry : matchCounts.entrySet()) {
            double similarity = (double) entry.getValue() / submissionGrams.size() * 100;
            if (similarity > 10.0) { // Only report suspicious levels
                String status = (similarity > 50.0) ? "!!! PLAGIARISM !!!" : "Suspicious";
                System.out.printf("-> Match with %s: %.2f%% (%s)\n", entry.getKey(), similarity, status);
            }
        }
    }

    private List<String> extractNGrams(String text) {
        String[] words = text.toLowerCase().replaceAll("[^a-zA-Z ]", "").split("\\s+");
        List<String> nGrams = new ArrayList<>();
        for (int i = 0; i <= words.length - N; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < N; j++) {
                sb.append(words[i + j]).append(" ");
            }
            nGrams.add(sb.toString().trim());
        }
        return nGrams;
    }
}