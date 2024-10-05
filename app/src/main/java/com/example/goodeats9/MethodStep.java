package com.example.goodeats9;

public class MethodStep {
    private String stepNumber;
    private String description;

    public MethodStep(String stepNumber, String description) {
        this.stepNumber = stepNumber;
        this.description = description;
    }

    public String getStepNumber() {
        return stepNumber;
    }

    public String getDescription() {
        return description;
    }
}
