package org.example.model;

public class Violation {
    private String message;
    private String violation;

    public Violation(String message, String violation) {
        this.message = message;
        this.violation = violation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getViolation() {
        return violation;
    }

    public void setViolation(String violation) {
        this.violation = violation;
    }
}
