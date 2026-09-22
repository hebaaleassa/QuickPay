package org.example.useCases;

import java.nio.file.Path;

public record UploadBulkCommand(Path file, String fileName) {
    public UploadBulkCommand {
        if (file == null) {
            throw new IllegalArgumentException();
        }
    }
}
