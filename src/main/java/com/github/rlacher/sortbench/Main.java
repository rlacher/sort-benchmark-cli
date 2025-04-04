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

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.github.rlacher.sortbench.benchmark.BenchmarkResult;
import com.github.rlacher.sortbench.benchmark.Benchmarker;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkDataFactory;
import com.github.rlacher.sortbench.sorter.Sorter;
import com.github.rlacher.sortbench.strategies.SortStrategy;
import com.github.rlacher.sortbench.strategies.DummySortStrategy;

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
     * Initial benchmark data for benchmarking
     */
    private static final BenchmarkData BENCHMARK_DATA = BenchmarkDataFactory.createRandomData(10);

    /**
     * The main method, which starts the sorting algorithm benchmarking process.
     * 
     * This class initializes and runs a list of sorting algorithms against a predefined benchmark dataset.
     *
     * @param args Command-line arguments (currently unused).
     */
    public static void main(String[] args)
    {
        logger.fine(String.format("Benchmark data: %s", BENCHMARK_DATA));

        List<SortStrategy> sortStrategies = Arrays.asList
        (
            new DummySortStrategy(new Benchmarker(ProfilingMode.EXECUTION_TIME))
        );
        
        sortBenchmark(sortStrategies, BENCHMARK_DATA);
    }

    /**
     * Runs the provided sorting strategies on the given benchmark data through the sorter context.
     * 
     * The benchmark result is printed to the console for each algorithm.
     * 
     * @param sortStrategies The list of sorting strategies to be executed.
     * @param benchmarkData The benchmark data to be sorted.
     */
    private static void sortBenchmark(List<SortStrategy> sortStrategies, final BenchmarkData benchmarkData)
    {
        if(sortStrategies == null)
        {
            throw new IllegalArgumentException("The list of sort strategies must not be null.");
        }

        for (SortStrategy sortStrategy : sortStrategies)
        {
            Sorter sorter = new Sorter(sortStrategy);
            BenchmarkResult benchmarkResult = sorter.sort(benchmarkData.getDataCopy());
            
            logger.info(String.format("Sorting strategy: %s, benchmark result: %s", sortStrategy.getClass().getSimpleName(), benchmarkResult.toString()));
        }
    }
}
