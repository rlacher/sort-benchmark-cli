<!--
Repository description: Docker CLI sort benchmark: Reproducible performance analysis across algorithms.

Repository topics: java, docker, benchmark, sorting, algorithm, cli.
-->

# Sorting Algorithm Benchmark

<!-- Badge ordering: Status, legal, deployment -->
[![Java CI Checks](https://github.com/rlacher/sort-benchmark-cli/actions/workflows/java-ci.yml/badge.svg)](https://github.com/rlacher/sort-benchmark-cli/actions/workflows/java-ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-lightgrey.svg)](https://opensource.org/licenses/MIT)
[![Docker Hub Image](https://img.shields.io/docker/pulls/renelacher/sort-benchmark-cli)](https://hub.docker.com/r/renelacher/sort-benchmark-cli)

Explore the nuances of sorting algorithm efficiency through this command-line interface (CLI) tool. Gain insights into the performance characteristics of popular sorting algorithms under controlled conditions within a containerised setup.

## Table of Contents

- [Why use this?](#why-use-this)
- [Features](#features)
- [Getting Started](#getting-started)
- [API Documentation](#api-documentation)
- [Benchmark Details](#benchmark-details)
- [Benchmark Methodology](#benchmark-methodology)
- [Design](#design)
- [Test](#test)
- [Benchmark Results](#benchmark-results)
- [Performance Discussion](#performance-discussion)
- [License](#license)
- [Author](#author)

## Why use this?

This tool offers developers and enthusiasts a reproducible and efficient way to benchmark sorting algorithms encountered in both industry and academic settings, providing practical insights relevant to real-world applications.

## Features

- **Performance Comparison:** Explore sorting algorithm efficiency.
- **Custom Data Generation:** Tailor input data for specific data scenarios.
- **Reproducible Benchmarks:** Run tests reliably in a containerised environment.
- **Modular Architecture:** Benefit from a maintainable and clean design.
- **Command-Line Interface:** Streamlined control with clear ASCII table output.
- **Effortless Setup:** Experience quick and easy benchmarking.

## Getting Started

Get up and running quickly using containerised deployment. You can start benchmarking with a single command, no need to clone or build locally.

```bash
docker container run --rm renelacher/sort-benchmark-cli --size 1000
```

The CLI accepts these five arguments:

| Argument             | Description                                                                  |
|----------------------|------------------------------------------------------------------------------|
| `-a`, `--algorithms` | The sorting algorithms to execute (e.g. `quicksort`).                        |
| `-t`, `--types`      | The types of data to generate for benchmarking (e.g. `RANDOM`, `SORTED`).    |
| `-s`, `--sizes`      | The number of elements in the generated data arrays.                         |
| `-i`, `--iterations` | How many times to run the benchmark with the specified algorithms and sizes. |
| `--verbose`          | Increase verbosity of log output.                                            |

Both singular (`--algorithm`) and plural (`--algorithms`) forms of command options are supported.
The CLI currently uses the default profiling mode: `EXECUTION_TIME` (see [Measurement](#measurement)).

## API Documentation

[Javadoc](https://rlacher.github.io/sort-benchmark/api/) - Reference API for the sort-benchmark Java backend libraries.

## Benchmark Details

This section outlines the specifics of how the sorting benchmarks are conducted.

### Input Data Characteristics

This benchmark assesses sorting algorithms performance on primitive integer arrays (`int[]`).

- *Max Performance:* Direct memory access, no boxing.
- *Core Algorithm Focus:* Isolates sorting efficiency.
- *In-Place Sorting:* Minimal memory use.

### Data Types

The `--types` CLI argument controls data distribution. Options:

- `PARTIALLY_SORTED`: Data with some existing order.
- `RANDOM`: Psuedo-randomly generated data.
- `REVERSE`: Data sorted in descending order.
- `SORTED`: Data sorted in ascending order.

Accepts a comma-separated list of data types. If omitted, the CLI argument defaults to `RANDOM`.

### Execution Environment

To minimise interference from background processes and ensure a consistent testing environment, benchmarks are executed within a Docker container. The container uses default Java Virtual Machine (JVM) settings.

### Software Environment

This project is built and tested with the following software:

- *Operating System:* Ubuntu 24.04.1 LTS
- *Java:* OpenJDK 21.0.6
- *Build Tool:* Gradle 8.13
- *Testing Framework:* Junit 5.13
- *Mocking Library:* Mockito 5.17
- *CLI Parsing:* Picocli 4.7

## Benchmark Methodology

### Benchmark Configuration

Users can define key aspects of the benchmark execution, such as the algorithm to be tested, the data type and size for benchmark data generation. To simplify the initial implementation and avoid overloading the user interface, some configuration parameters, including the profiling mode, are currently defined as compile-time constants.

### Data Generation

The `BenchmarkDataFactory` provides a single, parameterised entry point, `createData()`, which accepts the data type and length as arguments. Supported data types include `RANDOM`, `SORTED`, `REVERSED`, and `PARTIALLY_SORTED`.  Internally, data is generated using integer stream ranges and the JVM's default random number generator. This approach ensures efficient and reproducible data set creation for benchmarking.

### Algorithm Implementations

The following sorting algorithms are implemented in Java to sort in ascending order.

| **Algorithm** | **Description** | **Implementation**
| --- | --- | --- |
| Bubble Sort| Performs iterative passes through the array, comparing adjacent elements and swapping them if out of order. | Standard in-place. |
| Heap Sort | Leverages a heap data structure to sort an array by repeatedly extracting the root element and placing it in its final position. | Max heap is built bottom-up from the last non-leaf node. The `heapify` operation is implemented recursively. |
| Insertion Sort | Iteratively builds a sorted array by inserting each unsorted element into its correct position within the sorted portion. | Standard in-place. |
| Merge Sort | Recursively divides an array into smaller subarrays, sorts them, and merges them into a single sorted array. | Recursive top-down implementation with standard two-way merging using an auxiliary array. |
| Quick Sort | Selects a pivot element and partitions the array around it, recursively sorting the sub-arrays on either side of the pivot. | In-place sorting with the leftmost element as the pivot.

### Execution

The `BenchmarkRunner` orchestrates the benchmark execution by reading the configuration, delegating data generation to the `BenchmarkDataFactory`, and managing execution based on `BenchmarkContext` instances. This design promotes a clear separation of concerns and allows for flexible benchmark configuration.

### Measurement

- *Execution time:* Runtime of the sort operation, measured as the total wall-clock time elapsed.
- *Data writes:* Number of element writes (swaps, shifts, and inserts), counted directly within the sorting algorithm's code.
- *Memory usage:* JVM heap memory consumption, measured at key points during the sorting process.

### Result Aggregation

To ensure robust result aggregation and account for JVM warm-up, the `ResultAggregator` employs filtering and context-grouped aggregation. This approach mitigates the impact of factors such as garbage collection and JVM optimisations, leading to more reliable and accurate benchmark measurements. The `ResultAggregator` is configured with a supplier for a customisable `SkipIterationFilter` and `MedianAggregator` for this purpose.

## Design

The benchmark framework's modular and extensible architecture promotes flexibility and maintainability. It employs the Strategy pattern to allow easy addition and swapping of sorting algorithms, while a clear separation of concerns between data generation, benchmark execution, and result aggregation ensures a robust and adaptable benchmarking process, suitable for a wide range of performance evaluations.

### Class Diagram: Strategy pattern for Sorting Routines

![Class Diagram Sorter Strategies](./docs/uml/class-diagram-sorter-strategies.svg)

This benchmark project leverages the Strategy pattern to establish a modular and extensible architecture. Consequently, new sorting algorithms can be integrated without requiring modifications to the core benchmarking logic. Performance profiling is conducted by a dedicated `Benchmarker` class, which is injected into individual `SortStrategy` implementations, promoting loose coupling and enhanced code maintainability.

### Sequence Diagram: Benchmark Run

![Sequence Diagram Benchmark Run](./docs/uml/sequence-diagram-benchmark-process.svg)

The `BenchmarkRunner` orchestrates the repeated sorting process. It pre-generates `BenchmarkData` using the `BenchmarkDataFactory`, then repeatedly sorts this data using chosen `SortStrategy` implementations via the `Sorter`. The resulting benchmark results are then passed to a dedicated `ResultAggregator` for robust aggregation.

### Sequence Diagram: BenchmarkRunner.runIterations()

![Sequence Diagram Run Iterations](./docs/uml/sequence-diagram-runiterations.svg)

For each set of benchmark data, the `BenchmarkRunner` configures the `Sorter` with a strategy and then uses it to sort each data element, producing the raw benchmark metrics needed for the benchmark results.

*Notes on Diagrams:* For clarity and conciseness, the sequence diagrams in this document may omit certain aspects of the code, such as argument validation, logging, and error handling. The diagrams focus on illustrating the core workflows and interactions between key components.

## Test

This project features a robust suite of **326 unit and integration tests**, built with JUnit and Mockito, to ensure the reliability and correctness of both the benchmarking framework and sorting routines. The testing strategy rigorously applies principles such as boundary condition analysis, equivalence class partitioning, exception handling verification, and thorough data flow validation across components.

The current test suite achieves significant coverage, reaching **94% statement coverage** and **93% branch coverage**, demonstrating a strong commitment to code quality and comprehensive testing throughout the project.

### Running Unit Tests

1.  Clone the repository (if you haven't already).
2.  Navigate to the project directory in your terminal or command prompt.
3.  Run the Gradle test task (which automatically builds the project and resolves dependencies):
    ```bash
    ./gradlew test
    ```

### Viewing Test Coverage

After the tests complete, an HTML report will be generated in the `build/reports/jacoco/testReport/html` directory. Open the `index.html` file in your web browser to explore the coverage details.

## Benchmark Results

This section provides example benchmark results for different sorting algorithms and data characteristics, generated using the commands below. The output shows the median execution time (in milliseconds) after filtering, for each run. Consistent input sizes are maintained within comparisons for fairness.

### Quick Sort vs. Heap Sort/Merge Sort (Average Case)

This showcases the performance difference between O(n log n) algorithms.

Command:

```bash
docker container run --rm renelacher/sort-benchmark-cli --algorithms heapsort mergesort quicksort --type RANDOM --size 5000 --iterations 5
```

Output:
```text
Benchmark results (invariant parameters above table):
Data Type: RANDOM
Data Size: 5,000
Profiling Mode: EXECUTION_TIME
Iterations: 5

| Algorithm | Aggregate |
|-----------|-----------|
|  HeapSort |      0.61 |
| MergeSort |      0.70 |
| QuickSort |      0.37 |
```

### Quick Sort (Worst Case)

This illustrates how Quick Sort's performance degrades with first-element pivot selection on already sorted data, contrasted with random data.

```bash
docker container run --rm renelacher/sort-benchmark-cli --algorithm quicksort --types RANDOM SORTED --size 5000 --iterations 5
```

Output:
```text
Benchmark results (invariant parameters above table):
Algorithm: QuickSort
Data Size: 5,000
Profiling Mode: EXECUTION_TIME
Iterations: 5

| Data Type | Aggregate |
|-----------|-----------|
|    RANDOM |      0.34 |
|    SORTED |      5.29 |
```

### Bubble Sort vs. Insertion Sort (Efficiency Contrast)

This compares the performance of an inefficient (Bubble Sort) and a more efficient (Insertion Sort) algorithm.

Command:

```bash
docker container run --rm renelacher/sort-benchmark-cli --algorithms bubblesort insertionsort --type RANDOM --size 5000 --iterations 5
```

Output:
```text
Benchmark results (invariant parameters above table):
Data Type: RANDOM
Data Size: 5,000
Profiling Mode: EXECUTION_TIME
Iterations: 5

|     Algorithm | Aggregate |
|---------------|-----------|
|    BubbleSort |      8.25 |
| InsertionSort |      3.30 |
```

### Merge Sort vs. Insertion Sort (Scaling Behaviour)

This highlights the different scaling characteristics of Insertion Sort (`O(n²)`) and Merge Sort (`O(n log n)`) as input size grows. By making the data size ten times larger (from `10,000` to `100,000`) and running 30 iterations to allow for tiered JVM optimisation, the benchmark reveals that Insertion Sort's execution time multiplies by approximately 97 times, significantly more than Merge Sort's roughly 11 times growth.

```bash
docker container run --rm renelacher/sort-benchmark-cli --algorithms insertionsort mergesort --type RANDOM --sizes 10000 100000 --iterations 30
```

Output:
```text
Benchmark results (invariant parameters above table):
Data Type: RANDOM
Profiling Mode: EXECUTION_TIME
Iterations: 30

|     Algorithm | Data Size | Aggregate |
|---------------|-----------|-----------|
| InsertionSort |    10,000 |      4.08 |
|     MergeSort |    10,000 |      0.65 |
| InsertionSort |   100,000 |    394.57 |
|     MergeSort |   100,000 |      6.95 |
```

## Performance Discussion

Sorting performance, as benchmarked, is heavily influenced by both the chosen algorithm and the nature of the input data.

- Although Quick Sort, Heap Sort, and Merge Sort share O(n log n) average-case scaling, Quick Sort often performs best in practice due to smaller constant factors arising from fewer swaps and better cache locality compared to Heap Sort's maintenance overhead and Merge Sort's data movement with auxiliary arrays.
- A standard recursive Quick Sort is prone to stack overflow with consistently poor pivot selection (e.g. always the smallest/largest element).
- Quick Sort's runtime can exhibit greater variance due to its sensitivity to pivot quality for balanced partitions, contrasting with the more consistent performance of Merge Sort and Heap Sort.
- Bubble sort is primarily educational; its inefficiency results in significantly more swaps than necessary for practical sorting.
<!-- - Runtime strongly correlates with the number of data writes. While runtime is empirical, swap count validates theoretical complexity and inherent algorithm efficiency, independent of specific hardware and the execution environment. -->

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

Created by [René Lacher](https://github.com/rlacher).
