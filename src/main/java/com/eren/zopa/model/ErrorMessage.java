package com.eren.zopa.model;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

public enum ErrorMessage {
    INSTANCE;

    // singleton pattern using enum
    public ErrorMessage getInstance() {
        return INSTANCE;
    }

    private List<String> errors = new ArrayList<>();

    public void addError(String message) {
        errors.add(message);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public String getErrors() {
        return this.errors.stream()
                .collect(joining(", "));
    }
}