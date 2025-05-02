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

package com.github.rlacher.sortbench.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.github.rlacher.sortbench.results.BenchmarkContext;
import com.github.rlacher.sortbench.results.AggregatedResult;

/**
 * Formats a list of {@link AggregatedResult} into an ASCII table.
 */
public class AsciiTableFormatter
{
    /** Static definition of column titles for the title row. */
    private static final List<String> COLUMN_TITLES = List.of(
        "Algorithm", "Data Type", "Data Size", "Profiling Mode", "Aggregate", "Iterations");

    /** The number of decimal digits for floating-point number formatting. */
    private static final int FLOATING_DECIMAL_DIGITS = 2;

    /** Character used in the line to separate the title from the data rows. */
    private final char titleRowDelimiter;

    /** Character to separate the columns. */
    private final char columnDelimiter;

    /** Defines number formatting (i.e. grouping, fractional digits). */
    private NumberFormat numberFormatter;

    /**
     * Parameterless constructor initialising class fields to defaults (i.e. delimiters, number formatter).
     */
    public AsciiTableFormatter()
    {
        this(Locale.getDefault(), '-', '|');
    }

    /**
     * Overloaded constructor initialising class fields with provided {@link Locale} and delimiters.
     * 
     * @param locale The {@link Locale} to use for number formatting. Must not be {@code null}.
     * @param titleRowDelimiter The character to use for the title row delimiter.
     * @param columnDelimiter The character to use for the column delimiter.
     * @throws IllegalArgumentException If {@code locale} is {@code null} or if the delimiters are not printable ASCII characters.
     */
    public AsciiTableFormatter(final Locale locale, final char titleRowDelimiter, final char columnDelimiter)
    {
        if(locale == null)
        {
            throw new IllegalArgumentException("Locale must not be null");
        }
        if(!isPrintableAscii(columnDelimiter))
        {
            throw new IllegalArgumentException("Column delimiter must be a printable ASCII character");
        }
        if(!isPrintableAscii(titleRowDelimiter))
        {
            throw new IllegalArgumentException("Title row delimiter must be a printable ASCII character");
        }

        this.numberFormatter = NumberFormat.getNumberInstance(locale);
        this.numberFormatter.setGroupingUsed(true);
        this.titleRowDelimiter = titleRowDelimiter;
        this.columnDelimiter = columnDelimiter;
    }

    /**
     * Entry point for formatting a list of {@link AggregatedResult} into an ASCII table.
     *
     * <p>Uses the delimiters and number formatter defined in the constructor.</p>
     *
     * @param results The list of {@link AggregatedResult} to format.
     * @throws IllegalArgumentException If the results list is {@code null} or empty.
     * @return The formatted ASCII table as a {@link String}.
     */
    public String format(final List<AggregatedResult> results)
    {
        if(results == null)
        {
            throw new IllegalArgumentException("Results must not be null");
        }

        if(results.isEmpty())
        {
            return "No benchmark results to format.";
        }

        final Map<String, List<String>> resultsStringMap = convertResultsToStringMap(results);
        final List<Boolean> variableColumns = calculateColumnVariability(resultsStringMap);
        final List<Integer> maxColumnWidths = calculateMaxColumnWidths(resultsStringMap);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format("Benchmark results (invariant parameters above table):%n"));
        formatInvariantParameters(resultsStringMap, variableColumns, stringBuilder);
        formatVariableParameterTable(resultsStringMap, variableColumns, maxColumnWidths, stringBuilder);

        return stringBuilder.toString();
    }

    /**
     * Converts a list of {@link AggregatedResult} into a map of strings for formatting.
     *
     * @param results The list of {@link AggregatedResult} to convert. Must not be {@code null}.
     * @return A map of strings representing the results, with column titles as keys and lists of strings as values.
     */
    private Map<String, List<String>> convertResultsToStringMap(final List<AggregatedResult> results)
    {
        // Maintain order of columns through linked hash map
        Map<String, List<String>> stringMap = new LinkedHashMap<>();

        final List<BenchmarkContext> contexts = results.stream().map(result -> result.getContext()).toList();
        final List<String> algorithmStrings = contexts.stream().map(context -> context.getSortStrategyName()).toList();
        final List<String> dataTypeStrings = contexts.stream().map(context -> context.getDataType().toString()).toList();
        final List<String> dataLengthStrings = contexts.stream().map(context -> formatNumber(context.getDataLength())).toList();
        final List<String> profilingModeStrings = results.stream().map(result -> result.getProfilingMode().toString()).toList();
        final List<String> aggregateStrings = results.stream().map(result -> formatNumber(result.getAggregate())).toList();
        final List<String> iterationsStrings = results.stream().map(result -> formatNumber(result.getIterations())).toList();

        stringMap.put(COLUMN_TITLES.get(0), algorithmStrings);
        stringMap.put(COLUMN_TITLES.get(1), dataTypeStrings);
        stringMap.put(COLUMN_TITLES.get(2), dataLengthStrings);
        stringMap.put(COLUMN_TITLES.get(3), profilingModeStrings);
        stringMap.put(COLUMN_TITLES.get(4), aggregateStrings);
        stringMap.put(COLUMN_TITLES.get(5), iterationsStrings);

        return stringMap;
    }

    /**
     * Calculates the variability of each column in the results map.
     *
     * <p>Columns with only one distinct value are considered invariant.</p>
     *
     * @param stringMap The map of strings representing the results, with column titles as keys and lists of strings as values. Must not be {@code null}.
     * @return A list of booleans indicating whether each column is variable ({@code true}) or invariant ({@code false}).
     */
    private List<Boolean> calculateColumnVariability(final Map<String, List<String>> stringMap)
    {
        List<Boolean> columnVariability = new ArrayList<>();

        for(int i = 0; i < COLUMN_TITLES.size(); ++i)
        {
            final List<String> columnResults = stringMap.get(COLUMN_TITLES.get(i));
            final int dinstinctValues = (int) columnResults.stream().distinct().count();
            columnVariability.add(dinstinctValues > 1);
        }

        return columnVariability;
    }

    /**
     * Calculates the maximum width of each column in the results map.
     *
     * @param stringMap The map of strings representing the results, with column titles as keys and lists
     * of strings as values. Must not be {@code null}.
     * @return A list of integers representing the maximum width of each column. The width is determined
     * by the longest string in the column or the length of the column title, whichever is greater.
     */
    private List<Integer> calculateMaxColumnWidths(final Map<String, List<String>> stringMap)
    {
        List<Integer> maxColumnWidths = new ArrayList<>();

        for(int i = 0; i < COLUMN_TITLES.size(); ++i)
        {
            final String columnTitle = COLUMN_TITLES.get(i);
            final List<String> columnResults = stringMap.get(columnTitle);
            final int maxColumnWidth = columnResults.stream().mapToInt(String::length).max().orElse(0);
            maxColumnWidths.add(Math.max(maxColumnWidth, columnTitle.length()));
        }

        return maxColumnWidths;
    }

    /**
     * Formats the invariant parameters of the results map into a string.
     * 
     * <p>Invariant parameters are formatted as "Column Title: Value" pairs.</p>
     * @param stringMap The map of strings representing the results, with column titles as keys and lists
     * of strings as values. Must not be {@code null}.
     * @param variableColumns A list of booleans indicating whether each column is variable ({@code true}) or invariant ({@code false}).
     * @param stringBuilder The string builder to append the formatted invariant parameters to. Must not be {@code null}.
     */
    private void formatInvariantParameters(final Map<String, List<String>> stringMap, final List<Boolean> variableColumns, StringBuilder stringBuilder)
    {
        for(int i = 0; i < variableColumns.size(); ++i)
        {
            if(!variableColumns.get(i))
            {
                String columnTitle = COLUMN_TITLES.get(i);
                stringBuilder.append(String.format("%s: %s%n", columnTitle, stringMap.get(columnTitle).getFirst()));
            }
        }
    }

    /**
     * Formats the variable parameters of the results map into a table.
     * 
     * <p>Variable parameters are formatted as a table with the column titles as headers.</p>
     * 
     * @param stringMap The map of strings representing the results, with column titles as keys and lists
     * of strings as values. Must not be {@code null}.
     * @param variableColumns A list of booleans indicating whether each column is variable ({@code true}) or invariant ({@code false}).
     * @param maxColumnWidths A list of integers representing the maximum width of each column.
     * @param stringBuilder The string builder to append the formatted invariant parameters to. Must not be {@code null}.
     */
    private void formatVariableParameterTable(final Map<String, List<String>> stringMap, final List<Boolean> variableColumns, final List<Integer> maxColumnWidths, StringBuilder stringBuilder)
    {
        if(COLUMN_TITLES.size() != stringMap.size())
        {
            throw new IllegalStateException("Number of column titles must match number of entries in results map for table formatting.");
        }

        final List<Integer> numResults = stringMap.values().stream().mapToInt(List::size).boxed().toList();
        if(numResults.stream().distinct().count() != 1)
        {
            throw new IllegalStateException("All result columns must have the same size for table formatting.");
        }

        stringBuilder.append(System.lineSeparator());

        final int numRows = numResults.getFirst(); // Safe to get first
        if(numRows < 2)
        {
            stringBuilder.append(String.format("Table formatting requires at least two results.%n"));
            return;
        }

        // Header row
        for(int i = 0; i < variableColumns.size(); ++i)
        {
            if(variableColumns.get(i))
            {
                stringBuilder.append(String.format("%c %" + maxColumnWidths.get(i) + "s ", columnDelimiter, COLUMN_TITLES.get(i)));
            }
        }
        stringBuilder.append(String.format("%c%n", columnDelimiter));

        // Separator row
        for(int i = 0; i < variableColumns.size(); ++i)
        {
            if(variableColumns.get(i))
            {
                stringBuilder.append(String.format("%c%s", columnDelimiter, String.valueOf(titleRowDelimiter).repeat(maxColumnWidths.get(i) + 2)));
            }
        }
        stringBuilder.append(String.format("%c%n", columnDelimiter));

        // Data rows
        for(int row = 0; row < numRows; ++row)
        {
            for(int i = 0; i < variableColumns.size(); ++i)
            {
                if(variableColumns.get(i))
                {
                    stringBuilder.append(String.format("%c %" + maxColumnWidths.get(i) + "s ", columnDelimiter, stringMap.get(COLUMN_TITLES.get(i)).get(row)));
                }
            }
            stringBuilder.append(String.format("%c%n", columnDelimiter));
        }
    }

    /**
     * Formats a number into a string using the defined number formatter.
     *
     * <p>Sets the minimum and maximum fraction digits for floating point and integer numbers dynamically.</p>
     *
     * @param number The number to format. Must not be {@code null}.
     * @return The formatted number as a string.
     * @throws IllegalArgumentException If the number is {@code null}.
     */
    private String formatNumber(final Number number)
    {
        if(number == null)
        {
            throw new IllegalArgumentException("Number must not be null");
        }

        if(number instanceof Double || number instanceof Float)
        {
            numberFormatter.setMinimumFractionDigits(FLOATING_DECIMAL_DIGITS);
            numberFormatter.setMaximumFractionDigits(FLOATING_DECIMAL_DIGITS);
        }
        else
        {
            numberFormatter.setMaximumFractionDigits(0);
        }

        return numberFormatter.format(number);
    }

    /**
     * Checks if a character is a printable ASCII character.
     * @param c The character to check.
     * @return {@code true} if the character is a printable ASCII character, {@code false} otherwise.
     */
    private boolean isPrintableAscii(final char c)
    {
        return c >= 32 && c <= 126;
    }
}
