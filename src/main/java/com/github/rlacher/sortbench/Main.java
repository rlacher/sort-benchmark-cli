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
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

import com.github.rlacher.sortbench.benchmark.BenchmarkRunner;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData;
import com.github.rlacher.sortbench.logging.CustomFormatter;
import com.github.rlacher.sortbench.logging.LoggingUtil;
import com.github.rlacher.sortbench.processing.MedianAggregator;
import com.github.rlacher.sortbench.processing.SkipIterationFilter;
import com.github.rlacher.sortbench.results.AggregatedResult;
import com.github.rlacher.sortbench.results.BenchmarkResult;
import com.github.rlacher.sortbench.results.ResultAggregator;
import com.github.rlacher.sortbench.sorter.Sorter;


/**
 * The entry point for the sorting algorithm benchmarking application.
 * 
 * <p>This class runs the sorting algorithms.</p>
 */
public class Main
{
    /** Logger for logging messages. */
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    /**
     * Default benchmark data sizes for the sorting algorithms.
     * 
     * <p>The data points are chose to hightlight constant factors in smaller arrays sizes and demonstrate scaling characteristics in larger arrays.</p>
     */
    private static final List<Integer> BENCHMARK_DATA_SIZES = List.of(100, 1000, 10000);

    /**
     * Default number of iterations for the benchmark.
     * 
     * <p>This is used to determine how many times each sorting algorithm should run on input data with the same characteristics (length and type).</p>
     */
    private static final int BENCHMARK_ITERATIONS = 5;

    /** The number of JVM warmup iterations to skip. */
    private static final int WARMUP_ITERATIONS_TO_SKIP = 2;

    /** Enables verbose log messages. */
    private static final boolean VERBOSE_MODE = true;

    /** Private constructor. As the entry point of the application, instantiation of {@link Main} is not intended. */
    private Main() {}

    /**
     * The main method, which starts the sorting algorithm benchmarking process.
     * 
     * <p>This class initialises and runs a list of sorting algorithms against a predefined benchmark dataset.</p>
     *
     * @param args Command-line arguments (currently unused).
     */
    public static void main(String[] args)
    {
        LoggingUtil.setFormatter(new CustomFormatter(VERBOSE_MODE));

        Sorter sorter = new Sorter();
        BenchmarkRunner runner = new BenchmarkRunner(sorter);
        Map<String, Object> config = buildBenchmarkConfig();
        logger.info(String.format("Benchmark config: %s", config));

        List<BenchmarkResult> results = runner.run(config);

        Supplier<Predicate<BenchmarkResult>> skipIterationFilterSupplier = () -> new SkipIterationFilter(WARMUP_ITERATIONS_TO_SKIP);
        Supplier<Function<List<BenchmarkResult>, Double>> medianAggregatorSupplier = () -> new MedianAggregator();
        ResultAggregator resultAggregator = new ResultAggregator(skipIterationFilterSupplier, medianAggregatorSupplier);
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
        config.put("strategies", Set.of("bubblesort", "HEAPSORT", "InsertionSort", "MergeSort", "QuickSort"));
        config.put("data_type", BenchmarkData.DataType.RANDOM.toString());
        config.put("profiling_mode", ProfilingMode.EXECUTION_TIME);
        return config;
    }
}
