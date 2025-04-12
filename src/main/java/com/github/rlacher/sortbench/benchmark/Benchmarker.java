/*
 * Copyright (c) 2025 Rene Lacher
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.rlacher.sortbench.benchmark;

import java.time.Duration;
import java.time.Instant;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.logging.Logger;

/**
 * Benchmarker class for profiling sorting algorithms.
 * 
 * TODO: The current design of the Benchmarker needs refactoring as it is becoming too monolithic and difficult to maintain. See issue #2.
 */
public class Benchmarker
{
    /**
     * Enum representing the profiling modes.
     * 
     * NONE: No profiling.
     * MEMORY_USAGE: Memory usage profiling.
     * SWAP_COUNT: Swap count profiling.
     * EXECUTION_TIME: Execution time profiling.
     */
    public enum ProfilingMode
    {
        NONE,
        MEMORY_USAGE,
        SWAP_COUNT,
        EXECUTION_TIME
    }

    /// Logger for logging messages.
    private static final Logger logger = Logger.getLogger(Benchmarker.class.getName());

    /// Flag indicating profiling status.
    private boolean isProfiling = false;

    /// Start time for execution time profiling.
    private Instant startTime;

    /// End time for execution time profiling.
    private Instant endTime;

    /// Initial memory usage in kilobytes for memory profiling.
    private long initialMemoryKb;

    /**
     * Maximum memory usage in kilobytes for memory profiling.
     * This value is updated during the profiling process.
     */
    private long maxMemoryKb;

    /**
     * Swap count for swap count profiling.
     * This value is updated during the sorting process.
     */
    private long swapCount;

    /**
     * MemoryMXBean instance for memory profiling.
     * 
     * This bean provides information about the memory system of the Java virtual machine.
     * The scope of this benchmark is limited to heap memory analysis. Stack memory usage is not measured for simplicity.
     */
    private MemoryMXBean memoryBean;

    /**
     * Profiling mode to be used for benchmarking.
     */
    private final ProfilingMode profilingMode;

    /**
     * Constructor for the Benchmarker class parameterised with a profiling mode.
     * 
     * @param profilingMode The profiling mode to be used.
     * @throws IllegalArgumentException If the profiling mode is null.
     */
    public Benchmarker(final ProfilingMode profilingMode)
    {
        if(profilingMode == null)
        {
            throw new IllegalArgumentException("Profiling mode must not be null.");
        }

        logger.fine("Profiling mode set to " + profilingMode);
        this.profilingMode = profilingMode;

        memoryBean = ManagementFactory.getMemoryMXBean();
    }

    /**
     * Starts the profiling process based on the selected profiling mode.
     * 
     * @throws IllegalStateException If profiling is already in progress.
     */
    public void startProfiling()
    {
        if(isProfiling)
        {
            throw new IllegalStateException("Profiling is already in progress.");
        }

        isProfiling = true;
        logger.finest("Start profiling...");

        if (profilingMode == ProfilingMode.EXECUTION_TIME)
        {
            startTime = Instant.now();
            endTime = null;
        }
        else if (profilingMode == ProfilingMode.MEMORY_USAGE)
        {
            initialMemoryKb = memoryBean.getHeapMemoryUsage().getUsed() / 1024;
            maxMemoryKb = initialMemoryKb;
        }
    }

    /**
     * Stops the profiling process based on the selected profiling mode.
     * 
     * @throws IllegalStateException If profiling is not in progress.
     */
    public void stopProfiling()
    {
        if(!isProfiling)
        {
            throw new IllegalStateException("Profiling is not in progress.");
        }

        if (profilingMode == ProfilingMode.EXECUTION_TIME)
        {
            endTime = Instant.now();
        }
        else if (profilingMode == ProfilingMode.MEMORY_USAGE)
        {
            final long finalMemoryKb = memoryBean.getHeapMemoryUsage().getUsed() / 1024;
            maxMemoryKb = Math.max(maxMemoryKb, finalMemoryKb);
        }

        isProfiling = false;
        logger.finest("Stop profiling...");
    }

    /**
     * Measures the memory usage during the sorting process.
     * 
     * The scope of this benchmark is limited to heap memory analysis. Stack memory usage is not measured for simplicity.
     */
    public void measureMemory()
    {
        if (profilingMode == ProfilingMode.MEMORY_USAGE)
        {
            final long currentMemoryKb = memoryBean.getHeapMemoryUsage().getUsed() / 1024;;
            maxMemoryKb = Math.max(maxMemoryKb, currentMemoryKb);
        }
    }


    /**
     * Increments the swap count during the sorting process.
     * 
     * A swap refers to the exchange of two elements in the array being sorted (also known as invertion).
     */
    public void incrementSwaps()
    {
        if (profilingMode == ProfilingMode.SWAP_COUNT)
        {
            ++swapCount;
        }
    }

    /**
     * Resets the benchmarker to its initial state.
     */
    public void reset()
    {
        startTime = null;
        endTime = null;
        initialMemoryKb = 0;
        maxMemoryKb = 0;
        swapCount = 0;
    }

    /**
     * Returns the benchmark result based on the profiling mode.
     * 
     * @return The benchmark result containing the profiling mode and the corresponding metric value.
     * @throws IllegalStateException If the profiling is still in progress or the profiling mode is not set (should not happen).
     */
    public BenchmarkResult getResult()
    {
        if(isProfiling)
        {
            throw new IllegalStateException("Cannot get result while profiling is still in progress.");
        }

        switch(profilingMode)
        {
            case NONE:
                return new BenchmarkResult(profilingMode, 0);
            case MEMORY_USAGE:
                return new BenchmarkResult(profilingMode, maxMemoryKb - initialMemoryKb);
            case SWAP_COUNT:
                return new BenchmarkResult(profilingMode, swapCount);
            case EXECUTION_TIME:
                return new BenchmarkResult(profilingMode, (startTime == null) ? 0 : Duration.between(startTime, endTime).toNanos() / 1e6);
            default:
                throw new IllegalStateException("Unexpected profiling mode: " + profilingMode);
        }
    }
}
