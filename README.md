# Sorting Algorithm Benchmark

This project compares the performance of various sorting algorithms. The algorithms are implemented in Java.

## Purpose

The goal is to analyse and visualise the efficiency of different common sorting algorithms under various conditions.

## Key Features

- Benchmarking of multiple sorting algorithms
- Configurable test data generation
- *To be expanded*

## Getting Started

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

## Results

More information to come.

## Test

This project features a robust suite of unit tests, built with JUnit and Mockito, to ensure the reliability and correctness of the benchmarking framework. The testing strategy rigorously applies principles such as boundary condition analysis, equivalence class partitioning, exception handling verification, and thorough data flow validation across components.

The current test suite achieves significant coverage, reaching **97% statement coverage** and **89% branch coverage**, demonstrating a strong commitment to code quality and comprehensive testing throughout the project.

### Running Tests

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

### Viewing Test Coverage Report

After the tests complete, an HTML report will be generated in the `build/reports/jacoco/testReport/html` directory. Open the `index.html` file in your web browser to explore the coverage details.

## Design

This benchmark project leverages the Strategy pattern to establish a modular and extensible architecture. Consequently, new sorting algorithms can be integrated without requiring modifications to the core benchmarking logic. Performance profiling is conducted by a dedicated `Benchmarker` class, which is injected into individual `SortStrategy` implementations, promoting loose coupling and enhanced code maintainability.

![Benchmark Class Structure](./docs/benchmark-class-structure.svg)


Pre-generated `BenchmarkData`, created upfront by the `BenchmarkDataFactory`, is repeatedly sorted by a chosen `SortStrategy` through the `Sorter`, and the resulting benchmark metrics are robustly aggregated.

![Benchmark Sequence Diagram](./docs/benchmark-sequence-diagram.svg)

## Environment

This project was built and tested with the following environment:

- *Operating System:* Ubuntu 24.04.1 LTS
- *Java:* OpenJDK 21.0.6
- *Gradle:* 8.13
- *JUnit:* 5.13
- *Mockito:* 5.17

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Author

Created by [René Lacher](https://github.com/rlacher).