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
