package org.example.model;

import java.util.List;
import java.util.Map;

public record BulkResult(int total, int success, int failure,
                         Map<Integer, List<String>> rowErrors, List<Payment>  payments) {


}
