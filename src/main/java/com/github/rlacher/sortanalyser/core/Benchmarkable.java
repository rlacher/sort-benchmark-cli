package com.github.rlacher.sortanalyser.core;

import com.github.rlacher.sortanalyser.core.BenchmarkResult;

/**
 * Interface for classes that can be benchmarked.  
 */
public interface Benchmarkable
{
    /**
     * Executes and benchmarks an operation on the provided array.
     * 
     * @param array. The array to be operated on and benchmarked.
     * @return The result of the benchmarked operation in a {@link BenchmarkResult} object.
     * @throws NullPointerException If the 'array' is null.
     */
    BenchmarkResult benchmarkedOperation(int[] array) throws NullPointerException;
}