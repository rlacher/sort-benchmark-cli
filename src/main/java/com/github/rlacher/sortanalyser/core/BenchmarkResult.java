package com.github.rlacher.sortanalyser.core;

/**
 * Stores the result of a benchmark operation.
 */
public class BenchmarkResult
{
    // The time taken to execute the operation in nanoseconds.
    private final long executionTimeNs;

    // The amount of runtime memory used during the operation in kilobytes.
    private final long memoryUsedKb;

    /**
     * Constructs a new BenchmarkResult object.
     *
     * @param executionTimeNs The time taken to execute the operation in nanoseconds.
     * @param memoryUsedKb The amount of runtime memory used during the operation in kilobytes.
     */
    public BenchmarkResult(final long executionTimeNs, final long memoryUsedKb)
    {
        if (executionTimeNs < 0)
        {
            throw new IllegalArgumentException("Execution time must not be negative.");
        }
        if (memoryUsedKb < 0)
        {
            throw new IllegalArgumentException("Memory use must not be negative.");
        }

        this.executionTimeNs = executionTimeNs;
        this.memoryUsedKb = memoryUsedKb;
    }
    /**
     * Returns the execution time in nanoseconds.
     *
     * @return The execution time in nanoseconds.
     */
    public long getExecutionTimeNs()
    {
        return executionTimeNs;
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
        return "BenchmarkResult{" +
                "executionTimeNs=" + executionTimeNs +
                ", memoryUsedKb=" + memoryUsedKb +
                '}';
    }   
}
