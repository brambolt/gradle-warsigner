/*
 * Copyright 2017-2020 Brambolt ehf.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brambolt.util.jar;

import org.gradle.api.GradleException;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.brambolt.util.jar.Manifests.hasAttribute;
import static com.brambolt.util.jar.Manifests.readAllLines;

/**
 * Checks whether a manifest contains the provided attributes.
 */
public class CheckAttributes implements Manifests {

    /**
     * The attributes to check for.
     */
    public final List<String> attributes;

    /**
     * Constructor.
     * @param attributes The attributes to check for
     */
    public CheckAttributes(String... attributes) {
        this(Arrays.asList(attributes));
    }

    /**
     * Constructor.
     * @param attributes The attributes to check for
     */
    public CheckAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    /**
     * Checks whether the parameter jar manifest has the attributes.
     * @param fs The jar file system to check
     * @throws IllegalStateException If one or more attributes are missing
     * @throws IOException If unable to check the manifest
     */
    @Override
    public void apply(FileSystem fs) throws IOException {
        throwIf(fs);
    }

    /**
     * Checks whether the parameter jar manifest has the attributes.
     * @param fs The jar file system to check
     * @throws IllegalStateException If one or more attributes are missing
     * @throws IOException If unable to check the manifest
     */
    public void throwIf(FileSystem fs) throws GradleException, IOException {
        throwIf(readAllLines(fs));
    }


    /**
     * Checks whether the parameter jar manifest has the attributes.
     * @param lines The jar manifest lines to check
     * @throws IllegalStateException If one or more attributes are missing
     */
    public void throwIf(List<String> lines) throws IllegalStateException {
        List<String> missing = attributes.stream()
            .filter(a -> !hasAttribute(lines, a))
            .collect(Collectors.toList());
        if (!missing.isEmpty())
            throw new IllegalStateException(
                String.format("Missing attribute: %s", String.join(" ", missing)));
    }
}

