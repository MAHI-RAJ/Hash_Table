
import java.util.*;

public class StreamingCacheSystem {
    private final int L1_SIZE = 10000;

    // L1: In-Memory LinkedHashMap (Auto-LRU via accessOrder=true)
    private final Map<String, String> l1Cache = new LinkedHashMap<String, String>(L1_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            if (size() > L1_SIZE) {
                demoteToL2(eldest.getKey(), eldest.getValue());
                return true;
            }
            return false;
        }
    };

    // L2: Simulating SSD-backed storage
    private final Map<String, String> l2Cache = new HashMap<>();
    private final Map<String, Integer> accessCounts = new HashMap<>();

    // Stats counters
    private int l1Hits = 0, l2Hits = 0, l3Hits = 0;

    public String getVideo(String videoId) {
        // 1. Try L1 (RAM)
        if (l1Cache.containsKey(videoId)) {
            l1Hits++;
            return l1Cache.get(videoId) + " (L1 HIT)";
        }

        // 2. Try L2 (SSD)
        if (l2Cache.containsKey(videoId)) {
            l2Hits++;
            String data = l2Cache.get(videoId);

            // Promotion Logic: If popular, move to L1
            int count = accessCounts.getOrDefault(videoId, 0) + 1;
            accessCounts.put(videoId, count);
            if (count > 5) { // Threshold for promotion
                l1Cache.put(videoId, data);
                l2Cache.remove(videoId);
            }
            return data + " (L2 HIT)";
        }

        // 3. Try L3 (Database - Simulated)
        l3Hits++;
        String dbData = "VideoData_from_DB_" + videoId;

        // Add to L2 initially
        l2Cache.put(videoId, dbData);
        accessCounts.put(videoId, 1);

        return dbData + " (L3 HIT)";
    }

    private void demoteToL2(String key, String value) {
        l2Cache.put(key, value);
        accessCounts.put(key, 0); // Reset count on demotion
    }
}