package com.eren.noddus.protobufwriter.model;

public enum MessageType {
    USER("user"), ITEMS("items"), ITEM("item");
    private String type;

    MessageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
