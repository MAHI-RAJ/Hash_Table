import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimiter {
    // ClientID -> Their specific Token Bucket
    private final ConcurrentHashMap<String, TokenBucket> clientBuckets = new ConcurrentHashMap<>();

    private final long MAX_TOKENS = 1000;
    private final long REFILL_RATE_MS = 3600000 / MAX_TOKENS; // Refill 1 token every 3.6s

    static class TokenBucket {
        AtomicLong tokens;
        long lastRefillTimestamp;

        TokenBucket(long maxTokens) {
            this.tokens = new AtomicLong(maxTokens);
            this.lastRefillTimestamp = System.currentTimeMillis();
        }
    }

    /**
     * Requirement: Respond within 1ms using O(1) Lookup
     */
    public boolean checkRateLimit(String clientId) {
        TokenBucket bucket = clientBuckets.computeIfAbsent(clientId, k -> new TokenBucket(MAX_TOKENS));

        synchronized (bucket) {
            refill(bucket);

            if (bucket.tokens.get() > 0) {
                bucket.tokens.decrementAndGet();
                return true; // Request Allowed
            }
        }
        return false; // Request Denied
    }

    private void refill(TokenBucket bucket) {
        long now = System.currentTimeMillis();
        long timeElapsed = now - bucket.lastRefillTimestamp;

        // Calculate how many tokens should have been added since last check
        long tokensToAdd = timeElapsed / REFILL_RATE_MS;

        if (tokensToAdd > 0) {
            long newTokenCount = Math.min(MAX_TOKENS, bucket.tokens.get() + tokensToAdd);
            bucket.tokens.set(newTokenCount);
            bucket.lastRefillTimestamp = now;
        }
    }

    public long getRemaining(String clientId) {
        TokenBucket bucket = clientBuckets.get(clientId);
        return (bucket != null) ? bucket.tokens.get() : MAX_TOKENS;
    }
}