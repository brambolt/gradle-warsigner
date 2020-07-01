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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.brambolt.util.jar.Manifests.isManifestVersion;
import static com.brambolt.util.jar.Manifests.isSignatureVersion;

public class AddAttributes implements Manifests {

    /**
     * The attributes to be added.
     */
    List<String> attributes;

    /**
     * Constructor.
     * @param attributes The attributes to be added
     */
    public AddAttributes(String... attributes) {
        this(Arrays.asList(attributes));
    }

    /**
     * Constructor.
     * @param attributes The attributes to be added
     */
    public AddAttributes(List<String> attributes) {
        this.attributes = attributes;
        validate(attributes);
    }

    /**
     * Validates the parameter attributes. The implementation is syntactic,
     * any attribute name is allowed.
     * @param attributes The attributes to be added
     * @throws IllegalArgumentException If any parameter appears invalid
     */
    public void validate(List<String> attributes) {
        List<String> invalid = attributes.stream()
            .filter(attribute -> !validate(attribute))
            .collect(Collectors.toList());
        if (!invalid.isEmpty())
            throw new IllegalArgumentException(
                "Invalid attributes:\n" + String.join("\n\t", invalid));
    }

    /**
     * Validates the formatting of the parameter attribute.
     * @param attribute The attribute to check
     * @return True if and only if the attribute appears valid, else false
     */
    public boolean validate(String attribute) {
        return null != attribute && attribute.contains(": ");
    }

    /**
     * Inserts the attributes into the manifest lines. The attributes will be
     * added after the <code>Manifest-Version</code> and
     * <code>Signature-Version</code> attributes, if they are present.
     * @param lines The lines of the manifest document to add to
     * @return The modified manifest lines with the attributes added
     */
    public List<String> apply(List<String> lines) {
        if (null == attributes || attributes.isEmpty())
            return lines;
        if (null == lines || lines.isEmpty())
            return lines;
        List<String> missing = attributes.stream()
            .filter(attribute -> !hasAttribute(lines, attribute))
            .collect(Collectors.toList());
        if (missing.isEmpty())
            return lines;
        int index = 0;
        if (isManifestVersion(lines.get(index)))
            ++index;
        if (isSignatureVersion(lines.get(index)))
            ++index;
        List<String> result = new ArrayList<>(lines.subList(0, index));
        result.addAll(missing);
        result.addAll(lines.subList(index, lines.size()));
        return result;
    }

    /**
     * Checks whether the parameter attribute appears in the parameter manifest
     * lines. Only the attribute name is checked. The attribute parameter can
     * be provided as an attribute name or a name-value pair. In the latter
     * case everything following the colon will be ignored when the check is
     * carried out.
     * @param lines The manifest lines to check
     * @param attribute The attribute to check for
     * @return True if and only if the manifest includes the attribute
     */
    public boolean hasAttribute(List<String> lines, String attribute) {
        String attributeName = attribute.substring(0, attribute.indexOf(":"));
        return lines.stream().anyMatch(l ->
            l.trim().toLowerCase().startsWith(attributeName.toLowerCase()));
    }

}
