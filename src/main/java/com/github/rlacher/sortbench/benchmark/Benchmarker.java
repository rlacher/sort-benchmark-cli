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

import com.github.rlacher.sortbench.results.BenchmarkMetric;

/**
 * Benchmarker class for profiling sorting algorithms.
 */
public class Benchmarker
{
    /** Enum representing profiling modes for benchmarking. */
    public enum ProfilingMode
    {
        /** No profiling (used for debugging, testing). */
        NONE,
        /** Tracks memory consumption. */
        MEMORY_USAGE,
        /** Tracks data modifications (e.g. swaps, shifts, inserts). */
        DATA_WRITE_COUNT,
        /** Measures runtime. */
        EXECUTION_TIME
    }

    /** Logger for logging messages. */
    private static final Logger logger = Logger.getLogger(Benchmarker.class.getName());

    /** Flag indicating profiling status. */
    private boolean isProfiling = false;

    /** Start time for execution time profiling. */
    private Instant startTime;

    /** End time for execution time profiling. */
    private Instant endTime;

    /** Initial memory usage in kilobytes for memory profiling. */
    private long initialMemoryKb;

    /**
     * Maximum memory usage in kilobytes for memory profiling.
     *
     * <p>This value is updated during the profiling process.</p>
     */
    private long maxMemoryKb;

    /**
     * Data write count for data operation profiling.
     *
     * <p>This value is updated during the sorting process.</p>
     */
    private long dataWriteCount;

    /**
     * MemoryMXBean instance for memory profiling.
     *
     * <p>This bean provides information about the memory system of the Java virtual machine. The scope of this
     * benchmark is limited to heap memory analysis. Stack memory usage is not measured for simplicity.</p>
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
     * @throws IllegalArgumentException If the profiling mode is {@code null}.
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
     * Initiates profiling, resetting any prior metric data, based on the selected profiling mode.
     *
     * <p>Must be followed by {@link Benchmarker#stopProfiling} to complete metric collection.</p>
     *
     * @throws IllegalStateException If profiling is already active.
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
        else if (profilingMode == ProfilingMode.DATA_WRITE_COUNT)
        {
            dataWriteCount = 0;
        }
    }

    /**
     * Stops the profiling process based on the selected profiling mode.
     *
     * <p>Must follow a {@link Benchmarker#startProfiling} call to collect metrics.</p>
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
     * <p>This method should only be called when memory profiling is active. The scope of this
     * benchmark is limited to heap memory analysis. Stack memory usage is not measured for simplicity.</p>
     *
     * @throws IllegalStateException If profiling is not currently active.
     */
    public void measureMemory()
    {
        if(!isProfiling)
        {
            throw new IllegalStateException("Cannot measure memory usage when profiling is inactive.");
        }

        if (profilingMode == ProfilingMode.MEMORY_USAGE)
        {
            final long currentMemoryKb = memoryBean.getHeapMemoryUsage().getUsed() / 1024;;
            maxMemoryKb = Math.max(maxMemoryKb, currentMemoryKb);
        }
    }

    /**
     * Reports a swap operation during the sorting process.
     *
     * <p>A swap refers to the exchange of two elements in the array being sorted (also known as invertion).
     * This method increases the data write counter by 2.</p>
     */
    public void reportSwap()
    {
        reportWrites(2);
    }

    /**
     * Reports a shift operation during the sorting process.
     *
     * <p>A shift refers to movement of an element to the right within the array.
     * This method increments the data write counter by 1.</p>
     */
    public void reportShift()
    {
        reportWrites(1);
    }

    /**
     * Reports an insert operation during the sorting process.
     *
     * <p>An insert writes a specified value at a specified location within the array.
     * This method increments the data write counter by 1.</p>
     */
    public void reportInsert()
    {
        reportWrites(1);
    }

    /**
     * Reports a write operation during the sorting process.
     *
     * <p>This method increments the data write counter by 1.</p>
     */
    public void reportWrite()
    {
        reportWrites(1);
    }

    /**
     * Reports write operations during the sorting process.
     *
     * <p>This method increments the data write counter by the specified amount
     * only when profiling is active and the profiling mode is set to
     * {@code DATA_WRITE_COUNT}.</p>
     *
     * @param count The number of write operations performed.
     * @throws IllegalArgumentException If count is negative.
     * @throws IllegalStateException If profiling is not currently active.
     */
    public void reportWrites(final int count)
    {
        if(count < 0)
        {
            throw new IllegalArgumentException("Write count must not be negative.");
        }
        if(!isProfiling)
        {
            throw new IllegalStateException("Cannot report writes when profiling is inactive.");
        }

        if (profilingMode == ProfilingMode.DATA_WRITE_COUNT)
        {
            dataWriteCount+=count;
        }
    }

    /**
     * Resets the benchmarker to its initial state.
     *
     * @throws IllegalStateException if profiling is currently active.
     */
    public void reset()
    {
        if(isProfiling)
        {
            throw new IllegalStateException("Cannot reset while profiling is active.");
        }

        startTime = null;
        endTime = null;
        initialMemoryKb = 0;
        maxMemoryKb = 0;
        dataWriteCount = 0;
    }

    /**
     * Returns the benchmark metric based on the profiling mode.
     *
     * @return The benchmark iteration metric containing the profiling mode and the corresponding metric value.
     * @throws IllegalStateException If the profiling is still in progress or the profiling mode is not set (should not happen).
     */
    public BenchmarkMetric getMetric()
    {
        if(isProfiling)
        {
            throw new IllegalStateException("Cannot get metric while profiling is still in progress.");
        }

        switch(profilingMode)
        {
            case NONE:
                return new BenchmarkMetric(profilingMode, 0);
            case MEMORY_USAGE:
                return new BenchmarkMetric(profilingMode, maxMemoryKb - initialMemoryKb);
            case DATA_WRITE_COUNT:
                return new BenchmarkMetric(profilingMode, dataWriteCount);
            case EXECUTION_TIME:
                return new BenchmarkMetric(profilingMode, (startTime == null) ? 0 : Duration.between(startTime, endTime).toNanos() / 1e6);
            default:
                throw new IllegalStateException("Unexpected profiling mode: " + profilingMode);
        }
    }
}
