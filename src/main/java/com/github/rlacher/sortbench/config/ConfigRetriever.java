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

package com.github.rlacher.sortbench.config;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for validating and retrieving configuration values from a map.
 *
 * <p>Expands on standard CLI argument validation with context-specific configuration checks.</p>
 */
public final class ConfigRetriever
{
    /** Private constructor to prevent instantiation of utility class. */
    @Deprecated
    private ConfigRetriever() {}

    /**
     * Validates and retrieves configuration values from a map based on a set of keys.
     *
     * <p>This method ignores the case and leading/trailing whitespaces of the keys.</p>
     *
     * <p>If a key is not found, it will be ignored unless {@code throwIfNotFound} is {@code true}.</p>
     *
     * @param config The map containing the configuration.
     * @param keys A set of keys to look for in the configuration map (case-insensitive).
     * @param throwIfNotFound If {@code true}, an exception is thrown if a key in {@code keys} is not found in the config.
     * @return A map of the found configuration values.
     * @throws IllegalArgumentException If {@code config} is {@code null}, {@code keys} is {@code null}, any key in {@code keys} is {@code null} or blank,
     * or if a key in {@code keys} is not found in the config and {@code throwIfNotFound} is {@code true}.
     */
    public static <T> Map<String, T> validateAndFilterMap(final Map<String, T> config, final Set<String> keys, final boolean throwIfNotFound)
    {
        if(config == null)
        {
            throw new IllegalArgumentException("Config map must not be null");
        }
        if(keys == null)
        {
            throw new IllegalArgumentException("Key set must not be null");
        }

        Map<String, T> foundValues = new java.util.HashMap<>();

        if(keys.isEmpty())
        {
            return foundValues;
        }

        final Map<String, String> caseInsensitiveConfigKeys = config.keySet().stream()
            .collect(Collectors.toMap(k -> k.toLowerCase(), k -> k));

        for (String key : keys)
        {
            if(key == null)
            {
                throw new IllegalArgumentException("Key in keys must not be null");
            }
            if(key.isBlank())
            {
                throw new IllegalArgumentException("Key in keys must not be blank");
            }

            String trimmedKey = key.trim();
            String matchedKey = caseInsensitiveConfigKeys.get(trimmedKey.toLowerCase());

            if(matchedKey != null)
            {
                T value = config.get(matchedKey);

                if(value == null)
                {
                    throw new IllegalArgumentException(String.format("Configuration value for key '%s' is null", matchedKey));
                }
                foundValues.put(matchedKey, value);
            }
            else if(throwIfNotFound)
            {
                throw new IllegalArgumentException(String.format("Configuration is missing required key: '%s'", trimmedKey));
            }
        }

        return foundValues;
    }
}
