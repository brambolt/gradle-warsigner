package com.brambolt.util.jar;

import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.taskdefs.Expand;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static com.brambolt.nio.file.ZipFileSystems.unzip;

/**
 * Checks that a WAR file has expected signing data and manifest attributes.
 */
public class CheckWar {

    /**
     * The jar checker utility used for individual application jars.
     */
    private final CheckJar check;

    /**
     * Constructor. No attributes will be checked.
     * @param signatureFileNamePrefix The file name prefix to check for
     */
    public CheckWar(String signatureFileNamePrefix) {
        this(signatureFileNamePrefix, Collections.emptyList());
    }

    /**
     * Constructor. Signing data will not be checked.
     * @param attributes The attributes to check for
     */
    public CheckWar(List<String> attributes) {
        this(null, attributes);
    }

    /**
     * Constructor.
     * @param signatureFileNamePrefix The signing data file name prefix to check
     * @param attributes The attributes to check for
     */
    public CheckWar(String signatureFileNamePrefix, List<String> attributes) {
        this.check = new CheckJar(signatureFileNamePrefix, attributes);
    }

    /**
     * Checks the parameter WAR file.
     * @param warFile The WAR file to check
     * @return The WAR file, if checking succeeded
     * @throws IllegalStateException If signing data or attributes are missing
     * @throws IOException If unable to check
     */
    public File apply(File warFile) throws IOException {
        File tmpDir = null;
        try {
            tmpDir = expand(warFile);
            Files.walk(new File(tmpDir, "application-jars").toPath())
                .map(Path::toFile)
                .filter(file -> file.getName().endsWith(".jar"))
                .forEach(this::checkJarFile);
        } finally {
            delete(tmpDir);
        }
        return warFile;
    }

    private File expand(File warFile) throws IOException {
        File tmpDir = Files.createTempDirectory(warFile.getName()).toFile();
        Expand expand = new Expand();
        expand.setSrc(warFile);
        expand.setDest(tmpDir);
        expand.execute();
        return tmpDir;
    }

    private void delete(File tmpDir) {
        if (null == tmpDir || !tmpDir.exists() || !tmpDir.isDirectory())
            return; // Nothing to do
        Delete delete = new Delete();
        delete.setDir(tmpDir);
        delete.execute();
    }

    private void checkJarFile(File jarFile) {
        try (FileSystem jarFs = unzip(jarFile)) {
            check.apply(jarFs);
        } catch (IOException x) {
            throw new RuntimeException(x);
        }
    }

    private FileSystem apply(FileSystem fs) throws IOException {
        check(fs);
        return fs;
    }

    private void check(FileSystem fs) throws IOException {
        check.apply(fs);
    }
}
