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

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.Collections;
import java.util.List;

import static com.brambolt.nio.file.ZipFileSystems.unzip;

public class CheckJar {

    private final String signatureFileNamePrefix;

    private final List<String> attributes;

    public CheckJar(String signatureFileNamePrefix) {
        this(signatureFileNamePrefix, Collections.emptyList());
    }

    public CheckJar(List<String> attributes) {
        this(null, attributes);
    }

    public CheckJar(String signatureFileNamePrefix, List<String> attributes) {
        this.signatureFileNamePrefix = signatureFileNamePrefix;
        this.attributes = attributes;
    }

    public File apply(File file) throws IOException {
      try (FileSystem fs = unzip(file)) { apply(fs); }
      return file;
    }

    public FileSystem apply(FileSystem fs) throws IOException {
        check(fs);
        return fs;
    }

    public void check(FileSystem fs) throws IOException {
        checkAttributes(fs);
        checkSigning(fs);
    }

    public void checkAttributes(FileSystem fs) throws IOException {
        for (String attribute: attributes)
            new CheckAttributes(attribute).apply(fs);
    }

    public void checkSigning(FileSystem fs) throws IOException {
        if (null != signatureFileNamePrefix && !signatureFileNamePrefix.isEmpty())
            new CheckSigningData(signatureFileNamePrefix).apply(fs);
    }
}
