import java.util.*;
import java.util.concurrent.*;

public class AnalyticsEngine {
    // 1. Core Data Structures (Thread-safe for high throughput)
    private final ConcurrentHashMap<String, Integer> totalViews = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<String>> uniqueVisitors = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> trafficSources = new ConcurrentHashMap<>();

    private long totalProcessed = 0;

    /**
     * Requirement: Process events in real-time O(1)
     */
    public void processEvent(String url, String userId, String source) {
        // Increment total views
        totalViews.merge(url, 1, Integer::sum);

        // Track unique visitors (Thread-safe Set)
        uniqueVisitors.computeIfAbsent(url, k -> ConcurrentHashMap.newKeySet()).add(userId);

        // Track traffic sources
        trafficSources.merge(source, 1, Integer::sum);

        totalProcessed++;
    }

    /**
     * Requirement: Maintain Top 10 with high performance
     */
    public List<Map.Entry<String, Integer>> getTopPages(int n) {
        // Use a PriorityQueue (Min-Heap) to find Top N in O(TotalPages * log N)
        PriorityQueue<Map.Entry<String, Integer>> minHeap =
                new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<String, Integer> entry : totalViews.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > n) {
                minHeap.poll(); // Remove the smallest to keep only the largest N
            }
        }

        List<Map.Entry<String, Integer>> topN = new ArrayList<>(minHeap);
        topN.sort((a, b) -> b.getValue() - a.getValue()); // Final sort for display
        return topN;
    }

    public void displayDashboard() {
        System.out.println("\n--- REAL-TIME DASHBOARD (Updated every 5s) ---");
        List<Map.Entry<String, Integer>> top = getTopPages(3);

        for (Map.Entry<String, Integer> entry : top) {
            String url = entry.getKey();
            int views = entry.getValue();
            int uniques = uniqueVisitors.get(url).size();
            System.out.printf("Page: %s | Views: %d | Uniques: %d\n", url, views, uniques);
        }
    }
}