import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FlashSaleManager {
    // Thread-safe inventory: ProductID -> Atomic Stock Counter
    private final ConcurrentHashMap<String, AtomicInteger> inventory = new ConcurrentHashMap<>();

    // Waiting List: ProductID -> Queue of UserIDs (FIFO)
    private final ConcurrentHashMap<String, Queue<Integer>> waitingLists = new ConcurrentHashMap<>();

    public void initializeStock(String productId, int count) {
        inventory.put(productId, new AtomicInteger(count));
        waitingLists.put(productId, new ConcurrentLinkedQueue<>());
    }

    /**
     * Requirement: Instant availability check in O(1)
     */
    public int checkStock(String productId) {
        AtomicInteger stock = inventory.get(productId);
        return (stock != null) ? stock.get() : 0;
    }

    /**
     * Requirement: Prevent overselling with Atomic Operations
     */
    public String purchaseItem(String productId, int userId) {
        AtomicInteger stock = inventory.get(productId);

        if (stock == null) return "Product Not Found";

        // Attempt to decrement ONLY if stock > 0 (Atomic check)
        while (true) {
            int currentStock = stock.get();
            if (currentStock <= 0) {
                // Add to waiting list if stock is empty
                waitingLists.get(productId).add(userId);
                int position = waitingLists.get(productId).size();
                return "Out of Stock. Added to waiting list at position #" + position;
            }

            // CAS (Compare and Set): Only update if stock hasn't changed since we read it
            if (stock.compareAndSet(currentStock, currentStock - 1)) {
                return "Success! Purchase complete. " + (currentStock - 1) + " units remaining.";
            }
        }
    }
}