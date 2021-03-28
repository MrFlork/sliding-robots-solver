package net.booru.slidingrobots;

public class Timer {
    private final long iStart = System.nanoTime();
    private long iDurationNanos;

    public void close() {
        iDurationNanos = System.nanoTime() - iStart;
    }

    public double getDurationMillis() {
        return iDurationNanos / 1000_000.0;
    }

    @Override
    public String toString() {
        return String.format("Time %f (ms)", getDurationMillis());
    }
}
