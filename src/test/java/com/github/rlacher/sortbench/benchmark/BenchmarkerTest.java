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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

// Unit tests for the Benchmarker class.
class BenchmarkerTest
{
    private static final int FIFTY_MB = 50 * 1024 * 1024;
    private static final int THIRTY_MB = 30 * 1024 * 1024;
    private static final int TWENTY_MB = 20 * 1024 * 1024;
    private static final int TEN_MB = 10 * 1024 * 1024;
    private static final int HUNDRED_MS = 100;

    // Tests the constructor of the Benchmarker class when valid parameters are passed.
    @Test
    void constructor_validParameters_shouldNotThrow()
    {
        assertDoesNotThrow(() -> new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME), "Constructor should not throw with valid parameters");
    }

    // Tests the constructor of the Benchmarker class when null parameter is passed.
    @Test
    void constructor_nullArgument_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new Benchmarker(null), "Constructor should throw IllegalArgumentException when mode is null");
    }

    // Test if reportInsert() delegates to reportWrites()
    @Test
    void reportInsert_whenCalled_delegatesToReportWrites()
    {
        Benchmarker benchmarker = spy(new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT));

        benchmarker.startProfiling();
        benchmarker.reportInsert();

        verify(benchmarker, times(1)).reportWrites(1);
    }

    // Test if reportShift() delegates to reportWrites()
    @Test
    void reportShift_whenCalled_delegatesToReportWrites()
    {
        Benchmarker benchmarker = spy(new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT));

        benchmarker.startProfiling();
        benchmarker.reportShift();

        verify(benchmarker, times(1)).reportWrites(1);
    }

    // Test if reportShift() increases data write counter correctly in the Benchmarker class.
    @Test 
    void reportShift_whenCalled_countsDataWrite()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);
        benchmarker.startProfiling();
        benchmarker.reportShift();
        benchmarker.stopProfiling();

        assertEquals(1, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Data write count should be 1");
    }

    // Test if reportSwap() increases data write counter correctly in the Benchmarker class.
    @Test 
    void reportSwap_whenCalled_countsDataWrite()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);
        benchmarker.startProfiling();
        benchmarker.reportSwap();
        benchmarker.stopProfiling();;

        assertEquals(2, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Data write count should be 2");
    }

    // Tests that the Benchmarker class does not increment swaps when a different profiling mode is used.
    @Test
    void reportSwap_differentProfilingMode_dataWriteCountZero()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME);
        benchmarker.startProfiling();
        benchmarker.reportSwap();
        benchmarker.stopProfiling();

        assertEquals(0, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Swaps should not be incremented");
    }

    // Tests the negative write count check in reportWrites()
    @Test
    void reportWrites_negativeWriteCount_throwsIllegalArgumentException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);
        benchmarker.startProfiling();

        assertThrows(IllegalArgumentException.class, () -> benchmarker.reportWrites(-1), "Write count must be non-negative.");
    }

    // Tests if reportWrites() throws an IllegalStateException in case it is not currently profiling.
    @Test
    void reportWrites_inactiveProfiling_throwsIllegalStateException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);

        assertThrows(IllegalStateException.class, () -> benchmarker.reportWrites(1), "Profiling must be active to report data writes.");
    }

    // Test that data write count does not change if profiling mode is different from DATA_WRITE_COUNT.
    @Test
    void reportWrites_profilingModeNone_writesIgnored()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.NONE);

        benchmarker.startProfiling();
        benchmarker.reportWrites(1);
        benchmarker.stopProfiling();

        assertEquals(0, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Result value should not change if profiling mode is not DATA_WRITE_COUNT.");
    }

    // Test if data write counter is accumulative over multiple reportWrites() calls.
    @Test
    void reportWrites_multipleCalls_resultIsSumOfCounts()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);

        benchmarker.startProfiling();
        benchmarker.reportWrites(1);
        benchmarker.reportWrites(2);
        benchmarker.stopProfiling();

        assertEquals(3, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Result value should equal the sum of reported write counts.");
    }

    // Tests the reset method of the Benchmarker class when called with the profiling mode set to {@link Benchmarker.ProfilingMode#SWAP_COUNT}.
    @Test
    void reset_whenProfilingDataWrites_resetsDataWriteCounter()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);
        benchmarker.startProfiling();
        benchmarker.reportWrite();
        benchmarker.stopProfiling();
        benchmarker.reset();

        assertEquals(0, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Data write counter should be reset to 0");
    }

    // Tests if reset() throws an IllegalStateException while profiling is active.
    @Test
    void reset_whenProfiling_throwsIllegalStateException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);
        benchmarker.startProfiling();
        assertThrows(IllegalStateException.class, () -> benchmarker.reset(), "Reset should throw IllegalStateException while profiling is active.");
    }

    /*
     * Tests execution time profiling.
     * 
     * This test calls startProfiling() and stopProfiling()} to profile runtime.
     * The test simulates some processing time by sleeping for a specified duration.
     */
    @Test
    void startStopProfiling_executionTimeMode_profilesRuntime() throws InterruptedException
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME);
        benchmarker.startProfiling();
        
        // Simulate some processing time
        Thread.sleep(HUNDRED_MS);

        benchmarker.stopProfiling();

        assertTrue(Double.valueOf(benchmarker.getMetric().getValue()).intValue() >= HUNDRED_MS, "Execution time should be greater than or equal to sleep time");
    }

    /*
     * Tests memory usage profiling.
     * 
     * This test calls startProfiling() and stopProfiling() to profile memory usage.
     */
    @Test
    void startStopProfiling_memoryMode_profilesMemoryConsumption()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.MEMORY_USAGE);
        benchmarker.startProfiling();

        // Allocate 50MB of memory
        byte[] heapMemoryBlock = new byte[FIFTY_MB];

        benchmarker.stopProfiling();

        assertEquals(FIFTY_MB / 1024, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), TEN_MB / 1024, "Memory consumption should be approximately 50MB");
    }

    // Tests the startProfiling() method when called twice.
    @Test
    void startProfiling_calledTwice_throwsIllegalStateException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME);
        benchmarker.startProfiling();

        assertThrows(IllegalStateException.class, () -> benchmarker.startProfiling(), "startProfiling() should throw IllegalStateException when called twice");
    }

    // Tests that startProfiling() resets the data write count after multiple profiling cycles.
    @Test
    void startProfiling_multipleCycles_resetsDataWriteCount()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);

        // First cycle: Increment swaps, then stop
        benchmarker.startProfiling();
        benchmarker.reportWrite();
        benchmarker.stopProfiling();

        // Second cycle: Start and stop, no increment
        benchmarker.startProfiling();
        benchmarker.stopProfiling();

        assertEquals(0, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Data write count should reset to 0 in the second cycle.");
    }

    // Tests the stopProfiling() method when called without starting profiling.
    @Test
    void stopProfiling_profilingInactive_throwsIllegalStateException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME);

        assertThrows(IllegalStateException.class, () -> benchmarker.stopProfiling(), "stopProfiling() should throw IllegalStateException when profiling has not started");
    }

    // Tests the Benchmarker class when profiling mode is set to NONE.
    @Test
    void getResult_noneMode_returnsZero()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.NONE);
        benchmarker.startProfiling();
        benchmarker.stopProfiling();

        assertEquals(0, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Result value should be 0");
    }

    // Tests the getResult() method when profiling is still active.
    @Test
    void getResult_profilingActive_throwsIllegalStateException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME);
        benchmarker.startProfiling();

        assertThrows(IllegalStateException.class, () -> benchmarker.getMetric(), "getResult() should throw IllegalStateException when profiling is active");
    }

    // Tests measureMemory() throws an IllegalStateException if not profiling.
    @Test
    void measureMemory_inactiveProfiling_throwsIllegalStateException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.MEMORY_USAGE);

        assertThrows(IllegalStateException.class, () -> benchmarker.measureMemory(), "measureMemory() should throw IllegalStateException when profiling is not currently active.");
    }

    // Tests the Benchmarker class when memory is freed up before stopping profiling.
    @Test
    void measureMemory_whenMemoryIsFreedUpBeforeStopProfiling_returnsCorrectMemoryUsage()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.MEMORY_USAGE);
        
        benchmarker.startProfiling();

        byte[] memoryBlock30 = new byte[THIRTY_MB];

        {
            // Allocate another 20MB of memory
            byte[] memoryBlock20 = new byte[TWENTY_MB];
            benchmarker.measureMemory();
        }

        // Force garbage collection
        System.gc();

        benchmarker.stopProfiling();

        assertEquals(FIFTY_MB / 1024, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), TEN_MB / 1024, "Memory usage should be approximately 50MB");
    }
}
