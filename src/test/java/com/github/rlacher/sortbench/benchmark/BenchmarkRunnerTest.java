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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.github.rlacher.sortbench.sorter.Sorter;
import com.github.rlacher.sortbench.strategies.SortStrategy;
import com.github.rlacher.sortbench.strategies.implementations.MergeSortStrategy;
import com.github.rlacher.sortbench.strategies.implementations.QuickSortStrategy;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData.DataType;
import com.github.rlacher.sortbench.results.BenchmarkContext;
import com.github.rlacher.sortbench.results.BenchmarkMetric;
import com.github.rlacher.sortbench.results.BenchmarkResult;

// Unit and integration tests for the BenchmarkRunner class.
class BenchmarkRunnerTest
{
    private Sorter mockSorter;
    private BenchmarkRunner benchmarkRunner;
    private Map<String, Class<? extends SortStrategy>> selectedStrategies;
    private Set<BenchmarkData.DataType> dataTypes;
    private List<Integer> inputSizes;
    private int iterations;
    private ProfilingMode profilingMode;

    @BeforeEach
    void setUp()
    {
        mockSorter = Mockito.mock(Sorter.class);
        benchmarkRunner = new BenchmarkRunner(mockSorter);
        selectedStrategies = Map.of("MergeSort", MergeSortStrategy.class);
        dataTypes = Set.of(BenchmarkData.DataType.RANDOM);
        inputSizes = List.of(10);
        iterations = 1;
        profilingMode = ProfilingMode.EXECUTION_TIME;
    }

    @Test
    void constructor_nullSorter_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> new BenchmarkRunner(null), "Constructor should throw IllegalArgumentException when sorter is null");
    }

    @Test
    void run_nullStrategies_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(null, dataTypes, inputSizes, iterations, profilingMode),
            "run() should throw IllegalArgumentException when selectedStrategies is null");
    }

    @Test
    void run_nullDataTypes_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(selectedStrategies, null, inputSizes, iterations, profilingMode),
                "run() should throw IllegalArgumentException when dataTypes is null");
    }

    @Test
    void run_nullInputSizes_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(selectedStrategies, dataTypes, null, iterations, profilingMode),
                "run() should throw IllegalArgumentException when inputSizes is null");
    }

    @Test
    void run_nullProfilingMode_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(selectedStrategies, dataTypes, inputSizes, iterations, null),
                "run() should throw IllegalArgumentException when profilingMode is null");
    }

    @Test
    void run_nullKeyStrategy_throwsIllegalArgumentException()
    {
        Map<String, Class<? extends SortStrategy>> nullKeyStrategies = new HashMap<>();
        nullKeyStrategies.put(null, MergeSortStrategy.class);
        
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(nullKeyStrategies, dataTypes, inputSizes, iterations, profilingMode),
            "run() should throw IllegalArgumentException when selectedStrategies contains a null key");
    }

    @Test
    void run_nullValueStrategy_throwsIllegalArgumentException()
    {
        Map<String, Class<? extends SortStrategy>> nullValueStrategies = new HashMap<>();
        nullValueStrategies.put("MergeSort", null);

        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(nullValueStrategies, dataTypes, inputSizes, iterations, profilingMode),
            "run() should throw IllegalArgumentException when selectedStrategies contains a null value");
    }

    @Test
    void run_emptyStrategies_throwsIllegalArgumentException()
    {
        final Map<String, Class<? extends SortStrategy>> emptyStrategies = Collections.emptyMap();

        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(emptyStrategies, dataTypes, inputSizes, iterations, profilingMode),
            "run() should throw IllegalArgumentException when selectedStrategies is empty");
    }

    @Test
    void run_zeroIterations_throwsIllegalArgumentException()
    {
        final int zeroIterations = 0;
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(selectedStrategies, dataTypes, inputSizes, zeroIterations, profilingMode),
            "run() should throw IllegalArgumentException when iterations is 0");
    }

    @Test
    void run_negativeIterations_throwsIllegalArgumentException()
    {
        final int negativeIterations = -1;
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(selectedStrategies, dataTypes, inputSizes, negativeIterations, profilingMode),
            "run() should throw IllegalArgumentException when iterations is negative");
    }

    @Test
    void run_negativeInputSize_throwsIllegalArgumentException()
    {
        final List<Integer> negativeInputSizes = List.of(-10);
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(selectedStrategies, dataTypes, negativeInputSizes, iterations, profilingMode), 
            "run() should throw IllegalArgumentException when any input size in inputSizes is negative");
    }

    @Test
    void run_emptyDataTypes_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(selectedStrategies, Collections.emptySet(), inputSizes, iterations, profilingMode),
            "run() should throw IllegalArgumentException when dataTypes is empty");
    }

    @Test
    void run_emptyInputSizes_throwsIllegalArgumentException()
    {
        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(selectedStrategies, dataTypes, Collections.emptyList(), iterations, profilingMode),
            "run() should throw IllegalArgumentException when inputSizes is empty");
    }

    @Test
    void run_nonInstantiableStrategy_throwsIllegalStateException()
    {
        class FailingSortStrategy implements SortStrategy
        {
            public FailingSortStrategy(Benchmarker benchmarker)
            {
                throw new RuntimeException("Intentionally failing instantiation for testing purposes.");
            }

            @Override
            public BenchmarkMetric sort(final int[] array)
            {
                return new BenchmarkMetric(ProfilingMode.NONE, 0);
            }
        };
        final Map<String, Class<? extends SortStrategy>> failingStrategies = Map.of("FailingSort", FailingSortStrategy.class);

        assertThrows(IllegalStateException.class, () -> benchmarkRunner.run(failingStrategies, dataTypes, inputSizes, iterations, profilingMode),
            "run() should throw IllegalStateException if a strategy is not instantiable.");
    }

    @Test
    void run_benchmarkDataWithInvalidStrategy_throwsIllegalStateException()
    {
        final BenchmarkContext invalidStrategyContext = new BenchmarkContext(DataType.SORTED, 10, "InvalidStrategyName");

        Map<BenchmarkContext, List<BenchmarkData>> invalidStrategyMap = new HashMap<>();
        invalidStrategyMap.put(invalidStrategyContext, new ArrayList<BenchmarkData>());

        BenchmarkRunner spiedRunner = spy(new BenchmarkRunner(mockSorter));
        doReturn(invalidStrategyMap)
            .when(spiedRunner)
            .generateBenchmarkData(anySet(), anySet(), anyList(), anyInt());

        assertThrows(IllegalStateException.class, () -> spiedRunner.run(selectedStrategies, dataTypes, inputSizes, iterations, profilingMode),
        "Running with benchmark data containing an invalid strategy should throw an IllegalStateException.");
    }

    @Test
    void run_duplicateKeys_throwsIllegalArgumentException()
    {
        final Map<String, Class<? extends SortStrategy>> strategiesKeyDuplicates = Map.of
        (
            "SAMEKEYSORT", MergeSortStrategy.class,
            "samekeysort", QuickSortStrategy.class
        );

        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(strategiesKeyDuplicates, dataTypes, inputSizes, iterations, profilingMode),
            "Duplicate strategy keys should throw IllegalArgumentException");
    }

    @Test
    void run_duplicateValues_throwsIllegalArgumentException()
    {
        final Map<String, Class<? extends SortStrategy>> strategiesValueDuplicates = Map.of
        (
            "FirstMergeSort", MergeSortStrategy.class,
            "SecondMergeSort", MergeSortStrategy.class
        );

        assertThrows(IllegalArgumentException.class, () -> benchmarkRunner.run(strategiesValueDuplicates, dataTypes, inputSizes, iterations, profilingMode),
            "Duplicate strategy classes should throw IllegalArgumentException");
    }

    @Test
    void run_validParameters_returnsBenchmarkResults()
    {
        when(mockSorter.sort(any())).thenReturn(new BenchmarkMetric(ProfilingMode.EXECUTION_TIME, 0));

        List<BenchmarkResult> benchmarkResults = benchmarkRunner.run(selectedStrategies, dataTypes, inputSizes, iterations, profilingMode);
        final int expectedSize = selectedStrategies.size() * dataTypes.size() * inputSizes.size() * iterations;
        assertEquals(expectedSize, benchmarkResults.size(), "run() should return the correct number of benchmark results");
    }
}
