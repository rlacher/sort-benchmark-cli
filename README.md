<!--
Repository description: Microservice sorting benchmark: API backend & React frontend for interactive results.

Repository topics: java, docker, benchmark, sorting, algorithm, microservices, reactjs, rest-api.
-->

# Sorting Algorithm Benchmark

This project compares the performance of various sorting algorithms. The algorithms are implemented in Java.

## Purpose

The goal is to analyse and visualise the efficiency of different common sorting algorithms under various conditions.

## Key Features

- Scalable, distributed and modular architecture
- Performance comparison of sorting algorithms
- Customisable input data generation
- Reproducible containerised benchmarks
- Standardised REST API
- Interactive `React.js` result visualisation

Effortless setup and execution for quick benchmarking.

## Table of Contents

*TOC will go here*

## Getting Started

This project is designed for quick and easy setup. Through containerised deployment, you can have the benchmark running with a single command.

1. Clone the respository

    ```bash
    git clone https://github.com/rlacher/sort-algorithms.git
    ```

2. Navigate to the project directory
    ```bash
    cd sort-algorithms
    ```

2. Execute the application

    ```bash
    ./gradlew clean && ./gradlew run
    ```

3. *Instructions on how to configure the analysis will follow*

## Technical Details

### Benchmark Details

**GUI:**

This benchmark provides an interactive GUI that allows users to customise benchmark parameters.

*Describe: GUI overview, user-selectable params, benchmark execution.

**Input data characteristics:**

This benchmark assesses sorting algorithms performance on primitive integer arrays (`int[]`).

- *Max Performance:* Direct memory access, no boxing.
- *Core Algorithm Focus:* Isolates sorting efficiency.
- *In-Place Sorting:* Minimal memory use.

**Environment:**

Benchmarks run within a Docker container to minimise background process interference and ensure a consistent environment. The container is configured with default JVM settings and a single thread.

To minimise resource contention, the backend container is configured with:
* CPU limits: 2 cores
* Memory limits: 4GB
* CPU affinity: container process pinned to core
* Process priority: `renice`

For ease of use, both containers can be run on the same `Docker` daemon. However, for maximum benchmarking precision, a dedicated daemon for the backend container is recommended.

This project is built and tested with the following environment:

- *Operating System:* Ubuntu 24.04.1 LTS
- *Java:* OpenJDK 21.0.6
- *Gradle:* 8.13
- *JUnit:* 5.13
- *Mockito:* 5.17

### Benchmark Methodology

**Execution:**

**Data generation:**

**Measurement:**

- *Runtime:* Measured by profiling the sort operation's execution time.
- *Swaps:* Counted directly within the sorting algorithm's code during element swaps.
- *Memory:* JVM heap memory consumption, measured at key points during the sorting process.

**Algorithm implmementation:**

### Design

This benchmark project leverages the Strategy pattern to establish a modular and extensible architecture. Consequently, new sorting algorithms can be integrated without requiring modifications to the core benchmarking logic. Performance profiling is conducted by a dedicated `Benchmarker` class, which is injected into individual `SortStrategy` implementations, promoting loose coupling and enhanced code maintainability.

![Benchmark Class Structure](./docs/benchmark-class-structure.svg)


Pre-generated `BenchmarkData`, created upfront by the `BenchmarkDataFactory`, is repeatedly sorted by a chosen `SortStrategy` through the `Sorter`, and the resulting benchmark metrics are robustly aggregated.

![Benchmark Sequence Diagram](./docs/benchmark-sequence-diagram.svg)

### Test

This project features a robust suite of unit tests, built with JUnit and Mockito, to ensure the reliability and correctness of the benchmarking framework. The testing strategy rigorously applies principles such as boundary condition analysis, equivalence class partitioning, exception handling verification, and thorough data flow validation across components.

The current test suite achieves significant coverage, reaching **97% statement coverage** and **90% branch coverage**, demonstrating a strong commitment to code quality and comprehensive testing throughout the project.

**Running Tests:**

1.  Clone the repository (if you haven't already).
2.  Navigate to the project directory in your terminal or command prompt.
3.  Build the project (to ensure dependency resolution):
    ```bash
    ./gradlew build
    ```
4.  Run the Gradle test task:
    ```bash
    ./gradlew test
    ```

**Viewing Test Coverage:**

After the tests complete, an HTML report will be generated in the `build/reports/jacoco/testReport/html` directory. Open the `index.html` file in your web browser to explore the coverage details.

## Benchmark Results

- Strong positive correlation between runtime and perfored swaps.
- Swaps to verify theoretical complexity and inherent efficiency of algorithm. It is a hardware and environment-independent measure.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

Created by [René Lacher](https://github.com/rlacher).