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

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Benchmarker} class.
 */
class BenchmarkerTest
{
    /**
     * Tests the constructor of the {@link Benchmarker} class when valid parameters are passed.
     */
    @Test
    void constructor_givenValidParams_shouldNotThrow()
    {
        assertDoesNotThrow(() -> new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME), "Constructor should not throw with valid parameters");
    }

    /**
     * Tests the constructor of the {@link Benchmarker} class when null parameter is passed.
     */
    @Test
    void constructor_givenNullArgument_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new Benchmarker(null), "Constructor should throw IllegalArgumentException when mode is null");
    }

    /**
     * Tests incrementation of swaps in the {@link Benchmarker} class.
     */
    @Test 
    void incrementSwaps_whenCalled_incrementsSwaps()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.SWAP_COUNT);
        benchmarker.incrementSwaps();

        BenchmarkResult result = benchmarker.getResult();

        assertNotNull(result, "Result should not be null");
        assertEquals(Benchmarker.ProfilingMode.SWAP_COUNT, result.getProfilingMode(), "Profiling mode should be " + Benchmarker.ProfilingMode.SWAP_COUNT.toString());
        assertEquals(1, Double.valueOf(result.getValue()).intValue(), "Swaps should be incremented");
    }

    /**
     * Tests that the {@link Benchmarker} class does not increment swaps when a different profiling mode is used.
     */
    @Test
    void incrementSwaps_givenDifferentProfilingMode_shouldNotIncrementSwaps()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME);
        benchmarker.incrementSwaps();

        BenchmarkResult result = benchmarker.getResult();

        assertNotNull(result, "Result should not be null");
        assertEquals(Benchmarker.ProfilingMode.EXECUTION_TIME, result.getProfilingMode(), "Profiling mode should be " + Benchmarker.ProfilingMode.EXECUTION_TIME.toString());
        assertEquals(0, Double.valueOf(result.getValue()).intValue(), "Swaps should not be incremented");
    }

    /**
     * Tests the reset method of the {@link Benchmarker} class when called with the profiling mode set to {@link Benchmarker.ProfilingMode#SWAP_COUNT}.
     */
    @Test
    void reset_whenProfilingSwapCount_resetsSwapCount()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.SWAP_COUNT);
        benchmarker.incrementSwaps();
        benchmarker.reset();

        BenchmarkResult result = benchmarker.getResult();

        assertNotNull(result, "Result should not be null");
        assertEquals(Benchmarker.ProfilingMode.SWAP_COUNT, result.getProfilingMode(), "Profiling mode should be " + Benchmarker.ProfilingMode.SWAP_COUNT.toString());
        assertEquals(0, Double.valueOf(result.getValue()).intValue(), "Swaps should be reset to 0");
    }

    /**
     * Tests execution time profiling.
     * 
     * This test calls {@link com.github.rlacher.sortbench.benchmark.Benchmarker#startProfiling()} and
     * {@link com.github.rlacher.sortbench.benchmark.Benchmarker#stopProfiling()} to profile runtime.
     * 
     * The test simulates some processing time by sleeping for a specified duration.
     */
    @Test
    void startStopProfiling_whenCalledInExecutionTimeMode_profilesRuntime()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME);
        benchmarker.startProfiling();
        
        // Simulate some processing time
        final int sleepTimeMs = 100;
        try
        {
            Thread.sleep(sleepTimeMs);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }

        benchmarker.stopProfiling();

        BenchmarkResult result = benchmarker.getResult();

        assertNotNull(result, "Result should not be null");
        assertEquals(Benchmarker.ProfilingMode.EXECUTION_TIME, result.getProfilingMode(), "Profiling mode should be " + Benchmarker.ProfilingMode.EXECUTION_TIME.toString());
        assertTrue(result.getValue() >= sleepTimeMs, "Execution time should be greater than or equal to sleep time");
    }

    /**
     * Tests memory usage profiling.
     * 
     * This test calls {@link com.github.rlacher.sortbench.benchmark.Benchmarker#startProfiling()} and
     * {@link com.github.rlacher.sortbench.benchmark.Benchmarker#stopProfiling()} to profile memory usage.
     */
    @Test
    void startStopProfiling_whenCalledInMemoryMode_profilesMemoryConsumption()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.MEMORY);
        benchmarker.startProfiling();

        // Allocate 50MB of memory
        byte[] heapMemoryBlock = new byte[50 * 1024 * 1024];

        benchmarker.stopProfiling();

        BenchmarkResult result = benchmarker.getResult();

        assertNotNull(result, "Result should not be null");
        assertEquals(Benchmarker.ProfilingMode.MEMORY, result.getProfilingMode(), "Profiling mode should be " + Benchmarker.ProfilingMode.MEMORY.toString());
        assertEquals(50*1024, result.getValue(), 10*1024, "Memory consumption should be approximately 50MB");
    }

    /**
     * Tests the {@link Benchmarker} class when profiling mode is set to {@link Benchmarker.ProfilingMode#NONE}.
     */
    @Test
    void getResult_whenProfilingModeNone_returnsZero()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.NONE);
        benchmarker.startProfiling();
        benchmarker.stopProfiling();

        BenchmarkResult result = benchmarker.getResult();

        assertNotNull(result, "Result should not be null");
        assertEquals(Benchmarker.ProfilingMode.NONE, result.getProfilingMode(), "Profiling mode should be " + Benchmarker.ProfilingMode.NONE.toString());
        assertEquals(0, Double.valueOf(result.getValue()).intValue(), "Result value should be 0");
    }

    /**
     * Tests the {@link Benchmarker} class when memory is freed up before stopping profiling.
     */
    @Test
    void measureMemory_whenMemoryIsFreedUpBeforeStopProfiling_returnsCorrectMemoryUsage()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.MEMORY);
        
        benchmarker.startProfiling();

        // Allocate first 30MB of memory
        byte[] heapMemoryBlock1 = new byte[30 * 1024 * 1024];

        {
            // Allocate another 20MB of memory
            byte[] heapMemoryBlock2 = new byte[20 * 1024 * 1024];
            benchmarker.measureMemory();
        }

        // Force garbage collection
        System.gc();

        benchmarker.stopProfiling();

        BenchmarkResult result = benchmarker.getResult();

        assertNotNull(result, "Result should not be null");
        assertEquals(Benchmarker.ProfilingMode.MEMORY, result.getProfilingMode(), "Profiling mode should be " + Benchmarker.ProfilingMode.MEMORY.toString());
        assertEquals(50 * 1024, result.getValue(), 10*1024, "Memory usage should be approximately 50MB");
    }
}
