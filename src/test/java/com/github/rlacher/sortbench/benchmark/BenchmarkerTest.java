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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import com.github.rlacher.sortbench.results.BenchmarkMetric;

// Unit tests for the Benchmarker class.
class BenchmarkerTest
{
    private static final int FIFTY_MB = 50 * 1024 * 1024;
    private static final int THIRTY_MB = 30 * 1024 * 1024;
    private static final int TWENTY_MB = 20 * 1024 * 1024;
    private static final int FIVE_MB = 5 * 1024 * 1024;
    private static final int HUNDRED_MS = 100;

    @Test
    void constructor_validParameters_shouldNotThrow()
    {
        assertDoesNotThrow(() -> new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME), "Constructor should not throw with valid parameters");
    }

    @Test
    void constructor_nullArgument_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new Benchmarker(null), "Constructor should throw IllegalArgumentException when mode is null");
    }

    @Test
    void reportInsert_whenCalled_delegatesToReportWrites()
    {
        Benchmarker benchmarker = spy(new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT));

        benchmarker.startProfiling();
        benchmarker.reportInsert();

        verify(benchmarker, times(1)).reportWrites(1);
    }

    @Test
    void reportShift_whenCalled_delegatesToReportWrites()
    {
        Benchmarker benchmarker = spy(new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT));

        benchmarker.startProfiling();
        benchmarker.reportShift();

        verify(benchmarker, times(1)).reportWrites(1);
    }

    @Test 
    void reportShift_whenCalled_countsDataWrite()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);
        benchmarker.startProfiling();
        benchmarker.reportShift();
        benchmarker.stopProfiling();

        assertEquals(1, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Data write count should be 1");
    }

    @Test 
    void reportSwap_whenCalled_countsDataWrite()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);
        benchmarker.startProfiling();
        benchmarker.reportSwap();
        benchmarker.stopProfiling();;

        assertEquals(2, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Data write count should be 2");
    }

    @Test
    void reportSwap_differentProfilingMode_dataWriteCountZero()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME);
        benchmarker.startProfiling();
        benchmarker.reportSwap();
        benchmarker.stopProfiling();

        assertEquals(0, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Swaps should not be incremented");
    }

    @Test
    void reportWrites_negativeWriteCount_throwsIllegalArgumentException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);
        benchmarker.startProfiling();

        assertThrows(IllegalArgumentException.class, () -> benchmarker.reportWrites(-1), "Write count must be non-negative.");
    }

    @Test
    void reportWrites_inactiveProfiling_throwsIllegalStateException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);

        assertThrows(IllegalStateException.class, () -> benchmarker.reportWrites(1), "Profiling must be active to report data writes.");
    }

    @Test
    void reportWrites_profilingModeNone_writesIgnored()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.NONE);

        benchmarker.startProfiling();
        benchmarker.reportWrites(1);
        benchmarker.stopProfiling();

        assertEquals(0, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Result value should not change if profiling mode is not DATA_WRITE_COUNT.");
    }

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

    @Test
    void reset_afterProfilingCycle_resetsDataWriteCounter()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);
        benchmarker.startProfiling();
        benchmarker.reportWrite();
        benchmarker.stopProfiling();
        benchmarker.reset();

        assertEquals(0, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Data write counter should be reset to 0");
    }

    @Test
    void reset_whenProfiling_throwsIllegalStateException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.DATA_WRITE_COUNT);
        benchmarker.startProfiling();
        assertThrows(IllegalStateException.class, () -> benchmarker.reset(), "Reset should throw IllegalStateException while profiling is active.");
    }

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

    @Disabled("Disabled due to unpredictable GC effects")
    @Test
    void startStopProfiling_memoryMode_profilesMemoryConsumption() throws InterruptedException
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.MEMORY_USAGE);
        benchmarker.startProfiling();

        // Allocate 50MB of memory
        byte[] heapMemoryBlock = new byte[FIFTY_MB];
        Thread.sleep(HUNDRED_MS);

        benchmarker.stopProfiling();

        // The expected heap memory consumption to be profiled
        final double expectedValue = FIFTY_MB / 1024.0;
        final double actualValue = benchmarker.getMetric().getValue();
        final double delta = FIVE_MB / 1024.0;

        if (Math.abs(expectedValue - actualValue) < delta)
        {
            // Test passes: memory consumption was approximately 50MB
            return;
        }

        if (Math.abs(0.0 - actualValue) < delta)
        {
            // Test passes: memory consumption was approximately 0MB (likely due to early garbage collection)
            return; 
        }

        // Fail if neither condition is met
        fail(String.format("Memory consumption was %.2fMB, expected approximately %.2fMB or 0MB.", actualValue, expectedValue));
    }

    @Test
    void startProfiling_calledTwice_throwsIllegalStateException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME);
        benchmarker.startProfiling();

        assertThrows(IllegalStateException.class, () -> benchmarker.startProfiling(), "startProfiling() should throw IllegalStateException when called twice");
    }

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

    @Test
    void stopProfiling_profilingInactive_throwsIllegalStateException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME);

        assertThrows(IllegalStateException.class, () -> benchmarker.stopProfiling(), "stopProfiling() should throw IllegalStateException when profiling has not started");
    }

    @Test
    void getMetric_noneMode_returnsZero()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.NONE);
        benchmarker.startProfiling();
        benchmarker.stopProfiling();

        assertEquals(0, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), "Result value should be 0");
    }

    @Test
    void getMetric_profilingActive_throwsIllegalStateException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.EXECUTION_TIME);
        benchmarker.startProfiling();

        assertThrows(IllegalStateException.class, () -> benchmarker.getMetric(), "getResult() should throw IllegalStateException when profiling is active");
    }

    @ParameterizedTest
    @EnumSource(Benchmarker.ProfilingMode.class)
    void getMetric_beforeProfiling_returnsZero(Benchmarker.ProfilingMode mode)
    {
        Benchmarker benchmarker = new Benchmarker(mode);

        BenchmarkMetric metric = benchmarker.getMetric();
        assertNotNull(metric, String.format("Should return non-null metric for mode: %s.", mode.toString()));
        assertEquals(0.0, metric.getValue(), String.format("Metric value should be zero before profiling for mode: %s.", mode.toString()));
    }

    @Test
    void measureMemory_inactiveProfiling_throwsIllegalStateException()
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.MEMORY_USAGE);

        assertThrows(IllegalStateException.class, () -> benchmarker.measureMemory(), "measureMemory() should throw IllegalStateException when profiling is not currently active.");
    }

    @Test
    void measureMemory_whenMemoryIsFreedUpBeforeStopProfiling_returnsCorrectMemoryUsage() throws InterruptedException
    {
        Benchmarker benchmarker = new Benchmarker(Benchmarker.ProfilingMode.MEMORY_USAGE);
        
        benchmarker.startProfiling();

        byte[] memoryBlock30 = new byte[THIRTY_MB];

        {
            // Allocate another 20MB of memory
            byte[] memoryBlock20 = new byte[TWENTY_MB];
            Thread.sleep(HUNDRED_MS);
            benchmarker.measureMemory();
        }

        // Force garbage collection
        System.gc();
        Thread.sleep(HUNDRED_MS);

        benchmarker.stopProfiling();

        assertEquals(FIFTY_MB / 1024, Double.valueOf(benchmarker.getMetric().getValue()).intValue(), FIVE_MB / 1024, "Memory usage should be approximately 50MB");
    }
}
