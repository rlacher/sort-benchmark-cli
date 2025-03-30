package com.github.rlacher.sortanalyser.core;

/**
 * Stores the result of a benchmark operation.
 */
public final class BenchmarkResult
{
    // The time taken to execute the operation in milliseconds.
    private final long executionTimeMs;

    // The amount of runtime memory used during the operation in kilobytes.
    private final long memoryUsedKb;

    /**
     * Constructs a new BenchmarkResult object.
     *
     * @param executionTimeMs The time taken to execute the operation in milliseconds.
     * @param memoryUsedKb The amount of runtime memory used during the operation in kilobytes.
     * @throws IllegalArgumentException If either executionTimeNs or memoryUsedKb is negative.
     */
    public BenchmarkResult(final long executionTimeMs, final long memoryUsedKb) throws IllegalArgumentException
    {
        if (executionTimeMs < 0)
        {
            throw new IllegalArgumentException("Execution time must not be negative.");
        }
        if (memoryUsedKb < 0)
        {
            throw new IllegalArgumentException("Memory use must not be negative.");
        }

        this.executionTimeMs = executionTimeMs;
        this.memoryUsedKb = memoryUsedKb;
    }
    /**
     * Returns the execution time in milliseconds.
     *
     * @return The execution time in milliseconds.
     */
    public long getExecutionTimeMs()
    {
        return executionTimeMs;
    }
    /**
     * Returns the memory used in kilobytes.
     *
     * @return The memory used in kilobytes.
     */
    public long getMemoryUsedKb()
    {
        return memoryUsedKb;
    }
    /**
     * Turns the BenchmarkResult into a string representation.
     *
     * @return A string representation of the benchmark result.
     */
    @Override
    public String toString()
    {
        return  "{executionTimeMs=" + executionTimeMs +
                ", memoryUsedKb=" + memoryUsedKb +
                "}";
    }   
}
