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

package com.github.rlacher.sortbench.processing;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.rlacher.sortbench.results.BenchmarkResult;

// Unit tests for the MedianAggregator class.
class MedianAggregatorTest
{
    private final MedianAggregator medianAggregator = new MedianAggregator();

    @Test
    void apply_nullResults_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> medianAggregator.apply(null),
                "apply() with null results should throw IllegalArgumentException");
    }

    @Test
    void apply_emptyResults_throwsIllegalArgumentExceptionFromMedian()
    {
        final List<BenchmarkResult> results = List.of();

        assertThrows(IllegalArgumentException.class, () -> medianAggregator.apply(results),
                "apply() with empty results should throw IllegalArgumentException from median()");
    }

    @Test
    void apply_singleResult_returnsThatValue()
    {
        final BenchmarkResult mockResult = mock(BenchmarkResult.class);
        when(mockResult.getValue()).thenReturn(5.0);
        final List<BenchmarkResult> results = List.of(mockResult);

        assertEquals(5.0, medianAggregator.apply(results),
                "apply() with a single result should return that value (mocked result)");
    }

    @Test
    void apply_oddNumberOfResults_returnsMiddleValue()
    {
        final BenchmarkResult mockResult1 = mock(BenchmarkResult.class);
        when(mockResult1.getValue()).thenReturn(3.0);
        final BenchmarkResult mockResult2 = mock(BenchmarkResult.class);
        when(mockResult2.getValue()).thenReturn(1.0);
        final BenchmarkResult mockResult3 = mock(BenchmarkResult.class);
        when(mockResult3.getValue()).thenReturn(5.0);
        final List<BenchmarkResult> results = Arrays.asList(mockResult1, mockResult2, mockResult3);

        assertEquals(3.0, medianAggregator.apply(results),
                "apply() with odd number of results should return the middle value");
    }

    @Test
    void apply_evenNumberOfResults_returnsAverageOfMiddleTwo()
    {
        final BenchmarkResult mockResult1 = mock(BenchmarkResult.class);
        when(mockResult1.getValue()).thenReturn(2.0);
        final BenchmarkResult mockResult2 = mock(BenchmarkResult.class);
        when(mockResult2.getValue()).thenReturn(4.0);
        final BenchmarkResult mockResult3 = mock(BenchmarkResult.class);
        when(mockResult3.getValue()).thenReturn(1.0);
        final BenchmarkResult mockResult4 = mock(BenchmarkResult.class);
        when(mockResult4.getValue()).thenReturn(3.0);
        final List<BenchmarkResult> results = Arrays.asList(mockResult1, mockResult2, mockResult3, mockResult4);

        assertEquals(2.5, medianAggregator.apply(results),
                "apply() with even number of results should return the average of the middle two");
    }

    @Test
    void apply_duplicateValues_returnsCorrectMedian_withMocks()
    {
        final BenchmarkResult mockResult1 = mock(BenchmarkResult.class);
        when(mockResult1.getValue()).thenReturn(2.0);
        final BenchmarkResult mockResult2 = mock(BenchmarkResult.class);
        when(mockResult2.getValue()).thenReturn(2.0);
        final BenchmarkResult mockResult3 = mock(BenchmarkResult.class);
        when(mockResult3.getValue()).thenReturn(5.0);
        final BenchmarkResult mockResult4 = mock(BenchmarkResult.class);
        when(mockResult4.getValue()).thenReturn(1.0);
        final List<BenchmarkResult> results = Arrays.asList(mockResult1, mockResult2, mockResult3, mockResult4);

        assertEquals(2.0, medianAggregator.apply(results),
                "apply() with duplicate values should return the correct median");
    }
}