import java.util.*;

public class DNSCacheManager {
    private final int MAX_CAPACITY = 1000;
    private final long DEFAULT_TTL_MS = 300000; // 5 minutes in ms

    // LinkedHashMap(capacity, loadFactor, accessOrder=true) for LRU
    private final Map<String, DNSEntry> cache = new LinkedHashMap<String, DNSEntry>(MAX_CAPACITY, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
            return size() > MAX_CAPACITY; // Automatically evicts LRU item if full
        }
    };

    private int hits = 0;
    private int misses = 0;

    static class DNSEntry {
        String ipAddress;
        long expiryTime;

        DNSEntry(String ipAddress, long ttlMs) {
            this.ipAddress = ipAddress;
            this.expiryTime = System.currentTimeMillis() + ttlMs;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }

    public String resolve(String domain) {
        DNSEntry entry = cache.get(domain);

        // CASE 1: Cache Hit and not expired
        if (entry != null && !entry.isExpired()) {
            hits++;
            return entry.ipAddress + " (Cache HIT)";
        }

        // CASE 2: Cache Miss or Expired
        misses++;
        if (entry != null && entry.isExpired()) {
            cache.remove(domain); // Clean up expired entry
        }

        // Simulate Upstream Query (100ms delay)
        String upstreamIp = queryUpstreamDNS(domain);
        cache.put(domain, new DNSEntry(upstreamIp, DEFAULT_TTL_MS));

        return upstreamIp + " (Cache MISS/UPDATED)";
    }

    private String queryUpstreamDNS(String domain) {
        // Mocking an upstream IP response
        return "172.217." + new Random().nextInt(255) + "." + new Random().nextInt(255);
    }

    public void getCacheStats() {
        double hitRate = (hits + misses == 0) ? 0 : (double) hits / (hits + misses) * 100;
        System.out.printf("Stats -> Hits: %d, Misses: %d, Hit Rate: %.1f%%\n", hits, misses, hitRate);
    }
}