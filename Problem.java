import java.util.*;

public class FraudDetectionEngine {
    static class Transaction {
        int id;
        int amount;
        String merchant;
        long timestamp; // epoch ms

        Transaction(int id, int amount, String merchant, long timestamp) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.timestamp = timestamp;
        }
    }

    // Amount -> Transaction (to find complements)
    private final Map<Integer, Transaction> amountHistory = new HashMap<>();

    // Merchant:Amount -> List of Account IDs (to find duplicates)
    private final Map<String, List<Integer>> duplicateTracker = new HashMap<>();

    /**
     * Requirement: Find pairs that sum to target in O(n)
     */
    public List<String> findTwoSum(List<Transaction> transactions, int target) {
        List<String> suspiciousPairs = new ArrayList<>();
        amountHistory.clear();

        for (Transaction t : transactions) {
            int complement = target - t.amount;

            if (amountHistory.containsKey(complement)) {
                Transaction match = amountHistory.get(complement);
                suspiciousPairs.add("Match Found: ID " + t.id + " + ID " + match.id);
            }
            amountHistory.put(t.amount, t);
        }
        return suspiciousPairs;
    }

    /**
     * Requirement: Detect same amount/merchant across different accounts
     */
    public void detectDuplicates(Transaction t, int accountId) {
        String key = t.merchant + ":" + t.amount;
        duplicateTracker.computeIfAbsent(key, k -> new ArrayList<>()).add(accountId);

        if (duplicateTracker.get(key).size() > 1) {
            System.out.println("ALERT: Duplicate pattern detected for " + key);
        }
    }
}