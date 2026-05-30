package server;

public class RateLimiter {
    private final int maxToken;
    private double tokenBucket;

    private final double refillRate;
    private long lastUpdated;

    public RateLimiter(int limit) {
        maxToken = limit;
        tokenBucket = limit;
        lastUpdated = System.currentTimeMillis();

        refillRate = limit / 1000.0;
    }

    public boolean tryConsume() {
        long currentTime = System.currentTimeMillis();
        double elapsedTime = currentTime - lastUpdated;

        tokenBucket = Math.min(maxToken, tokenBucket + elapsedTime * refillRate);
        lastUpdated = currentTime;

        if (tokenBucket < 1.0) return false;
        tokenBucket -= 1.0;
        return true;
    }
}
