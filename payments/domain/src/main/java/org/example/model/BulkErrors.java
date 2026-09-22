package org.example.model;

import java.util.List;

public record BulkErrors(int rowNumber, List<String> errors) {

}
