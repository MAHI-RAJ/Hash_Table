import java.util.*;

public class UsernameService {
    // Core Registry: Username -> UserID
    private Map<String, Long> registeredUsers = new HashMap<>();
    // Popularity Tracker: Username -> Count of attempts
    private Map<String, Integer> attemptFrequency = new HashMap<>();

    private Random random = new Random();

    /**
     * Requirement: Check availability in O(1) time
     */
    public boolean checkAvailability(String username) {
        // Track the attempt regardless of availability
        attemptFrequency.put(username, attemptFrequency.getOrDefault(username, 0) + 1);

        // Returns true if the username is NOT in the map
        return !registeredUsers.containsKey(username.toLowerCase());
    }

    /**
     * Requirement: Suggest similar usernames
     */
    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();
        int count = 0;

        while (suggestions.size() < 3) {
            String candidate = username + (random.nextInt(900) + 100);
            if (!registeredUsers.containsKey(candidate)) {
                suggestions.add(candidate);
            }
        }
        return suggestions;
    }

    /**
     * Requirement: Track popularity
     */
    public String getMostAttempted() {
        return Collections.max(attemptFrequency.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public void register(String username, long id) {
        registeredUsers.put(username.toLowerCase(), id);
    }
}