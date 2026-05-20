package server;

public class RateLimiter {
    private final int maxToken;
    private int tokenBucket;

    private long lastUpdated;

    public RateLimiter(int limit) {
        maxToken = limit;
        tokenBucket = limit;
        lastUpdated = System.currentTimeMillis();
    }

    public void updateTokens() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdated < 1000) return;

        tokenBucket = maxToken;
        lastUpdated = currentTime;
    }

    public boolean isEnoughTokens() {
        return tokenBucket > 0;
    }

    public void takeToken() {
        tokenBucket--;
    }
}
