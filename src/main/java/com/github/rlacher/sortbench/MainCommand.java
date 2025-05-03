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
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import com.github.rlacher.sortbench.benchmark.BenchmarkRunner;
import com.github.rlacher.sortbench.benchmark.Benchmarker.ProfilingMode;
import com.github.rlacher.sortbench.benchmark.data.BenchmarkData.DataType;
import com.github.rlacher.sortbench.config.ConfigRetriever;
import com.github.rlacher.sortbench.logging.CustomFormatter;
import com.github.rlacher.sortbench.logging.LoggingUtil;
import com.github.rlacher.sortbench.processing.MedianAggregator;
import com.github.rlacher.sortbench.processing.SkipIterationFilter;
import com.github.rlacher.sortbench.results.AggregatedResult;
import com.github.rlacher.sortbench.results.BenchmarkResult;
import com.github.rlacher.sortbench.results.ResultAggregator;
import com.github.rlacher.sortbench.sorter.Sorter;
import com.github.rlacher.sortbench.strategies.SortStrategy;
import com.github.rlacher.sortbench.strategies.implementations.BubbleSortStrategy;
import com.github.rlacher.sortbench.strategies.implementations.HeapSortStrategy;
import com.github.rlacher.sortbench.strategies.implementations.InsertionSortStrategy;
import com.github.rlacher.sortbench.strategies.implementations.MergeSortStrategy;
import com.github.rlacher.sortbench.strategies.implementations.QuickSortStrategy;
import com.github.rlacher.sortbench.util.AsciiTableFormatter;

/**
 * The entry point for the sorting algorithm benchmarking application.
 * 
 * <p>This class runs the sorting algorithms.</p>
 */
@Command(
    name = "sort-benchmark-cli",
    version = "v1.0.0-cli-docker",
    mixinStandardHelpOptions = true,
    description = "Benchmark sorting algorithms from the command line."
)
public class MainCommand implements Callable<Integer>
{
    /** Logger for logging messages. */
    private static final Logger logger = Logger.getLogger(MainCommand.class.getName());

    /** Sorting algorithms to benchmark (command line option). */
    @Option(names = {"-a", "--algorithms", "--algorithm"}, description = "Sorting algorithms to benchmark.", arity="1..5", defaultValue = "all")
    private Set<String> algorithmNames;

    /** Set of data types to be used for the benchmark (command line option). */
    @Option(names = {"-t", "--types", "--type"}, description = "Type of the data to be sorted.", arity="1..4", defaultValue = "RANDOM")
    private Set<DataType> dataTypes;

    /** Number of iterations each sort algorithm runs on data of the same length and type (command line option). */
    @Option(names = {"-i", "--iterations"}, description = "Number of iterations for the benchmark.", defaultValue = "5")
    private int iterations;

    /** Input data sizes for the sort algorithms (command line option). */
    @Option(names = {"-s", "--sizes", "--size"}, description = "Input data sizes for the benchmark.", arity="1..n", required = true)
    private List<Integer> inputSizes;

    /** Verbosity flag (command-line option). */
    @Option(names = {"--verbose"}, description = "Enable verbose mode.", defaultValue = "false")
    private boolean verboseMode;

    /** Default profiling mode for the benchmark. */
    private static final ProfilingMode DEFAULT_PROFILING_MODE = ProfilingMode.EXECUTION_TIME;

    /** The number of JVM warmup iterations to skip. */
    private static final int WARMUP_ITERATIONS_TO_SKIP = 2;

    /** Map of available sort strategies (name to class type). */
    private static final Map<String, Class<? extends SortStrategy>> AVAILABLE_STRATEGIES = Map.of
    (
        "BubbleSort", BubbleSortStrategy.class,
        "HeapSort", HeapSortStrategy.class,
        "InsertionSort", InsertionSortStrategy.class,
        "MergeSort", MergeSortStrategy.class,
        "QuickSort", QuickSortStrategy.class
    );

    /** Private constructor. As the entry point of the application, instantiation of {@link MainCommand} is not intended. */
    private MainCommand() {}

    /**
     * Initialises and runs the sorting algorithm benchmarking process.
     * 
     * <p>This method is called when the command line interface (CLI) is executed. It sets up the logging format,
     * validates the input parameters, and runs the benchmark. It uses the {@link Sorter} class to run the benchmark
     * and the {@link BenchmarkRunner} class to orchestrate the benchmarking process. It also uses the{@link ResultAggregator}
     * class to process the benchmark results and the {@link AsciiTableFormatter} class to format the output.</p>
     *
     * @return Exits with code 0 if successful (non-zero exit codes indicate errors).
     */
    @Override
    public Integer call() throws Exception
    {
        LoggingUtil.setFormatter(new CustomFormatter(verboseMode));

        Sorter sorter = new Sorter();
        BenchmarkRunner runner = new BenchmarkRunner(sorter);

        final boolean throwIfNotFound = true;
        final Map<String, Class<? extends SortStrategy>> selectedStrategies =
            (algorithmNames.size() == 1 && algorithmNames.iterator().next().equalsIgnoreCase("all"))
            ? AVAILABLE_STRATEGIES
            : new HashMap<>(ConfigRetriever.validateAndFilterMap(
                AVAILABLE_STRATEGIES,
                algorithmNames,
                throwIfNotFound
            ));

        List<BenchmarkResult> results = runner.run(
            selectedStrategies,
            dataTypes,
            inputSizes,
            iterations,
            DEFAULT_PROFILING_MODE
        );

        Supplier<Predicate<BenchmarkResult>> skipIterationFilterSupplier = () -> new SkipIterationFilter(WARMUP_ITERATIONS_TO_SKIP);
        Supplier<Function<List<BenchmarkResult>, Double>> medianAggregatorSupplier = () -> new MedianAggregator();
        ResultAggregator resultAggregator = new ResultAggregator(skipIterationFilterSupplier, medianAggregatorSupplier);
        List<AggregatedResult> aggregatedResults = resultAggregator.process(results);

        AsciiTableFormatter tableFormatter = new AsciiTableFormatter();
        logger.info(tableFormatter.format(aggregatedResults));

        return 0;
    }

    /**
     * The main method, which starts the sorting algorithm benchmarking process.
     *
     * <p>This method merely executes the command line interface (CLI) using the {@link CommandLine} class and processes its exit code.</p>
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args)
    {
        int exitCode = new CommandLine(new MainCommand()).execute(args);
        System.exit(exitCode);
    }
}
