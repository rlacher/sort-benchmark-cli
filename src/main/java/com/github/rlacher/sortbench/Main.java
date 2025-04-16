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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.github.rlacher.sortbench.benchmark.*;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.results.AggregatedResult;
import com.github.rlacher.sortbench.results.BenchmarkResult;
import com.github.rlacher.sortbench.results.ResultAggregator;
import com.github.rlacher.sortbench.sorter.Sorter;


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
    private static final List<Integer> BENCHMARK_DATA_SIZES = List.of(100, 1000, 10000);

    /**
     *  Default number of iterations for the benchmark.
     * 
     *  This is used to determine how many times each sorting algorithm should run on input data with the same characteristics (length and type).
     */
    private static final int BENCHMARK_ITERATIONS = 5;

    /**
     * The main method, which starts the sorting algorithm benchmarking process.
     * 
     * This class initializes and runs a list of sorting algorithms against a predefined benchmark dataset.
     *
     * @param args Command-line arguments (currently unused).
     */
    public static void main(String[] args)
    {
        Sorter sorter = new Sorter();
        BenchmarkRunner runner = new BenchmarkRunner(sorter);
        Map<String, Object> config = buildBenchmarkConfig();
        logger.info(String.format("Benchmark config: %s", config));

        List<BenchmarkResult> results = runner.run(config);

        ResultAggregator resultAggregator = new ResultAggregator(ResultAggregator.DEFAULT_FILTER, ResultAggregator.DEFAULT_AGGREGATOR);
        List<AggregatedResult> aggregatedResults = resultAggregator.process(results);

        for(var aggregatedResult : aggregatedResults)
        {
            logger.info(String.format("Aggregated result: %s", aggregatedResult));
        }
    }

    /**
     * Returns a default benchmark configuration.
     * 
     * @return A map containing the default benchmark configuration.
     */
    private static Map<String, Object> buildBenchmarkConfig()
    {
        Map<String, Object> config = new HashMap<>();
        config.put("input_sizes", BENCHMARK_DATA_SIZES);
        config.put("iterations", BENCHMARK_ITERATIONS);
        config.put("strategies", List.of("BubbleSort", "InsertionSort", "MergeSort", "HeapSort"));
        config.put("profiling_mode", ProfilingMode.EXECUTION_TIME);
        return config;
    }
}
