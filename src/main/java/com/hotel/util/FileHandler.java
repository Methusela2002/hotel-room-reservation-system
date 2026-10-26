package com.hotel.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Central place for all text file read/write operations.
 * Every method is synchronized on one lock so two requests
 * cannot corrupt a file by writing at the same time.
 */
public final class FileHandler {

    private static final Object LOCK = new Object();

    private FileHandler() {
    }

    public static Path resolve(String fileName) {
        return Paths.get(DataConfig.getDataDir(), fileName);
    }

    // Creates the data folder and the file if they do not exist yet
    public static void ensureFileExists(String fileName) throws IOException {
        synchronized (LOCK) {
            Path path = resolve(fileName);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        }
    }

    // READ: returns every non-empty line of the file
    public static List<String> readAllLines(String fileName) throws IOException {
        synchronized (LOCK) {
            ensureFileExists(fileName);
            List<String> lines = new ArrayList<>();
            for (String line : Files.readAllLines(resolve(fileName), StandardCharsets.UTF_8)) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
            return lines;
        }
    }

    // CREATE: adds one line to the end of the file
    public static void appendLine(String fileName, String line) throws IOException {
        synchronized (LOCK) {
            ensureFileExists(fileName);
            Files.write(resolve(fileName),
                    Collections.singletonList(line),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND);
        }
    }

    // UPDATE / DELETE: replaces the whole file content.
    // Writes to a temp file first, then swaps it in, so a crash
    // half-way through cannot leave a broken data file.
    public static void writeAllLines(String fileName, List<String> lines) throws IOException {
        synchronized (LOCK) {
            ensureFileExists(fileName);
            Path target = resolve(fileName);
            Path temp = target.resolveSibling(fileName + ".tmp");
            Files.write(temp, lines, StandardCharsets.UTF_8);
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}