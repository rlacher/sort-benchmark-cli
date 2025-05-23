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

package com.github.rlacher.sortbench.strategies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.github.rlacher.sortbench.benchmark.Benchmarker;
import com.github.rlacher.sortbench.strategies.implementations.*;

// Tests that ensure the profiling methods are called correctly in the SortStrategy implementations.
public class SortStrategyProfilingTest
{
    // Mocked benchmarker instance for verification of profiling method calls.
    private static Benchmarker mockBenchmarker = mock(Benchmarker.class);

    private static int startProfilingCallCount = 0;
    private static int stopProfilingCallCount = 0;

    // Factory method to create instances of SortStrategy implementations for parameterised testing.
    static Stream<SortStrategy> createSortStrategies()
    {
        return Stream.of
        (
            new BubbleSortStrategy(mockBenchmarker),
            new HeapSortStrategy(mockBenchmarker),
            new InsertionSortStrategy(mockBenchmarker),
            new MergeSortStrategy(mockBenchmarker),
            new QuickSortStrategy(mockBenchmarker)
        );
    }

    // Reset the profiling call counters before each test.
    @BeforeEach
    void resetProfilingCallCounter()
    {
        doAnswer(invocation ->
        {
            ++startProfilingCallCount;
            return null;
        }).when(mockBenchmarker).startProfiling();

        doAnswer(invocation ->
        {
            ++stopProfilingCallCount;
            return null;
        }).when(mockBenchmarker).stopProfiling();

        startProfilingCallCount = 0;
        stopProfilingCallCount = 0;
    }

    @ParameterizedTest 
    @MethodSource("createSortStrategies")
    void sort_validArray_callsStartStopProfilingOnceEach(SortStrategy strategy)
    {
        int[] array = { 5, 3, 8, 1, 2 };
        strategy.sort(array);

        assertEquals(1, startProfilingCallCount, "startProfiling() should be called once");
        assertEquals(1, stopProfilingCallCount, "stopProfiling() should be called once");
    }
}
