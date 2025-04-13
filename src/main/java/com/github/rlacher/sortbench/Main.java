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

package com.github.rlacher.sortbench;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.github.rlacher.sortbench.benchmark.BenchmarkResult;
import com.github.rlacher.sortbench.benchmark.Benchmarker;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkDataFactory;
import com.github.rlacher.sortbench.reporting.ResultAggregator;
import com.github.rlacher.sortbench.sorter.Sorter;
import com.github.rlacher.sortbench.strategies.SortStrategy;
import com.github.rlacher.sortbench.strategies.implementations.*;

/**
 * The entry point for the sorting algorithm benchmarking application.
 * 
 * This class runs the sorting algorithms.
 */
public class Main
{
    /** 
     * Logger for logging messages.
     */
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    /**
     * Default benchmark data sizes for the sorting algorithms.
     * 
     * The data points are chose to hightlight constant factors in smaller arrays sizes and demonstrate scaling characteristics in larger arrays.
     */
    private static final int[] BENCHMARK_DATA_SIZES = new int[]
    {
        100,
        1000,
        10000
    };

    /**
     * Default benchmark data types for the sorting algorithms (all data types are used).
     */
    private static final List<BenchmarkData.DataType> BENCHMARK_DATA_TYPES = Arrays.asList
    (
        BenchmarkData.DataType.SORTED,
        BenchmarkData.DataType.REVERSED,
        BenchmarkData.DataType.RANDOM,
        BenchmarkData.DataType.PARTIALLY_SORTED
    );

    /**
     *  Default number of runs for the benchmark.
     * 
     *  This is used to determine how many times each sorting algorithm should run on input data with the same characteristics (length and type).
     */
    private static final int DEFAULT_NUMBER_OF_RUNS = 5;

    /**
     * The main method, which starts the sorting algorithm benchmarking process.
     * 
     * This class initializes and runs a list of sorting algorithms against a predefined benchmark dataset.
     *
     * @param args Command-line arguments (currently unused).
     */
    public static void main(String[] args)
    {
        List<SortStrategy> sortStrategies = Arrays.asList
        (
            new BubbleSortStrategy(new Benchmarker(ProfilingMode.EXECUTION_TIME)),
            new MergeSortStrategy(new Benchmarker(ProfilingMode.EXECUTION_TIME)),
            new BubbleSortStrategy(new Benchmarker(ProfilingMode.MEMORY_USAGE)),
            new MergeSortStrategy(new Benchmarker(ProfilingMode.MEMORY_USAGE)),
            new BubbleSortStrategy(new Benchmarker(ProfilingMode.SWAP_COUNT)),
            new MergeSortStrategy(new Benchmarker(ProfilingMode.SWAP_COUNT))
        );
        
        sortBenchmark(sortStrategies);
    }

    /**
     * Runs the provided sorting strategies on the given benchmark data through the sorter context.
     * 
     * The benchmark result is printed to the console for each algorithm.
     * 
     * @param sortStrategies The list of sorting strategies to be executed.
     */
    private static void sortBenchmark(List<SortStrategy> sortStrategies)
    {
        if(sortStrategies == null)
        {
            throw new IllegalArgumentException("The list of sort strategies must not be null.");
        }

        // Initialise benchmarkData with data arrangements that all sort strategies will run on.
        Map<Integer, List<BenchmarkData>> benchmarkDataMap = generateRandomBenchmarkDataBySizes(BENCHMARK_DATA_SIZES);

        ResultAggregator resultAggregator = new ResultAggregator(ResultAggregator.DEFAULT_FILTER, ResultAggregator.DEFAULT_AGGREGATOR);

        benchmarkDataMap.forEach((dataSize, benchmarkDataList) ->
        {
            logger.info(String.format("Data size: %d", dataSize));

            sortStrategies.forEach(sortStrategy ->
            {
                Sorter sorter = new Sorter(sortStrategy);

                List<BenchmarkResult> benchmarkResults = benchmarkDataList.stream()
                .map(benchmarkData ->
                     performSorting(sorter, benchmarkData))
                .toList();

                benchmarkResults.stream().forEach(result ->  
                    logger.fine(String.format("Sorting strategy: %s, benchmark result: %s", sortStrategy.getClass().getSimpleName(), result.toString())));

                BenchmarkResult aggregatedBenchmarkResult = resultAggregator.process(benchmarkResults);
                logger.info(String.format("Sorting strategy: %s, aggregated benchmark result: %s", sortStrategy.getClass().getSimpleName(), aggregatedBenchmarkResult));
            });
        });
    }

    private static BenchmarkResult performSorting(Sorter sorter, final BenchmarkData benchmarkData)
    {
        BenchmarkData benchmarkDataCopy = new BenchmarkData(benchmarkData);
        logger.finer(String.format("Data before sorting: %s", benchmarkDataCopy.toString()));
        BenchmarkResult benchmarkResult = sorter.sort(benchmarkDataCopy.getData());
        logger.finer(String.format("Data after sorting: %s", benchmarkDataCopy.toString()));
        return benchmarkResult;
    }

    /**
     * Generates benchmark data grouped by data length to facilitate batch processing.
     * This improves JVM optimisation by enhancing data locality.
     *
     * @param sizes An array of data sizes to generate benchmark data for.
     * @return A map where keys are data lengths and values are lists of benchmark data.
     */
    private static Map<Integer, List<BenchmarkData>> generateRandomBenchmarkDataBySizes(final int[] sizes)
    {
        Map<Integer, List<BenchmarkData>> dataBySize = new HashMap<>();

        IntStream.range(0, sizes.length)
            .forEach(i -> 
            {
                final int dataSize = sizes[i];
                List<BenchmarkData> dataList = Stream.generate(() -> BenchmarkDataFactory.createRandomData(dataSize))
                    .limit(DEFAULT_NUMBER_OF_RUNS)
                    .collect(Collectors.toList());
                dataBySize.put(dataSize, dataList);
            });

        return dataBySize;
    }
}
