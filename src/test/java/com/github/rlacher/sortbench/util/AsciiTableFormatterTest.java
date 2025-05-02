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

package com.github.rlacher.sortbench.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.rlacher.sortbench.results.AggregatedResult;
import com.github.rlacher.sortbench.results.BenchmarkContext;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData.DataType;

// Unit tests for the AsciiTableFormatter class.
class AsciiTableFormatterUnitTest
{
    private AsciiTableFormatter formatter;

    @Mock
    private AggregatedResult mockResult;

    @Mock
    private BenchmarkContext mockContext;

    private AutoCloseable openMocks;

    @BeforeEach
    void setUp()
    {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception
    {
        openMocks.close();
    }

    @Test
    void constructor_nullLocale_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new AsciiTableFormatter(null, '-', '|'),
            "Constructor with null locale should throw IllegalArgumentException.");
    }

    @Test
    void constructor_invalidColumnDelimiter_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new AsciiTableFormatter(Locale.UK, '-', '\u0007'),
            "Constructor with invalid column delimiter should throw IllegalArgumentException.");
    }

    @Test
    void constructor_invalidTitleRowDelimiter_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new AsciiTableFormatter(Locale.UK, '\u0001', '|'),
            "Constructor with invalid title row delimiter should throw IllegalArgumentException.");
    }

    @Test
    void format_nullResults_throwsIllegalArgumentException()
    {
        formatter = new AsciiTableFormatter();
        assertThrows(IllegalArgumentException.class, () -> formatter.format(null),
            "Formatting null results should throw IllegalArgumentException.");
    }

    @Test
    void format_emptyResults_returnsNoResultsMessage()
    {
        formatter = new AsciiTableFormatter();
        String formatted = formatter.format(Collections.emptyList());
        assertEquals("No benchmark results to format.", formatted,
            "Formatting empty results should return the 'No benchmark results' message.");
    }

    @Test
    void format_singleResultWithInvariantParameters_formatsCorrectly()
    {
        formatter = new AsciiTableFormatter(Locale.UK, '-', '|');
        when(mockContext.getSortStrategyName()).thenReturn("BubbleSort");
        when(mockContext.getDataType()).thenReturn(DataType.SORTED);
        when(mockContext.getDataLength()).thenReturn(1000);
        when(mockResult.getContext()).thenReturn(mockContext);
        when(mockResult.getProfilingMode()).thenReturn(ProfilingMode.EXECUTION_TIME);
        when(mockResult.getAggregate()).thenReturn(1.234);
        when(mockResult.getIterations()).thenReturn(5);

        List<AggregatedResult> results = Collections.singletonList(mockResult);
        String formattedTable = formatter.format(results);

        assertTrue(formattedTable.contains("Algorithm: BubbleSort"),
            "Formatted table should contain the invariant Algorithm.");
        assertTrue(formattedTable.contains("Data Type: " + DataType.SORTED.toString()),
            "Formatted table should contain the invariant Data Type.");
        assertTrue(formattedTable.contains("Data Size: 1,000"),
            "Formatted table should contain the invariant Data Size.");
        assertTrue(formattedTable.contains("Profiling Mode: " + ProfilingMode.EXECUTION_TIME.toString()),
            "Formatted table should contain the invariant Profiling Mode.");
        assertTrue(formattedTable.contains("Aggregate: 1.23"),
            "Formatted table should contain the invariant Aggregate.");
        assertTrue(formattedTable.contains("Iterations: 5"),
            "Formatted table should contain the invariant Iterations.");
        assertFalse(formattedTable.contains(String.valueOf('-')),
            "Formatted table should not contain any title row delimiters for all invariant parameters.");
        assertFalse(formattedTable.contains(String.valueOf('|')),
            "Formatted table should not contain any column delimiters for all invariant parameters.");
    }

    @Test
    void format_multipleResultsWithVariableParameters_formatsCorrectly()
    {
        formatter = new AsciiTableFormatter(Locale.UK, '-', '|');
        AggregatedResult mockResult2 = Mockito.mock(AggregatedResult.class);
        BenchmarkContext mockContext2 = Mockito.mock(BenchmarkContext.class);

        when(mockContext.getSortStrategyName()).thenReturn("BubbleSort");
        when(mockContext.getDataType()).thenReturn(DataType.SORTED);
        when(mockContext.getDataLength()).thenReturn(1000);
        when(mockResult.getContext()).thenReturn(mockContext);
        when(mockResult.getProfilingMode()).thenReturn(ProfilingMode.EXECUTION_TIME);
        when(mockResult.getAggregate()).thenReturn(1.234);
        when(mockResult.getIterations()).thenReturn(5);

        when(mockContext2.getSortStrategyName()).thenReturn("MergeSort");
        when(mockContext2.getDataType()).thenReturn(DataType.REVERSED);
        when(mockContext2.getDataLength()).thenReturn(5000);
        when(mockResult2.getContext()).thenReturn(mockContext2);
        when(mockResult2.getProfilingMode()).thenReturn(ProfilingMode.EXECUTION_TIME);
        when(mockResult2.getAggregate()).thenReturn(3.456);
        when(mockResult2.getIterations()).thenReturn(5);

        List<AggregatedResult> results = List.of(mockResult, mockResult2);
        String formattedTable = formatter.format(results);

        Pattern dataSizePattern = Pattern.compile("\\|\\s*\\d{1,3}(?:,\\d{3})*\\s*\\|");
        Matcher dataSizeMatcher = dataSizePattern.matcher(formattedTable);

        Pattern aggregatePattern = Pattern.compile("\\|\\s*\\d+\\.\\d{2}\\s*\\|");
        Matcher aggregateMatcher = aggregatePattern.matcher(formattedTable);

        assertTrue(formattedTable.contains("Profiling Mode: " + ProfilingMode.EXECUTION_TIME.toString()),
            "Formatted table should contain the invariant Profiling Mode.");
        assertTrue(dataSizeMatcher.find() && dataSizeMatcher.group().contains("1,000"),
            "Formatted table should contain the first right-padded formatted Data Size.");
        assertTrue(dataSizeMatcher.find() && dataSizeMatcher.group().contains("5,000"),
            "Formatted table should contain the second right-padded formatted Data Size.");
        assertTrue(aggregateMatcher.find() && aggregateMatcher.group().contains("1.23"),
            "Formatted table should contain the first right-padded formatted Aggregate value.");
        assertTrue(aggregateMatcher.find() && aggregateMatcher.group().contains("3.46"),
            "Formatted table should contain the second right-padded formatted Aggregate value.");
    }
}
