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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Utility class for validating and retrieving configuration values from a map.
 */
public class ConfigValidator
{
    /** Private constructor to prevent instantiation of utility class. */
    private ConfigValidator() {}

    /**
     * Validates and retrieves a single configuration value from a map.
     *
     * @param config The map containing the configuration.
     * @param key The key of the configuration value to retrieve.
     * @param expectedType The expected class type of the configuration value.
     * @param <T> The type of the expected configuration value.
     * @return The validated and cast configuration value.
     * @throws IllegalArgumentException If any input is invalid: ({@code null} {@code config},
     * {@code null}/blank {@code key}, {@code null} {@code expectedType}), or if the {@code key}
     * is missing/{@code null} in {@code config}, or if the value is not of {@code expectedType}.
     */
    public static <T> T validateAndGet(Map<String, Object> config, String key, Class<T> expectedType)
    {
        if(config == null)
        {
            throw new IllegalArgumentException("Config must not be null");
        }
        if(key == null)
        {
            throw new IllegalArgumentException("Key must not be null");
        }
        if(key.isBlank())
        {
            throw new IllegalArgumentException("Key must not be blank");
        }
        if(expectedType == null)
        {
            throw new IllegalArgumentException("Expected type must not be null");
        }

        Object rawValue = config.get(key);

        if (rawValue == null)
        {
            throw new IllegalArgumentException(String.format("Configuration is missing key: '%s'", key));
        }

        if (!expectedType.isInstance(rawValue))
        {
            throw new IllegalArgumentException(String.format("Configuration key '%s' must be of type %s, but found: %s",
                                                            key, expectedType.getSimpleName(), rawValue.getClass().getSimpleName()));
        }

        // Cast to the now verified, expected type
        return expectedType.cast(rawValue);
    }

    /**
     * Validates and retrieves a collection of configuration values from a map.
     *
     * @param config The map containing the configuration.
     * @param key The key of the configuration collection to retrieve.
     * @param expectedElementType The expected class type of each element in the collection.
     * @param <T> The type of the elements in the expected collection.
     * @return The validated and cast collection of configuration values.
     * @throws IllegalArgumentException If {@code config} is {@code null}, {@code key} is {@code null} or blank,
     * {@code expectedElementType} is {@code null}, the {@code key} is missing in {@code config}, the value for
     * {@code key} is {@code null} or not a {@link Collection}, or the collection contains {@code null} elements or elements
     * not of {@code expectedElementType}.
     */
    public static <T> Collection<T> validateAndGetCollection(Map<String, Object> config, String key, Class<T> expectedElementType)
    {
        if(config == null)
        {
            throw new IllegalArgumentException("Config must not be null");
        }
        if(key == null)
        {
            throw new IllegalArgumentException("Key must not be null");
        }
        if(key.isBlank())
        {
            throw new IllegalArgumentException("Key must not be blank");
        }
        if(expectedElementType == null)
        {
            throw new IllegalArgumentException("Expected element type must not be null");
        }

        Object rawObject = config.get(key);

        if (rawObject == null)
        {
            throw new IllegalArgumentException(String.format("Config is missing key: '%s'", key));
        }

        if(!(rawObject instanceof Collection<?>))
        {
            throw new IllegalArgumentException(String.format("Configuration key '%s' must be of collection type, but found: %s",
                                                            key, rawObject.getClass().getSimpleName()));
        }

        // Cast to verified collection type
        Collection<?> rawCollection = (Collection<?>) rawObject;
        Collection<T>  validatedCollection = new ArrayList<>();
        for(Object element : rawCollection)
        {
            if(element == null)
            {
                throw new IllegalArgumentException(String.format("Collection '%s' contains a null element.", key));
            }
            if(!expectedElementType.isInstance(element))
            {
                throw new IllegalArgumentException(String.format("Elements in collection '%s' must be of type %s, but found incorrect type %s",
                                                                key, expectedElementType.getSimpleName(), element.getClass().getSimpleName()));
            }

            validatedCollection.add(expectedElementType.cast(element));
        }

        return validatedCollection;
    }
}
