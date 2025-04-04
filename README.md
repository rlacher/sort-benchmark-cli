# Sorting Algorithm Benchmark

This project compares the performance of various sorting algorithms. The algorithms are implemented in Java.

## Purpose

The goal is to analyse and visualise the efficiency of different common sorting algorithms under various conditions.

## Getting Started

1. Clone the respository

    ```bash
    git clone https://github.com/rlacher/sort-algorithms.git
    ```

2. Execute the application

    ```bash
    gradle clean && gradle run
    ```

3. Instructions on how to configure the analysis will follow

## Design

This benchmark project leverages the Strategy pattern to establish a modular and extensible architecture. Consequently, new sorting algorithms can be integrated without requiring modifications to the core benchmarking logic. Performance profiling is conducted by a dedicated `Benchmarker` class, which is injected into individual `SortStrategy` implementations, promoting loose coupling and enhanced code maintainability.

![Benchmark Class Structure](./docs/benchmark-class-structure.svg)


Pre-generated `BenchmarkData`, created upfront by the `BenchmarkDataFactory`, is repeatedly sorted by a chosen `SortStrategy` through the `Sorter`, and the resulting benchmark metrics are robustly aggregated.

![Benchmark Sequence Diagram](./docs/benchmark-sequence-diagram.svg)



## Results

More information to come.

## Environment

This project was built and tested with the following environment:

- *Operating System:* Ubuntu 24.04.1 LTS
- *Java:* OpenJDK 21.0.6
- *Gradle:* 8.13
- *JUnit:* 5.10

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
